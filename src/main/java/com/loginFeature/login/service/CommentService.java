package com.loginFeature.login.service;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.CommentRepository;
import com.loginFeature.login.repository.UserRepsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepsitory userRepsitory;

    public Comment addComment(UUID blogId, String content, String username){
        Blog blog = blogService.getBlogById(blogId);
        User user = userRepsitory.findByUsername(username);

        Comment comment = new Comment();
        comment.setBlog(blog);
        comment.setUser(user);
        comment.setContent(content);
        return commentRepository.save(comment);

    }

    public List<Comment> getCommentsForBlog(UUID blogId){
        return commentRepository.findByBlog_Id(blogId);
    }

}
