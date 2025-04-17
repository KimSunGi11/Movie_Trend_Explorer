package com.movietrend.service;

import com.movietrend.config.TmdbConfig;
import com.movietrend.dto.MovieDto;
import com.movietrend.dto.MovieListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TmdbService {
    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;

    public MovieListResponse getTrendingMovies() {
        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/trending/movie/week")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .build()
            .toUriString();
        
        System.out.println("TMDB API URL: " + url);
        MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
        System.out.println("TMDB API Response: " + response);
        return response;
    }

    public MovieListResponse getDailyTrendingMovies() {
        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/trending/movie/day")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .build()
            .toUriString();
        
        System.out.println("TMDB API URL (Daily): " + url);
        MovieListResponse response = restTemplate.getForObject(url, MovieListResponse.class);
        System.out.println("TMDB API Response (Daily): " + response);
        return response;
    }

    public MovieListResponse searchMovies(String query, int page) {
        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/movie")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("query", query)
            .queryParam("page", page)
            .queryParam("language", "ko-KR")
            .build()
            .toUriString();
        
        return restTemplate.getForObject(url, MovieListResponse.class);
    }

    public MovieDto getMovieDetails(Long movieId) {
        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/movie/" + movieId)
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .build()
            .toUriString();
        
        return restTemplate.getForObject(url, MovieDto.class);
    }
} 