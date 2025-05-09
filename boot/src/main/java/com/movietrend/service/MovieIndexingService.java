package com.movietrend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.movietrend.document.MovieDocument;
import com.movietrend.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieIndexingService {

    private final TmdbService tmdbService;
    private final MovieSearchService movieSearchService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시에 실행
    public void indexMovies() {
        log.info("Starting to index movies");
        Set<MovieDto> allMovies = new HashSet<>();
        
        try {
            // 1. 트렌딩 영화 수집
            List<MovieDto> trendingMovies = tmdbService.getTrendingMovies().getResults();
            allMovies.addAll(trendingMovies);
            log.info("Collected {} trending movies", trendingMovies.size());

            // 2. 현재 상영중인 영화 수집 (5페이지)
            for (int i = 1; i <= 5; i++) {
                List<MovieDto> nowPlayingMovies = tmdbService.getNowPlayingMovies(i).getResults();
                allMovies.addAll(nowPlayingMovies);
                log.info("Collected {} now playing movies from page {}", nowPlayingMovies.size(), i);
            }

            // 3. 인기 영화 수집 (5페이지)
            for (int i = 1; i <= 5; i++) {
                List<MovieDto> popularMovies = tmdbService.getPopularMovies(i).getResults();
                allMovies.addAll(popularMovies);
                log.info("Collected {} popular movies from page {}", popularMovies.size(), i);
            }

            // 4. 평점 높은 영화 수집 (5페이지)
            for (int i = 1; i <= 5; i++) {
                List<MovieDto> topRatedMovies = tmdbService.getTopRatedMovies(i).getResults();
                allMovies.addAll(topRatedMovies);
                log.info("Collected {} top rated movies from page {}", topRatedMovies.size(), i);
            }

            // 5. 개봉 예정 영화 수집 (3페이지)
            for (int i = 1; i <= 3; i++) {
                List<MovieDto> upcomingMovies = tmdbService.getUpcomingMovies(i).getResults();
                allMovies.addAll(upcomingMovies);
                log.info("Collected {} upcoming movies from page {}", upcomingMovies.size(), i);
            }

            // 6. 한국 영화 수집 (5페이지)
            for (int i = 1; i <= 5; i++) {
                List<MovieDto> koreanMovies = tmdbService.getKoreanPopularMovies(i).getResults();
                allMovies.addAll(koreanMovies);
                log.info("Collected {} Korean movies from page {}", koreanMovies.size(), i);
            }

            // 수집된 모든 영화를 인덱싱
            List<MovieDocument> movieDocuments = allMovies.stream()
                .map(this::convertToMovieDocument)
                .collect(Collectors.toList());
            
            movieSearchService.indexMovies(movieDocuments);
            log.info("Successfully indexed {} total movies", movieDocuments.size());
        } catch (Exception e) {
            log.error("Error indexing movies", e);
        }
    }

    private MovieDocument convertToMovieDocument(MovieDto movieDto) {
        MovieDocument doc = new MovieDocument();
        doc.setId(movieDto.getId());
        doc.setTitle(movieDto.getTitle());
        doc.setOriginalTitle(movieDto.getOriginal_title());
        doc.setOverview(movieDto.getOverview());
        doc.setPosterPath(movieDto.getPoster_path());
        doc.setBackdropPath(movieDto.getBackdrop_path());
        doc.setReleaseDate(movieDto.getRelease_date());
        doc.setVoteAverage(movieDto.getVote_average());
        doc.setVoteCount(movieDto.getVote_count());
        doc.setPopularity(movieDto.getPopularity());
        
        // 초성 검색을 위한 suggestions 필드 설정
        if (movieDto.getTitle() != null) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add(movieDto.getTitle());
            
            // 한글 초성 추출
            String title = movieDto.getTitle();
            String chosung = extractChosung(title);
            if (!chosung.isEmpty()) {
                suggestions.add(chosung);
            }
            
            // 첫 글자 초성만 추가
            if (title.length() > 0) {
                String firstChar = title.substring(0, 1);
                String firstChosung = extractChosung(firstChar);
                if (!firstChosung.isEmpty()) {
                    suggestions.add(firstChosung);
                }
            }
            
            doc.setSuggestions(suggestions.toArray(new String[0]));
        }
        
        return doc;
    }

    private String extractChosung(String text) {
        final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        };
        
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (c >= '가' && c <= '힣') {
                int chosungIndex = (c - '가') / 28 / 21;
                result.append(CHOSUNG[chosungIndex]);
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
} 