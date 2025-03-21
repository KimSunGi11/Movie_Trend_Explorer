package com.movietrend.controller;

import com.movietrend.dto.CommentDto;
import com.movietrend.dto.CommentRequest;
import com.movietrend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies/{movieId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long movieId) {
        return ResponseEntity.ok(commentService.getCommentsByMovieId(movieId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
        @PathVariable Long movieId,
        @RequestBody CommentRequest request,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        CommentDto comment = commentService.createComment(movieId, request.getContent(), userEmail);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long movieId,
        @PathVariable Long commentId,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.ok().build();
    }
} 