package com.movietrend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.movietrend.service.TmdbService;
import com.movietrend.dto.MovieListResponse;
import com.movietrend.dto.MovieDto;
import com.movietrend.dto.GenreDto;
import com.movietrend.dto.MovieSearchResponse;
import lombok.RequiredArgsConstructor;
import com.movietrend.service.MovieSearchService;
import com.movietrend.document.MovieDocument;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final TmdbService tmdbService;
    private final MovieSearchService movieSearchService;

    @GetMapping("/trending")
    public MovieListResponse getTrendingMovies() {
        MovieListResponse response = tmdbService.getTrendingMovies();
        System.out.println("Controller - Total Results: " + response.getTotalResults());
        System.out.println("Controller - First Movie: " + response.getResults().get(0));
        return response;
    }

    @GetMapping("/trending/daily")
    public MovieListResponse getDailyTrendingMovies() {
        MovieListResponse response = tmdbService.getDailyTrendingMovies();
        System.out.println("Controller - Daily Total Results: " + response.getTotalResults());
        if (!response.getResults().isEmpty()) {
            System.out.println("Controller - Daily First Movie: " + response.getResults().get(0));
        }
        return response;
    }

    @GetMapping("/search")
    public MovieListResponse searchMovies(
        @RequestParam(defaultValue = "") String query,
        @RequestParam(defaultValue = "1") int page
    ) {
        return tmdbService.searchMovies(query, page);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieDetails(@PathVariable Long id) {
        return tmdbService.getMovieDetails(id);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<GenreDto>> getGenres() {
        return ResponseEntity.ok(tmdbService.getGenres());
    }

    @GetMapping("/discover")
    public ResponseEntity<MovieSearchResponse> discoverMovies(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String sort_by,
            @RequestParam(required = false) String with_genres,
            @RequestParam(required = false) String with_original_language,
            @RequestParam(required = false) Double vote_average_gte,
            @RequestParam(required = false) Integer vote_count_gte,
            @RequestParam(required = false) Integer with_runtime_gte,
            @RequestParam(required = false) Integer with_runtime_lte,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer min_favorite_count) {
        
        return ResponseEntity.ok(tmdbService.discoverMovies(
            page, sort_by, with_genres, with_original_language, 
            vote_average_gte, vote_count_gte,
            with_runtime_gte, with_runtime_lte, keyword,
            min_favorite_count
        ));
    }

    @GetMapping("/search/autocomplete")
    public ResponseEntity<List<MovieDto>> searchMoviesForAutocomplete(@RequestParam String query) {
        List<MovieDto> results = tmdbService.searchMoviesForAutocomplete(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/korean/popular")
    public ResponseEntity<MovieSearchResponse> getKoreanPopularMovies(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tmdbService.getKoreanPopularMovies(page));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<MovieDto>> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(movieSearchService.autocomplete(query));
    }

    @GetMapping("/keywords/autocomplete")
    public ResponseEntity<List<String>> autocompleteKeywords(@RequestParam String query) {
        return ResponseEntity.ok(movieSearchService.autocompleteKeywords(query));
    }
} 