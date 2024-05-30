package com.eden.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eden.model.Post;
import com.eden.service.PostService;
import com.eden.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
	private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showHomePage(Model model, HttpSession session) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        
        Object user = session.getAttribute("userId");
        model.addAttribute("userId", user);
        
        System.out.println("Number of posts: " + posts.size());
        for (Post post : posts) {
            System.out.println("Post title: " + post.getTitle());
        }
        return "home"; // 返回主页的模板名称
    }

    @GetMapping("/login-register")
    public String showLoginRegisterPage(Model model) {
        return "login-register"; // 返回合并的登录和注册页面的模板名称
    }
}
