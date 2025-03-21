package com.movietrend.service;

import com.movietrend.dto.CommentDto;
import com.movietrend.entity.Comment;
import com.movietrend.entity.User;
import com.movietrend.repository.CommentRepository;
import com.movietrend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByMovieId(Long movieId) {
        return commentRepository.findByMovieIdOrderByCreatedAtDesc(movieId)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(Long movieId, String content, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setMovieId(movieId);
        comment.setContent(content);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setMovieId(comment.getMovieId());
        dto.setContent(comment.getContent());
        dto.setName(comment.getUser().getName());
        dto.setUsername(comment.getUser().getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
} 