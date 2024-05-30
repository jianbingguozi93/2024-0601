package com.eden.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eden.model.User;

/**
 * 用户仓库接口，提供数据库操作方法
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByResetToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.avatar = ?2 WHERE u.id = ?1")
    void updateAvatarPath(Long userId, String avatarPath);
}
