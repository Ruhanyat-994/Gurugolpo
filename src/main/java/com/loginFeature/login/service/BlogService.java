package com.loginFeature.login.service;
import com.loginFeature.login.Dto.BlogDto;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Optional;
import java.util.UUID;

@Service

public class BlogService {

    @Autowired

    private BlogRepository blogRepository;

    public Blog createBlog(Blog blog){
        blogRepository.save(blog);
        return blog;
    }



    public Blog getBlogById(UUID id){
        Optional<Blog> blog = blogRepository.findById(id);
        return  blog.orElse(null);
    }


    public Blog updateBlog(Blog blog){
        return blogRepository.save(blog);
    }

    public void deleteBlog(UUID id){
        blogRepository.deleteById(id);
    }

    public List<Blog> getAllBlogsSortedByNewest(){
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }
    public List<Blog> searchBlog(String keyword){
        return blogRepository.searchByKeyWord(keyword);
    }

}