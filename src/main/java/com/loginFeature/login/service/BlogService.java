package com.loginFeature.login.service;

import com.loginFeature.login.Dto.BlogDto;
import com.loginFeature.login.Dto.CommentDto;
import com.loginFeature.login.entity.Blog;
import com.loginFeature.login.entity.Comment;
import com.loginFeature.login.enums.VoteType;
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

    public Blog createBlog(Blog blog){
        blogRepository.save(blog);
        return blog;
    }

    public BlogDto convertToDto(Blog blog){
        BlogDto dto= new BlogDto();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setUpVote(blog.getUpVote());

        dto.setUpVote(blog.getVotes().stream()
                .filter(v -> v.getVoteType().equals(VoteType.UPVOTE))
                .count());
        dto.setDownVote(blog.getVotes().stream()
                .filter(v->v.getVoteType().equals(VoteType.DOWNVOTE))
                .count());
        return dto;
    }

    private CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getCreatedAt()
        );
    }


    public List<BlogDto> getAllBlogs(){
        return blogRepository.findAll().stream()
                .map(blog -> new BlogDto(
                        blog.getId(),
                        blog.getTitle(),
                        blog.getContent(),
                        blog.getAuthor(),
                        blog.getComments().stream()
                                .map(this::mapToCommentDto)
                                .toList(),
                        blog.getUpVote(),
                        blog.getDownVote()
                ))
                .toList();
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
}
