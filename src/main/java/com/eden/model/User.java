package com.eden.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User实体类映射数据库中的users表。
 * 该类包含用户的基本信息，如用户名、密码、邮箱等。
 */
@Entity(name = "User") // 确保实体名称唯一
@Table(name = "users")
public class User {

    /**
     * 用户ID，主键，自动生成。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 用于指定主键字段的生成策略
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 用户名，唯一且不能为空。
     */
    @Column(nullable = false, unique = true, name = "username", length = 50)
    private String username;

    /**
     * 用户密码，使用哈希存储。
     */
    @Column(nullable = false, name = "password", length = 255)
    private String password;

    /**
     * 用户邮箱地址。
     */
    @Column(nullable = false, name = "email", length = 100)
    private String email;

    /**
     * 用户的姓。
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * 用户的名。
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * 用户姓的假名（カタカナ）。
     */
    @Column(name = "first_name_kana", nullable = false, length = 50)
    private String firstNameKana;

    /**
     * 用户名的假名（カタカナ）。
     */
    @Column(name = "last_name_kana", nullable = false, length = 50)
    private String lastNameKana;

    /**
     * 用户性别，枚举类型，值为"男性"、"女性"或"選択しない"。
     */
    @Column(nullable = false, name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * 用户地址，可以为空。
     */
    @Column(name = "address", length = 255)
    private String address;

    /**
     * 用户电话号码。
     */
    @Column(nullable = false, name = "phone", length = 20)
    private String phone;

    /**
     * 用户头像图片的路径，可以为空。
     */
    @Column(name = "avatar", length = 255)
    private String avatar;

    /**
     * 用户登录失败的次数。
     */
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    /**
     * 用户账号锁定的时间，可以为空。
     */
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    /**
     * 用户密码重置令牌，可以为空。
     */
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    /**
     * 用户密码重置令牌的有效期限，可以为空。
     */
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // Getters and Setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstNameKana() {
        return firstNameKana;
    }

    public void setFirstNameKana(String firstNameKana) {
        this.firstNameKana = firstNameKana;
    }

    public String getLastNameKana() {
        return lastNameKana;
    }

    public void setLastNameKana(String lastNameKana) {
        this.lastNameKana = lastNameKana;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
