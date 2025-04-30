package com.movietrend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import java.util.List;
import java.util.Map;

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
    
    @JsonProperty("runtime")
    private Integer runtime;
    
    @JsonProperty("genres")
    private List<GenreDto> genres;
    
    @JsonProperty("keywords")
    private Map<String, Object> keywords;
    private List<KeywordDto> keywordList;

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
    
    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }
    
    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }
    
    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }
    
    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }
    
    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
    
    public List<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }
    
    public Map<String, Object> getKeywords() {
        return keywords;
    }

    public void setKeywords(Map<String, Object> keywords) {
        this.keywords = keywords;
    }

    public List<KeywordDto> getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(List<KeywordDto> keywordList) {
        this.keywordList = keywordList;
    }
} 