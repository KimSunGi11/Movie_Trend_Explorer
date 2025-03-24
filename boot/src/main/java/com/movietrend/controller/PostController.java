package com.movietrend.controller;

import com.movietrend.dto.PostDto;
import com.movietrend.dto.PostCommentDto;
import com.movietrend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestBody PostDto postDto,
            Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(
            postDto.getTitle(),
            postDto.getContent(),
            authentication.getName()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto,
            Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(
            id,
            postDto.getTitle(),
            postDto.getContent(),
            authentication.getName()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<PostCommentDto> addComment(
            @PathVariable Long id,
            @RequestBody PostCommentDto commentDto,
            Authentication authentication) {
        return ResponseEntity.ok(postService.addComment(
            id,
            commentDto.getContent(),
            authentication.getName()
        ));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication authentication) {
        postService.deleteComment(postId, commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }
} 