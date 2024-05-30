package com.eden.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eden.model.Gender;
import com.eden.model.User;
import com.eden.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.avatar.directory}")
	private String avatarDirectory;

	@Value("${app.default-avatar-male}")
	private String defaultAvatarMale;

	@Value("${app.default-avatar-female}")
	private String defaultAvatarFemale;

	@Value("${app.default-avatar-other}")
	private String defaultAvatarOther;

	private static final int MAX_FAILED_ATTEMPTS = 5; // 最大登录失败次数

	// 注册用户方法
	public void registerUser(String username, String password, String email, String firstName, String lastName,
			String firstNameKana, String lastNameKana, String gender, String address, String phone) throws Exception {
		if (!isUsernameValid(username)) {
			throw new IllegalArgumentException("ユーザー名の形式が正しくありません");
		}
		if (userRepository.findByUsername(username) != null) {
			throw new IllegalArgumentException("ユーザー名はすでに存在しています");
		}
		if (!isPasswordValid(password)) {
			throw new IllegalArgumentException("パスワードの形式が正しくありません");
		}
		if (userRepository.findByEmail(email) != null) {
			throw new IllegalArgumentException("メールアドレスはすでに存在しています");
		}
		if (!isEmailValid(email)) {
			throw new IllegalArgumentException("メールアドレスの形式が正しくありません");
		}
		if (userRepository.findByPhone(phone) != null) {
			throw new IllegalArgumentException("電話番号はすでに存在しています");
		}
		if (!isPhoneValid(phone)) {
			throw new IllegalArgumentException("電話番号の形式が正しくありません");
		}
		if (!isKanaValid(firstNameKana)) {
			throw new IllegalArgumentException("姓のカタカナの形式が正しくありません");
		}
		if (!isKanaValid(lastNameKana)) {
			throw new IllegalArgumentException("名のカタカナの形式が正しくありません");
		}

		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password)); // 密码加密
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setFirstNameKana(firstNameKana);
		user.setLastNameKana(lastNameKana);
		user.setGender(Gender.valueOf(gender));
		user.setAddress(address);
		user.setPhone(phone);
		user.setAvatar(getDefaultAvatar(gender)); // 设置默认头像
		userRepository.save(user); // 保存用户到数据库
	}

	// 检查用户名是否有效
	private boolean isUsernameValid(String username) {
		return username.matches("^[a-zA-Z0-9_]{1,10}$") && !username.startsWith("_") && !username.endsWith("_")
				&& !username.contains(" ");
	}

	// 检查密码是否有效
	private boolean isPasswordValid(String password) {
		return password.matches("^[a-zA-Z0-9]{8,}$");
	}

	// 检查邮箱是否有效
	private boolean isEmailValid(String email) {
		return email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
	}

	// 检查电话号码是否有效
	private boolean isPhoneValid(String phone) {
		return phone.matches("^[0-9]{10,15}$");
	}

	// 检查假名是否有效
	private boolean isKanaValid(String kana) {
		return kana.matches("^[\\u30A0-\\u30FF]+$");
	}

	// 用户登录方法
	public boolean loginUser(String usernameOrEmail, String password) throws Exception {
		User user = userRepository.findByUsername(usernameOrEmail);
		if (user == null) {
			user = userRepository.findByEmail(usernameOrEmail);
		}

		if (user == null) {
			throw new IllegalArgumentException("ユーザー名またはメールアドレスが見つかりません");
		}

		if (isAccountLocked(user)) {
			throw new IllegalArgumentException("アカウントがロックされています。5分後に再試行してください。");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			increaseFailedAttempts(user);
			int attemptsLeft = MAX_FAILED_ATTEMPTS - user.getFailedAttempts();
			if (attemptsLeft > 0) {
				throw new IllegalArgumentException("ユーザー名またはパスワードが間違っています。あと " + attemptsLeft + " 回の試行でアカウントがロックされます。");
			} else {
				lockAccount(user);
				throw new IllegalArgumentException("アカウントがロックされました。5分後に再試行してください。");
			}
		}

		resetFailedAttempts(user);
		return true;
	}

	// 增加登录失败次数
	private void increaseFailedAttempts(User user) {
		int newFailedAttempts = user.getFailedAttempts() + 1;
		user.setFailedAttempts(newFailedAttempts);
		userRepository.save(user);
	}

	// 重置登录失败次数
	private void resetFailedAttempts(User user) {
		user.setFailedAttempts(0);
		user.setLockTime(null);
		userRepository.save(user);
	}

	// 锁定账户
	private void lockAccount(User user) {
		user.setLockTime(LocalDateTime.now());
		userRepository.save(user);
	}

	// 解锁账户
	private void unlockAccount(User user) {
		user.setLockTime(null);
		user.setFailedAttempts(0);
		userRepository.save(user);
	}

	// 检查账户是否被锁定
	private boolean isAccountLocked(User user) {
		if (user.getLockTime() == null) {
			return false;
		}
		LocalDateTime lockTime = user.getLockTime();
		LocalDateTime now = LocalDateTime.now();
		if (lockTime.plusMinutes(5).isBefore(now)) {
			unlockAccount(user);
			return false;
		}
		return true;
	}

	// 发送找回密码邮件方法
	public void resetPassword(String email) throws Exception {
		// 根据邮箱查找用户
		User user = userRepository.findByEmail(email);
		if (user != null) {
			// 生成重置密码的token
			String token = UUID.randomUUID().toString();
			user.setResetToken(token);
			user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // token有效期为1小时
			userRepository.save(user);

			// 发送重置密码邮件
			sendResetPasswordEmail(user.getEmail(), token);
		} else {
			throw new IllegalArgumentException("メールアドレスが見つかりません");
		}
	}

	// 发送重置密码邮件
	private void sendResetPasswordEmail(String email, String token) {
		// 生成邮件内容
		String subject = "パスワードリセットのリクエスト";
		String resetUrl = "http://localhost:8081/resetPasswordForm?token=" + token;
		String message = "パスワードをリセットするには、次のリンクをクリックしてください:\n" + resetUrl;

		// 创建邮件消息
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);

		// 发送邮件
		mailSender.send(mailMessage);
	}

	// 根据重置密码的token获取用户
	public User getUserByResetToken(String token) {
		return userRepository.findByResetToken(token);
	}

	// 更新密码方法
	public void updatePassword(User user, String newPassword) throws Exception {
		if (!isPasswordValid(newPassword)) {
			throw new IllegalArgumentException("パスワードの形式が正しくありません");
		}
		user.setPassword(passwordEncoder.encode(newPassword)); // 密码加密
		user.setResetToken(null);
		user.setResetTokenExpiry(null);
		userRepository.save(user);
	}

	// 检查token是否有效
	public boolean isResetTokenValid(User user) {
		return user.getResetTokenExpiry().isAfter(LocalDateTime.now());
	}

	// 根据用户名或邮箱获取当前登录用户信息
	public User getCurrentUser(String usernameOrEmail) {
		User user = userRepository.findByUsername(usernameOrEmail);
		if (user == null) {
			user = userRepository.findByEmail(usernameOrEmail);
		}
		return user;
	}

	// 根据用户ID获取当前登录用户信息
	public User getCurrentUser(Long userId) {
		return userRepository.findById(userId).orElse(null);
	}

	// 更新用户信息方法
	public void updateUserProfile(User user, String firstName, String lastName, String firstNameKana,
			String lastNameKana, String email, String phone) {
		if (!isKanaValid(firstNameKana)) {
			throw new IllegalArgumentException("姓のカタカナの形式が正しくありません");
		}
		if (!isKanaValid(lastNameKana)) {
			throw new IllegalArgumentException("名のカタカナの形式が正しくありません");
		}
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setFirstNameKana(firstNameKana);
		user.setLastNameKana(lastNameKana);
		user.setEmail(email);
		user.setPhone(phone);
		userRepository.save(user); // 更新用户信息到数据库
	}

	// 选择默认头像
	private String getDefaultAvatar(String gender) {
		switch (gender) {
		case "男性":
			return defaultAvatarMale;
		case "女性":
			return defaultAvatarFemale;
		default:
			return defaultAvatarOther;
		}
	}

	// 上传头像方法
	public void uploadAvatar(Long userId, MultipartFile avatar) throws IOException {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null && !avatar.isEmpty()) {
			// 检查文件类型
			if (!isImageFile(avatar)) {
				throw new IOException("画像ファイルのみをアップロードできます。");
			}

			// 保存头像文件到本地文件系统
			byte[] bytes = avatar.getBytes();
			Path path = Paths.get(avatarDirectory, userId + "_" + avatar.getOriginalFilename());
			Files.write(path, bytes);

			// 更新用户的头像路径
			user.setAvatar(path.toString());
			userRepository.save(user);
		} else {
			throw new IOException("アバターのアップロードに失敗しました。");
		}
	}

	// 检查文件是否为图片文件
	private boolean isImageFile(MultipartFile file) {
		String contentType = file.getContentType();
		return contentType != null && (contentType.startsWith("image/png") || contentType.startsWith("image/jpeg")
				|| contentType.startsWith("image/gif"));
	}
}
