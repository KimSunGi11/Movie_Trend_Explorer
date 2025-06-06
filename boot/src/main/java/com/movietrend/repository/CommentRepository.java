package com.movietrend.repository;

import com.movietrend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMovieIdOrderByCreatedAtDesc(Long movieId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
} 