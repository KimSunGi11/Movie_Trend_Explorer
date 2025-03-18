package com.movietrend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MovieListResponse {
    private int page;
    private List<MovieDto> results;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    @JsonProperty("total_results")
    private int totalResults;
} 