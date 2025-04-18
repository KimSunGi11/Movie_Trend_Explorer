package com.movietrend.controller;

import com.movietrend.dto.CommentDto;
import com.movietrend.dto.CommentRequest;
import com.movietrend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentOwner(#id, authentication.principal.username)")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok().body("댓글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 삭제에 실패했습니다.");
        }
    }
} 