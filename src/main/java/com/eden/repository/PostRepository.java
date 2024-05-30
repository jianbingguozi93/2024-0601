package com.eden.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eden.model.Post;
import com.eden.model.User;

/**
 * 文章仓库接口，提供数据库操作方法
 */
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByAuthor(User author);
	List<Post> findAllByOrderByCreatedAtDesc();
}
