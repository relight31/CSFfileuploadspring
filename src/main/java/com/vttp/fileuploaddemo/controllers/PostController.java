package com.vttp.fileuploaddemo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vttp.fileuploaddemo.models.Post;
import com.vttp.fileuploaddemo.repositories.FileRepo;

@Controller
@RequestMapping(path = "/post")
public class PostController {
    @Autowired
    private FileRepo fileRepo;

    @GetMapping(path = "{postId}")
    public String getPost(@PathVariable int postId, Model model) {
        Optional<Post> opt = fileRepo.getPost(postId);
        Post post = opt.get();
        model.addAttribute("post", post);
        model.addAttribute("imageSrc",
                "/upload/%d".formatted(post.getPostId()));
        return "upload";
    }
}
