package com.movietrend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.movietrend.document.MovieDocument;
import com.movietrend.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieIndexingService {

    private final TmdbService tmdbService;
    private final MovieSearchService movieSearchService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시에 실행
    public void indexTrendingMovies() {
        log.info("Starting to index trending movies");
        try {
            List<MovieDto> trendingMovies = tmdbService.getTrendingMovies().getResults();
            List<MovieDocument> movieDocuments = trendingMovies.stream()
                .map(this::convertToMovieDocument)
                .collect(Collectors.toList());
            
            movieSearchService.indexMovies(movieDocuments);
            log.info("Successfully indexed {} trending movies", movieDocuments.size());
        } catch (Exception e) {
            log.error("Error indexing trending movies", e);
        }
    }

    private MovieDocument convertToMovieDocument(MovieDto movieDto) {
        MovieDocument document = new MovieDocument();
        document.setId(movieDto.getId());
        document.setTitle(movieDto.getTitle());
        document.setOriginalTitle(movieDto.getOriginal_title());
        document.setOverview(movieDto.getOverview());
        document.setPosterPath(movieDto.getPoster_path());
        document.setBackdropPath(movieDto.getBackdrop_path());
        document.setReleaseDate(movieDto.getRelease_date());
        document.setVoteAverage(movieDto.getVote_average());
        document.setVoteCount(movieDto.getVote_count());
        document.setPopularity(movieDto.getPopularity());
        
        // 자동완성을 위한 제안 배열 설정
        document.setSuggestions(new String[]{movieDto.getTitle()});
        
        return document;
    }
} 