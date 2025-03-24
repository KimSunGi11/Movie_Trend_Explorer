package com.movietrend.service;

import com.movietrend.dto.PostDto;
import com.movietrend.dto.PostCommentDto;
import com.movietrend.entity.Post;
import com.movietrend.entity.PostComment;
import com.movietrend.entity.User;
import com.movietrend.repository.PostRepository;
import com.movietrend.repository.PostCommentRepository;
import com.movietrend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        return convertToDto(post);
    }

    @Transactional
    public PostDto createPost(String title, String content, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Transactional
    public PostDto updatePost(Long id, String title, String content, String username) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("You can only update your own posts");
        }

        post.setTitle(title);
        post.setContent(content);

        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    @Transactional
    public PostCommentDto addComment(Long postId, String content, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PostComment comment = new PostComment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        PostComment savedComment = postCommentRepository.save(comment);
        return convertToCommentDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String username) {
        PostComment comment = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to this post");
        }

        if (!comment.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("You can only delete your own comments");
        }

        postCommentRepository.delete(comment);
    }

    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setUsername(post.getUser().getUsername());
        dto.setName(post.getUser().getName());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setComments(post.getComments().stream()
            .map(this::convertToCommentDto)
            .collect(Collectors.toList()));
        return dto;
    }

    private PostCommentDto convertToCommentDto(PostComment comment) {
        PostCommentDto dto = new PostCommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUsername(comment.getUser().getUsername());
        dto.setName(comment.getUser().getName());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
} 