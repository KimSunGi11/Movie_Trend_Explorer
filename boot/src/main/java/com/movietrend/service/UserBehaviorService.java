package com.movietrend.service;

import com.movietrend.document.UserBehaviorDocument;
import com.movietrend.dto.MovieDto;
import com.movietrend.repository.elasticsearch.UserBehaviorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBehaviorService {
    private final UserBehaviorRepository userBehaviorRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final TmdbService tmdbService;

    public void recordMovieView(String username, Long movieId, String movieTitle, 
                              List<String> genres, List<String> keywords, Integer viewDuration) {
        log.info("영화 조회 기록 시작 - 사용자: {}, 영화ID: {}", username, movieId);
        
        try {
        UserBehaviorDocument document = new UserBehaviorDocument();
        document.setUsername(username);
        document.setMovieId(movieId);
        document.setMovieTitle(movieTitle);
        document.setGenres(genres);
        document.setKeywords(keywords);
        document.setViewDuration(viewDuration);
        document.setViewedAt(LocalDate.now());
        document.setIsFavorite(false);
            
            userBehaviorRepository.save(document);
            log.info("영화 조회 기록 저장 성공 - 사용자: {}, 영화ID: {}", username, movieId);
        } catch (Exception e) {
            log.error("영화 조회 기록 저장 실패 - 사용자: {}, 영화ID: {}, 오류: {}", 
                     username, movieId, e.getMessage());
            throw e;
        }
    }

    public void recordFavorite(String username, Long movieId, boolean isFavorite) {
        Optional<UserBehaviorDocument> behavior = userBehaviorRepository
            .findByUsernameAndMovieId(username, movieId);
        
        if (behavior.isPresent()) {
            UserBehaviorDocument doc = behavior.get();
            doc.setIsFavorite(isFavorite);
            userBehaviorRepository.save(doc);
        }
    }

    public List<MovieDto> getRecommendedMovies(String username) {
        // 1. 사용자의 최근 행동 데이터 조회
        Criteria criteria = new Criteria("username").is(username);
        CriteriaQuery query = new CriteriaQuery(criteria)
            .addSort(Sort.by(Sort.Direction.DESC, "viewedAt"))
            .setPageable(PageRequest.of(0, 100));
        
        try {
            System.out.println("사용자 행동 데이터 조회 시작: " + username);
            SearchHits<UserBehaviorDocument> searchHits = elasticsearchOperations.search(query, UserBehaviorDocument.class);
            List<UserBehaviorDocument> behaviors = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

            System.out.println("조회된 행동 데이터 수: " + behaviors.size());

            if (behaviors.isEmpty()) {
                System.out.println("행동 데이터가 없습니다.");
                return Collections.emptyList();
            }

            // 2. 사용자의 선호 장르와 키워드 분석
            Map<String, Integer> genreScores = new HashMap<>();
            Map<String, Integer> keywordScores = new HashMap<>();

            for (UserBehaviorDocument behavior : behaviors) {
                // 즐겨찾기한 영화는 가중치 2배
                int weight = behavior.getIsFavorite() ? 2 : 1;
                
                // 장르 점수 계산
                if (behavior.getGenres() != null) {
                    for (String genre : behavior.getGenres()) {
                        genreScores.merge(genre, weight, Integer::sum);
                    }
                }
                
                // 키워드 점수 계산
                if (behavior.getKeywords() != null) {
                    for (String keyword : behavior.getKeywords()) {
                        keywordScores.merge(keyword, weight, Integer::sum);
                    }
                }
            }

            System.out.println("장르 점수: " + genreScores);
            System.out.println("키워드 점수: " + keywordScores);

            // 3. 상위 장르와 키워드 선택
            List<String> topGenres = genreScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            List<String> topKeywords = keywordScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            System.out.println("선택된 상위 장르: " + topGenres);
            System.out.println("선택된 상위 키워드: " + topKeywords);

            // 4. TMDB API를 통해 추천 영화 검색
            List<MovieDto> recommendedMovies = tmdbService.getRecommendedMovies(topGenres, topKeywords);
            System.out.println("추천된 영화 수: " + recommendedMovies.size());
            
            return recommendedMovies;
        } catch (Exception e) {
            System.err.println("추천 영화 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
} 