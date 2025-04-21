package com.movietrend.dto;

import lombok.Data;
import java.util.List;

@Data
public class MovieSearchResponse {
    private List<MovieDto> results;
    private int page;
    private int total_pages;
    private int total_results;
} 