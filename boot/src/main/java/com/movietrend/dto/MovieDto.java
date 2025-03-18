package com.movietrend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MovieDto {
    private Long id;
    private String title;
    private String overview;
    
    @JsonProperty("poster_path")
    private String poster_path;
    
    @JsonProperty("release_date")
    private String release_date;
    
    @JsonProperty("vote_average")
    private Double vote_average;
    
    @JsonProperty("vote_count")
    private Integer vote_count;
    
    @JsonProperty("backdrop_path")
    private String backdrop_path;
    
    private Boolean adult;
    
    @JsonProperty("original_title")
    private String original_title;
    
    @JsonProperty("original_language")
    private String original_language;
    
    private Double popularity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public Integer getVote_count() {
        return vote_count;
    }

    public void setVote_count(Integer vote_count) {
        this.vote_count = vote_count;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
} 