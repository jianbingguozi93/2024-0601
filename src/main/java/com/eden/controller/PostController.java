package com.eden.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.eden.model.Post;
import com.eden.model.User;
import com.eden.service.PostService;
import com.eden.service.UserService;

@Controller
@SessionAttributes("userId") // 声明要存储在会话中的属性
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");

    /**
     * 显示所有文章，按创建时间降序排列
     * @param model 用于传递数据到视图
     * @return 返回文章列表页面的模板名称
     */
    @GetMapping("/posts")
    public String showPostList(Model model) {
        // 获取所有文章并按创建时间降序排列
        List<Post> posts = postService.getAllPosts().stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // 按创建时间降序排列
                .map(post -> {
                    post.setFormattedCreatedAt(post.getCreatedAt().format(DATE_TIME_FORMATTER)); // 格式化创建时间
                    return post;
                })
                .collect(Collectors.toList());
        model.addAttribute("posts", posts); // 将文章列表添加到模型中
        return "postList"; // 返回文章列表页面的模板名称
    }

    /**
     * 显示文章详情页面
     * @param id 文章ID
     * @param model 用于传递数据到视图
     * @return 返回文章详情页面的模板名称
     */
    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {
        model.addAttribute("post", postService.findPostById(id)); // 将文章添加到模型中
        return "postDetail"; // 返回文章详情页面的模板名称
    }

    /**
     * 显示用户的所有文章页面
     * @param userId 用户ID
     * @param edit 编辑中的文章ID（可选）
     * @param model 用于传递数据到视图
     * @return 返回用户文章页面的模板名称
     */
    @GetMapping("/myPosts")
    public String showMyPosts(@SessionAttribute("userId") Long userId, @RequestParam(required = false) Long edit, Model model) {
        User user = userService.getCurrentUser(userId); // 获取当前用户
        // 获取用户的所有文章并按创建时间降序排列
        List<Post> posts = postService.getPostsByUser(user).stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // 按创建时间降序排列
                .map(post -> {
                    post.setFormattedCreatedAt(post.getCreatedAt().format(DATE_TIME_FORMATTER)); // 格式化创建时间
                    return post;
                })
                .collect(Collectors.toList());
        model.addAttribute("posts", posts); // 将文章列表添加到模型中

        // 添加编辑中的文章ID列表
        List<Long> editingIds = new ArrayList<>();
        if (edit != null) {
            editingIds.add(edit);
        }
        model.addAttribute("editingIds", editingIds); // 将编辑中的文章ID列表添加到模型中

        return "myPosts"; // 返回用户文章页面的模板名称
    }

    /**
     * 处理创建新文章请求
     * @param title 文章标题
     * @param content 文章内容
     * @param userId 用户ID
     * @return 重定向到用户文章页面
     */
    @PostMapping("/posts/create")
    public String createPost(@RequestParam String title, @RequestParam String content, @SessionAttribute("userId") Long userId) {
        User author = userService.getCurrentUser(userId); // 获取当前用户
        postService.createPost(title, content, author); // 创建新文章
        return "redirect:/myPosts"; // 重定向到用户文章页面
    }

    /**
     * 处理编辑文章请求
     * @param id 文章ID
     * @param content 文章内容
     * @param userId 用户ID
     * @return 重定向到用户文章页面
     */
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable Long id, @RequestParam String content, @SessionAttribute("userId") Long userId) {
        Post post = postService.getPostById(id); // 获取文章
        if (post.getAuthor().getId().equals(userId)) { // 确认用户是文章的作者
            postService.editPost(post, post.getTitle(), content); // 编辑文章
        }
        return "redirect:/myPosts"; // 重定向到用户文章页面
    }

    /**
     * 处理删除文章请求
     * @param id 文章ID
     * @param userId 用户ID
     * @return 重定向到用户文章页面
     */
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, @SessionAttribute("userId") Long userId) {
        Post post = postService.getPostById(id); // 获取文章
        if (post.getAuthor().getId().equals(userId)) { // 确认用户是文章的作者
            postService.deletePost(id); // 删除文章
        }
        return "redirect:/myPosts"; // 重定向到用户文章页面
    }
}
