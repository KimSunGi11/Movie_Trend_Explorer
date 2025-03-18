package com.movietrend.controller;

import org.springframework.web.bind.annotation.*;
import com.movietrend.service.TmdbService;
import com.movietrend.dto.MovieListResponse;
import com.movietrend.dto.MovieDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final TmdbService tmdbService;

    @GetMapping("/trending")
    public MovieListResponse getTrendingMovies() {
        MovieListResponse response = tmdbService.getTrendingMovies();
        System.out.println("Controller - Total Results: " + response.getTotalResults());
        System.out.println("Controller - First Movie: " + response.getResults().get(0));
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
} 