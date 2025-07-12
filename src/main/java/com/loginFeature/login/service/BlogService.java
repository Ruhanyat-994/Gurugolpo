package com.loginFeature.login.service;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public Blog createBlog(Blog blog){
        blogRepository.save(blog);
        return blog;
    }
    public List<Blog> getAllBlogs(){
        return blogRepository.findAll();
    }

}
