package com.movietrend.service;

import com.movietrend.config.TmdbConfig;
import com.movietrend.dto.MovieDto;
import com.movietrend.dto.MovieListResponse;
import com.movietrend.dto.GenreDto;
import com.movietrend.dto.MovieSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TmdbService {
    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;
    private final FavoriteService favoriteService;

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

    public List<GenreDto> getGenres() {
        String url = tmdbConfig.getBaseUrl() + "/genre/movie/list?api_key=" + tmdbConfig.getKey();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<Map<String, Object>> genres = (List<Map<String, Object>>) response.getBody().get("genres");
            return genres.stream()
                .map(genre -> {
                    GenreDto dto = new GenreDto();
                    dto.setId(Long.valueOf(genre.get("id").toString()));
                    dto.setName((String) genre.get("name"));
                    return dto;
                })
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public MovieSearchResponse discoverMovies(
            Integer page,
            String sortBy,
            String withGenres,
            String language,
            Double voteAverageGte,
            Integer voteCountGte,
            Integer withRuntimeGte,
            Integer withRuntimeLte,
            String keyword,
            Integer minFavoriteCount) {
        
        StringBuilder urlBuilder = new StringBuilder(tmdbConfig.getBaseUrl() + "/discover/movie?api_key=" + tmdbConfig.getKey());
        
        if (page != null) {
            urlBuilder.append("&page=").append(page);
        }
        if (sortBy != null) {
            urlBuilder.append("&sort_by=").append(sortBy);
        }
        if (withGenres != null && !withGenres.isEmpty()) {
            urlBuilder.append("&with_genres=").append(withGenres);
        }
        if (language != null && !language.isEmpty()) {
            urlBuilder.append("&language=").append(language);
        }
        if (voteAverageGte != null) {
            urlBuilder.append("&vote_average.gte=").append(voteAverageGte);
        }
        if (voteCountGte != null) {
            urlBuilder.append("&vote_count.gte=").append(voteCountGte);
        }
        if (withRuntimeGte != null) {
            urlBuilder.append("&with_runtime.gte=").append(withRuntimeGte);
        }
        if (withRuntimeLte != null) {
            urlBuilder.append("&with_runtime.lte=").append(withRuntimeLte);
        }
        if (keyword != null && !keyword.isEmpty()) {
            urlBuilder.append("&with_keywords=").append(keyword);
        }

        ResponseEntity<Map> response = restTemplate.getForEntity(urlBuilder.toString(), Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            MovieSearchResponse searchResponse = new MovieSearchResponse();
            searchResponse.setPage((Integer) response.getBody().get("page"));
            searchResponse.setTotal_pages((Integer) response.getBody().get("total_pages"));
            searchResponse.setTotal_results((Integer) response.getBody().get("total_results"));
            
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            List<MovieDto> movies = results.stream()
                .map(this::convertToMovieDto)
                .collect(Collectors.toList());
            
            // 즐겨찾기 수 필터링
            if (minFavoriteCount != null && minFavoriteCount > 0) {
                movies = movies.stream()
                    .filter(movie -> {
                        Long favoriteCount = favoriteService.getFavoriteCount(movie.getId());
                        return favoriteCount >= minFavoriteCount;
                    })
                    .collect(Collectors.toList());
                
                // 필터링 후 페이지네이션 정보 업데이트
                int totalFiltered = movies.size();
                searchResponse.setTotal_results(totalFiltered);
                searchResponse.setTotal_pages((int) Math.ceil((double) totalFiltered / 20));
                
                // 페이지네이션 적용
                int startIndex = (page - 1) * 20;
                int endIndex = Math.min(startIndex + 20, totalFiltered);
                if (startIndex < totalFiltered) {
                    movies = movies.subList(startIndex, endIndex);
                } else {
                    movies = new ArrayList<>();
                }
            }
            
            searchResponse.setResults(movies);
            return searchResponse;
        }
        return new MovieSearchResponse();
    }

    private MovieDto convertToMovieDto(Map<String, Object> movie) {
        MovieDto dto = new MovieDto();
        dto.setId(Long.valueOf(movie.get("id").toString()));
        dto.setTitle((String) movie.get("title"));
        dto.setOverview((String) movie.get("overview"));
        dto.setPoster_path((String) movie.get("poster_path"));
        dto.setBackdrop_path((String) movie.get("backdrop_path"));
        dto.setRelease_date((String) movie.get("release_date"));
        dto.setVote_average(((Number) movie.get("vote_average")).doubleValue());
        dto.setVote_count(((Number) movie.get("vote_count")).intValue());
        dto.setPopularity(((Number) movie.get("popularity")).doubleValue());
        return dto;
    }
} 