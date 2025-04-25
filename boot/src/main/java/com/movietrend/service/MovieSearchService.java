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

@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final MovieSearchRepository movieSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void indexMovies(List<MovieDocument> movies) {
        movieSearchRepository.saveAll(movies);
    }

    public List<MovieDto> autocomplete(String query) {
        Criteria criteria = new Criteria("title").startsWith(query)
            .or("suggestions").startsWith(query);
        
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        searchQuery.setPageable(PageRequest.of(0, 10));

        SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(searchQuery, MovieDocument.class);
        return searchHits.stream()
            .map(hit -> convertToMovieDto(hit.getContent()))
            .collect(Collectors.toList());
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

    private MovieDto convertToMovieDto(MovieDocument doc) {
        MovieDto dto = new MovieDto();
        dto.setId(doc.getId());
        dto.setTitle(doc.getTitle());
        dto.setOverview(doc.getOverview());
        dto.setPoster_path(doc.getPosterPath());
        dto.setRelease_date(doc.getReleaseDate());
        dto.setVote_average(doc.getVoteAverage());
        return dto;
    }
} 