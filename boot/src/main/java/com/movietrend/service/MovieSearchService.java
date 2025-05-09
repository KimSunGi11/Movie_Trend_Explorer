package com.movietrend.service;

import com.movietrend.document.MovieDocument;
import com.movietrend.repository.elasticsearch.MovieSearchRepository;
import com.movietrend.dto.MovieDto;
import com.movietrend.dto.MovieSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import com.movietrend.config.TmdbConfig;
import org.springframework.web.client.RestTemplate;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final MovieSearchRepository movieSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;

    public void indexMovies(List<MovieDocument> movies) {
        movieSearchRepository.saveAll(movies);
    }

    public List<MovieDto> autocomplete(String query) {
        if (query == null || query.length() < 1) {
            return new ArrayList<>();
        }

        List<MovieDto> results = new ArrayList<>();
        Set<Long> addedMovieIds = new HashSet<>();

        // 1. Elasticsearch에서 검색 (한글 검색 최적화)
        Criteria criteria = new Criteria("title").startsWith(query)
            .or("suggestions").startsWith(query);
        
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        searchQuery.setPageable(PageRequest.of(0, 10));

        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(searchQuery, MovieDocument.class);
        List<MovieDto> elasticResults = searchHits.stream()
            .map(hit -> convertToMovieDto(hit.getContent()))
            .collect(Collectors.toList());

        // Elasticsearch 결과 추가
        for (MovieDto movie : elasticResults) {
            if (addedMovieIds.add(movie.getId())) {
                results.add(movie);
            }
        }

        // 2. Elasticsearch 결과가 10개 미만이면 TMDB API에서 추가 검색
        if (results.size() < 10) {
            int maxPages = 2; // 최대 2페이지까지 검색
            int remaining = 10 - results.size();

            for (int page = 1; page <= maxPages; page++) {
                String url = UriComponentsBuilder
                    .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/movie")
                    .queryParam("api_key", tmdbConfig.getKey())
                    .queryParam("query", query)
                    .queryParam("page", page)
                    .queryParam("language", "ko-KR")
                    .build()
                    .toUriString();

                try {
                    ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        List<Map<String, Object>> tmdbResults = (List<Map<String, Object>>) response.getBody().get("results");
                        if (tmdbResults != null && !tmdbResults.isEmpty()) {
                            List<MovieDto> pageResults = tmdbResults.stream()
                                .map(this::convertToMovieDto)
                                .filter(movie -> addedMovieIds.add(movie.getId())) // 중복 제거
                                .limit(remaining)
                                .collect(Collectors.toList());
                            results.addAll(pageResults);
                            remaining -= pageResults.size();
                            if (remaining <= 0) break;
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("TMDB API 검색 중 오류 발생: " + e.getMessage());
                    break;
                }
            }
        }

        return results;
    }

    public MovieSearchResponse searchMovies(String query, int page, int size) {
        Criteria criteria = new Criteria("title").matches(query)
            .or("overview").matches(query);
        
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        searchQuery.setPageable(PageRequest.of(page - 1, size));

        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(searchQuery, MovieDocument.class);
        List<MovieDto> results = searchHits.stream()
            .map(hit -> convertToMovieDto(hit.getContent()))
            .collect(Collectors.toList());

        MovieSearchResponse response = new MovieSearchResponse();
        response.setResults(results);
        response.setPage(page);
        response.setTotal_pages((int) Math.ceil(searchHits.getTotalHits() / (double) size));
        response.setTotal_results((int) searchHits.getTotalHits());

        return response;
    }

    public List<String> autocompleteKeywords(String query) {
        if (query == null || query.length() < 1) {
            return new ArrayList<>();
        }

        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/keyword")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("query", query)
            .queryParam("page", 1)
            .build()
            .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                if (results != null) {
                    return results.stream()
                        .map(result -> (String) result.get("name"))
                        .limit(10)
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("키워드 자동완성 검색 중 오류 발생: " + e.getMessage());
        }

        return new ArrayList<>();
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

    private MovieDto convertToMovieDto(MovieDocument movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setOverview(movie.getOverview());
        dto.setPoster_path(movie.getPosterPath());
        dto.setBackdrop_path(movie.getBackdropPath());
        dto.setRelease_date(movie.getReleaseDate());
        dto.setVote_average(movie.getVoteAverage());
        dto.setVote_count(movie.getVoteCount());
        dto.setPopularity(movie.getPopularity());
        return dto;
    }
} 