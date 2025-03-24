package com.movietrend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostCommentDto {
    private Long id;
    private String content;
    private String username;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 