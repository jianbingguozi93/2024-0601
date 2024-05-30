package com.eden.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.eden.model.User;
import com.eden.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("userId") // 声明要存储在会话中的属性
public class UserController {

	@Autowired
	private UserService userService;

	// 处理用户注册请求
	@PostMapping("/register")
	public String registerUser(@RequestParam String username, @RequestParam String password,
			@RequestParam String email, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String firstNameKana,
			@RequestParam String lastNameKana, @RequestParam String gender,
			@RequestParam String address, @RequestParam String phone, Model model) {
		try {
			userService.registerUser(username, password, email, firstName, lastName, firstNameKana, lastNameKana,
					gender, address, phone); // 调用服务层方法注册用户
			return "redirect:/login-register"; // 注册成功后重定向到合并页面
		} catch (Exception e) {
			model.addAttribute("registrationError", e.getMessage());
			return "login-register"; // 注册失败，重新显示合并页面并显示错误信息
		}
	}

	// 处理用户登录请求
	@PostMapping("/login")
	public String loginUser(@RequestParam("usernameOrEmail") String usernameOrEmail,
			@RequestParam String password, HttpSession session, Model model) {
		try {
			userService.loginUser(usernameOrEmail, password);
			User user = userService.getCurrentUser(usernameOrEmail); // 获取当前登录用户的信息
			session.setAttribute("userId", user.getId()); // 将 userId 添加到会话中
			return "redirect:/"; // 登录成功后重定向到主页
		} catch (Exception e) {
			model.addAttribute("loginError", e.getMessage());
			return "login-register"; // 登录失败，重新显示合并页面
		}
	}

	// 注销用户
	@GetMapping("/logout")
	public String logout(HttpSession session, SessionStatus sessionStatus) {
		session.invalidate(); // 清除会话
		sessionStatus.setComplete(); // 标记会话完成
		return "redirect:/"; // 注销后重定向到主页
	}

	// 显示和编辑用户信息页面
	@GetMapping("/profile")
	public String showUserProfile(@SessionAttribute("userId") Long userId, Model model) {
		User user = userService.getCurrentUser(userId);
		model.addAttribute("user", user);
		return "profile"; // 返回用户信息页面的模板名称
	}

	// 处理更新用户信息请求
	@PostMapping("/updateProfile")
	public String updateUserProfile(@SessionAttribute("userId") Long userId, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String firstNameKana, @RequestParam String lastNameKana,
			@RequestParam String email, @RequestParam String phone) {
		User user = userService.getCurrentUser(userId);
		userService.updateUserProfile(user, firstName, lastName, firstNameKana, lastNameKana, email, phone); // 调用服务层方法更新用户信息
		return "redirect:/profile"; // 更新成功后重定向到用户信息页面
	}

	// 显示找回密码页面
	@GetMapping("/resetPasswordRequest")
	public String showResetPasswordRequestForm(Model model) {
		return "resetPasswordRequest"; // 返回找回密码页面的模板名称
	}

	// 处理找回密码请求
	@PostMapping("/resetPasswordRequest")
	public String resetPassword(@RequestParam String email, Model model) {
		try {
			userService.resetPassword(email); // 调用服务层方法发送找回密码邮件
			model.addAttribute("message", "パスワードリセットメールが送信されました");
		} catch (Exception e) {
			model.addAttribute("resetPasswordError", e.getMessage());
		}
		return "redirect:/"; // 找回密码成功后返回主页
	}

	// 显示重置密码表单页面
	@GetMapping("/resetPasswordForm")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		User user = userService.getUserByResetToken(token);
		if (user == null) {
			model.addAttribute("error", "無効なトークンです。");
			return "resetPasswordRequest"; // 返回到找回密码页面
		}
		model.addAttribute("token", token);
		return "resetPasswordForm"; // 返回重置密码表单页面
	}

	// 处理重置密码请求
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam("token") String token, @RequestParam("password") String newPassword,
			Model model) {
		User user = userService.getUserByResetToken(token);
		if (user == null) {
			model.addAttribute("error", "無効なトークンです。");
			return "resetPasswordForm";
		}
		try {
			userService.updatePassword(user, newPassword);
			return "redirect:/login-register"; // 重置成功后重定向到登录页面
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "resetPasswordForm";
		}
	}

	/**
	 * 处理上传头像的请求
	 * @param userId 用户ID
	 * @param avatarFile 上传的头像文件
	 * @param model 用于传递数据到视图
	 * @return 重定向到用户详情页面
	 */
	@PostMapping("/uploadAvatar")
	public String uploadAvatar(Long userId, MultipartFile avatarFile, Model model) {
		try {
			userService.uploadAvatar(userId, avatarFile);
		} catch (IOException e) {
			model.addAttribute("error", "アバターのアップロードに失敗しました：" + e.getMessage());
			return "user/profile"; // 假设用户详情页为 "user/profile"
		}
		return "redirect:/user/profile"; // 重定向到用户详情页
	}
}
