package com.loginFeature.login.service;

import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    // it will help to create blogs
    public Blog createBlog(Blog blog){
        blogRepository.save(blog);
        return blog;
    }
    // it will give all the blogs
    public List<Blog> getAllBlogs(){
        return blogRepository.findAll();
    }

    // this get blogs by the id
    public Blog getBlogById(UUID id){
        Optional<Blog> blog = blogRepository.findById(id);
        return  blog.orElse(null);
    }
     public Blog updateBlog(Blog blog){
        return blogRepository.save(blog);
    }

    //this will delete the blog
    public void deleteBlog(UUID id){
        blogRepository.deleteById(id);
    }



}
