package com.movietrend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long movieId;
    private String content;
    private String name;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Data
class CommentResponse {
    private Long id;
    private Long movieId;
    private String content;
    private String name;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 