package com.eden.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eden.model.Post;
import com.eden.model.User;
import com.eden.repository.PostRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	//格式化日期时间
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");

	//根据 ID 获取文章
	public Post findPostById(Long id) {
		return postRepository.findById(id).orElse(null);
	}

	// 创建文章方法
	public void createPost(String title, String content, User author) {
		Post post = new Post();
		post.setTitle(title);
		post.setContent(content);
		post.setAuthor(author);
		postRepository.save(post); // 保存文章到数据库
	}

	// 编辑文章方法
	public void editPost(Post post, String title, String content) {
		post.setTitle(title);
		post.setContent(content);
		postRepository.save(post); // 更新文章到数据库
	}

	// 删除文章方法
	public void deletePost(Long postId) {
		postRepository.deleteById(postId); // 从数据库中删除文章
	}

	// 获取所有文章，按创建时间降序
	public List<Post> getAllPosts() {
		return postRepository.findAllByOrderByCreatedAtDesc();
	}

	// 根据ID获取文章
	public Post getPostById(Long postId) {
		return postRepository.findById(postId).orElse(null);
	}

	// 获取特定用户的所有文章
	public List<Post> getPostsByUser(User user) {
		return postRepository.findByAuthor(user);
	}
}
