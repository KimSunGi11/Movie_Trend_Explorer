package com.movietrend.service;

import com.movietrend.config.TmdbConfig;
import com.movietrend.dto.MovieDto;
import com.movietrend.dto.MovieListResponse;
import com.movietrend.dto.GenreDto;
import com.movietrend.dto.MovieSearchResponse;
import com.movietrend.dto.KeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;

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
        // 영화 기본 정보 가져오기
        String url = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/movie/" + movieId)
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .build()
            .toUriString();
        
        MovieDto movieDto = restTemplate.getForObject(url, MovieDto.class);
        
        // 키워드 정보 별도로 가져오기
        String keywordsUrl = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/movie/" + movieId + "/keywords")
            .queryParam("api_key", tmdbConfig.getKey())
            .build()
            .toUriString();
        
        try {
            ResponseEntity<Map> keywordsResponse = restTemplate.getForEntity(keywordsUrl, Map.class);
            if (keywordsResponse.getStatusCode() == HttpStatus.OK && keywordsResponse.getBody() != null) {
                List<Map<String, Object>> keywordsList = (List<Map<String, Object>>) keywordsResponse.getBody().get("keywords");
                if (keywordsList != null) {
                    List<KeywordDto> keywords = keywordsList.stream()
                        .map(keyword -> {
                            KeywordDto keywordDto = new KeywordDto();
                            keywordDto.setId(Long.valueOf(keyword.get("id").toString()));
                            keywordDto.setName((String) keyword.get("name"));
                            return keywordDto;
                        })
                        .collect(Collectors.toList());
                    movieDto.setKeywordList(keywords);
                }
            }
        } catch (Exception e) {
            System.err.println("키워드 정보를 가져오는 중 오류 발생: " + e.getMessage());
        }
        
        return movieDto;
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
            String sort_by,
            String with_genres,
            String language,
            Double vote_average_gte,
            Integer vote_count_gte,
            Integer with_runtime_gte,
            Integer with_runtime_lte,
            String keyword,
            Integer min_favorite_count) {
        
        UriComponentsBuilder urlBuilder = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/discover/movie")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR");
        
        if (page != null) {
            urlBuilder.queryParam("page", page);
        }
        if (sort_by != null) {
            urlBuilder.queryParam("sort_by", sort_by);
        }
        if (with_genres != null && !with_genres.isEmpty()) {
            urlBuilder.queryParam("with_genres", with_genres);
        }
        if (language != null && !language.isEmpty()) {
            urlBuilder.queryParam("with_original_language", language);
        }
        if (vote_average_gte != null) {
            // 웹에서 0-10 사이의 값을 받으므로 그대로 사용
            urlBuilder.queryParam("vote_average.gte", vote_average_gte);
        }
        if (vote_count_gte != null) {
            urlBuilder.queryParam("vote_count.gte", vote_count_gte);
        }
        if (with_runtime_gte != null && with_runtime_lte != null) {
            // 상영 시간 범위를 분 단위로 지정
            urlBuilder.queryParam("with_runtime.gte", with_runtime_gte);
            urlBuilder.queryParam("with_runtime.lte", with_runtime_lte);
        } else if (with_runtime_gte != null) {
            urlBuilder.queryParam("with_runtime.gte", with_runtime_gte);
        } else if (with_runtime_lte != null) {
            urlBuilder.queryParam("with_runtime.lte", with_runtime_lte);
        }
        
        String url = urlBuilder.build().toUriString();
        
        if (keyword != null && !keyword.isEmpty()) {
            // 키워드 검색을 위해 먼저 키워드 ID를 찾습니다
            String keywordSearchUrl = UriComponentsBuilder
                .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/keyword")
                .queryParam("api_key", tmdbConfig.getKey())
                .queryParam("query", keyword)
                .build()
                .toUriString();
                
            ResponseEntity<Map> keywordResponse = restTemplate.getForEntity(keywordSearchUrl, Map.class);
            
            if (keywordResponse.getStatusCode() == HttpStatus.OK && keywordResponse.getBody() != null) {
                List<Map<String, Object>> keywords = (List<Map<String, Object>>) keywordResponse.getBody().get("results");
                if (!keywords.isEmpty()) {
                    String keywordId = keywords.get(0).get("id").toString();
                    url = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("with_keywords", keywordId)
                        .build()
                        .toUriString();
                }
            }
        }

        System.out.println("TMDB API URL: " + url); // URL 로깅 추가

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            MovieSearchResponse searchResponse = new MovieSearchResponse();
            
            // 즐겨찾기 수 필터링이 필요한 경우
            if (min_favorite_count != null && min_favorite_count > 0) {
                // 전체 페이지의 영화를 가져오기 위해 여러 페이지를 조회
                List<MovieDto> allMovies = new ArrayList<>();
                int totalPages = (Integer) response.getBody().get("total_pages");
                int maxPages = Math.min(totalPages, 1000); // 최대 1000페이지까지 조회
                
                // 병렬 처리를 위한 스레드 풀 생성
                int processors = Runtime.getRuntime().availableProcessors();
                ExecutorService executor = Executors.newFixedThreadPool(processors);
                List<Future<List<MovieDto>>> futures = new ArrayList<>();
                
                // 페이지를 병렬로 처리
                for (int i = 1; i <= maxPages; i++) {
                    final int pageNum = i;
                    futures.add(executor.submit(() -> {
                        UriComponentsBuilder pageUrlBuilder = UriComponentsBuilder
                            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/discover/movie")
                            .queryParam("api_key", tmdbConfig.getKey())
                            .queryParam("language", "ko-KR")
                            .queryParam("page", pageNum);
                        
                        if (sort_by != null) {
                            pageUrlBuilder.queryParam("sort_by", sort_by);
                        }
                        if (with_genres != null && !with_genres.isEmpty()) {
                            pageUrlBuilder.queryParam("with_genres", with_genres);
                        }
                        if (language != null && !language.isEmpty()) {
                            pageUrlBuilder.queryParam("with_original_language", language);
                        }
                        if (vote_average_gte != null) {
                            pageUrlBuilder.queryParam("vote_average.gte", vote_average_gte);
                        }
                        if (vote_count_gte != null) {
                            pageUrlBuilder.queryParam("vote_count.gte", vote_count_gte);
                        }
                        if (with_runtime_gte != null && with_runtime_lte != null) {
                            pageUrlBuilder.queryParam("with_runtime.gte", with_runtime_gte);
                            pageUrlBuilder.queryParam("with_runtime.lte", with_runtime_lte);
                        } else if (with_runtime_gte != null) {
                            pageUrlBuilder.queryParam("with_runtime.gte", with_runtime_gte);
                        } else if (with_runtime_lte != null) {
                            pageUrlBuilder.queryParam("with_runtime.lte", with_runtime_lte);
                        }
                        
                        String pageUrl = pageUrlBuilder.build().toUriString();
                        
                        if (keyword != null && !keyword.isEmpty()) {
                            String keywordSearchUrl = UriComponentsBuilder
                                .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/keyword")
                                .queryParam("api_key", tmdbConfig.getKey())
                                .queryParam("query", keyword)
                                .build()
                                .toUriString();
                                
                            ResponseEntity<Map> keywordResponse = restTemplate.getForEntity(keywordSearchUrl, Map.class);
                            
                            if (keywordResponse.getStatusCode() == HttpStatus.OK && keywordResponse.getBody() != null) {
                                List<Map<String, Object>> keywords = (List<Map<String, Object>>) keywordResponse.getBody().get("results");
                                if (!keywords.isEmpty()) {
                                    String keywordId = keywords.get(0).get("id").toString();
                                    pageUrl = UriComponentsBuilder.fromHttpUrl(pageUrl)
                                        .queryParam("with_keywords", keywordId)
                                        .build()
                                        .toUriString();
                                }
                            }
                        }
                        
                        ResponseEntity<Map> pageResponse = restTemplate.getForEntity(pageUrl, Map.class);
                        if (pageResponse.getStatusCode() == HttpStatus.OK && pageResponse.getBody() != null) {
                            List<Map<String, Object>> results = (List<Map<String, Object>>) pageResponse.getBody().get("results");
                            return results.stream()
                                .map(this::convertToMovieDto)
                                .collect(Collectors.toList());
                        }
                        return new ArrayList<MovieDto>();
                    }));
                }
                
                // 모든 결과 수집
                for (Future<List<MovieDto>> future : futures) {
                    try {
                        allMovies.addAll(future.get());
                    } catch (Exception e) {
                        System.err.println("페이지 처리 중 오류 발생: " + e.getMessage());
                    }
                }
                
                executor.shutdown();
                
                // 즐겨찾기 수를 한 번에 조회
                Map<Long, Long> favoriteCounts = favoriteService.getFavoriteCounts(
                    allMovies.stream()
                        .map(MovieDto::getId)
                        .collect(Collectors.toList())
                );
                
                // 즐겨찾기 수로 필터링
                List<MovieDto> filteredMovies = allMovies.stream()
                    .filter(movie -> {
                        Long favoriteCount = favoriteCounts.getOrDefault(movie.getId(), 0L);
                        return favoriteCount >= min_favorite_count;
                    })
                    .collect(Collectors.toList());
                
                // 필터링된 결과에 대해 페이지네이션 적용
                int totalFiltered = filteredMovies.size();
                searchResponse.setTotal_results(totalFiltered);
                searchResponse.setTotal_pages((int) Math.ceil((double) totalFiltered / 20));
                
                int startIndex = ((page != null ? page : 1) - 1) * 20;
                int endIndex = Math.min(startIndex + 20, totalFiltered);
                if (startIndex < totalFiltered) {
                    searchResponse.setResults(filteredMovies.subList(startIndex, endIndex));
                } else {
                    searchResponse.setResults(new ArrayList<>());
                }
            } else {
                // 즐겨찾기 수 필터링이 없는 경우 기존 로직 사용
                searchResponse.setPage((Integer) response.getBody().get("page"));
                searchResponse.setTotal_pages((Integer) response.getBody().get("total_pages"));
                searchResponse.setTotal_results((Integer) response.getBody().get("total_results"));
                
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                List<MovieDto> movies = results.stream()
                    .map(this::convertToMovieDto)
                    .collect(Collectors.toList());
                
                searchResponse.setResults(movies);
            }
            
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
        
        // 추가 정보 설정
        if (movie.get("runtime") != null) {
            dto.setRuntime(((Number) movie.get("runtime")).intValue());
        }
        
        if (movie.get("original_language") != null) {
            dto.setOriginal_language((String) movie.get("original_language"));
        }
        
        if (movie.get("original_title") != null) {
            dto.setOriginal_title((String) movie.get("original_title"));
        }
        
        if (movie.get("adult") != null) {
            dto.setAdult((Boolean) movie.get("adult"));
        }
        
        // 장르 정보 설정
        if (movie.get("genres") != null) {
            List<Map<String, Object>> genresList = (List<Map<String, Object>>) movie.get("genres");
            List<GenreDto> genres = genresList.stream()
                .map(genre -> {
                    GenreDto genreDto = new GenreDto();
                    genreDto.setId(Long.valueOf(genre.get("id").toString()));
                    genreDto.setName((String) genre.get("name"));
                    return genreDto;
                })
                .collect(Collectors.toList());
            dto.setGenres(genres);
        }
        
        // 키워드 정보 설정
        if (movie.get("keywords") != null) {
            Map<String, Object> keywordsMap = (Map<String, Object>) movie.get("keywords");
            dto.setKeywords(keywordsMap);
            
            if (keywordsMap.get("keywords") != null) {
                List<Map<String, Object>> keywordsList = (List<Map<String, Object>>) keywordsMap.get("keywords");
                List<KeywordDto> keywords = keywordsList.stream()
                    .map(keyword -> {
                        KeywordDto keywordDto = new KeywordDto();
                        keywordDto.setId(Long.valueOf(keyword.get("id").toString()));
                        keywordDto.setName((String) keyword.get("name"));
                        return keywordDto;
                    })
                    .collect(Collectors.toList());
                dto.setKeywordList(keywords);
            }
        }
        
        return dto;
    }

    public MovieSearchResponse getNowPlayingMovies(int page) {
        String url = String.format("%s/movie/now_playing?api_key=%s&language=ko-KR&page=%d", tmdbConfig.getBaseUrl(), tmdbConfig.getKey(), page);
        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public MovieSearchResponse getPopularMovies(int page) {
        String url = String.format("%s/movie/popular?api_key=%s&language=ko-KR&page=%d", tmdbConfig.getBaseUrl(), tmdbConfig.getKey(), page);
        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public MovieSearchResponse getTopRatedMovies(int page) {
        String url = String.format("%s/movie/top_rated?api_key=%s&language=ko-KR&page=%d", tmdbConfig.getBaseUrl(), tmdbConfig.getKey(), page);
        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public MovieSearchResponse getUpcomingMovies(int page) {
        String url = String.format("%s/movie/upcoming?api_key=%s&language=ko-KR&page=%d", tmdbConfig.getBaseUrl(), tmdbConfig.getKey(), page);
        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public MovieSearchResponse getKoreanPopularMovies(int page) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/discover/movie")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .queryParam("with_original_language", "ko")
            .queryParam("sort_by", "popularity.desc")
            .queryParam("page", page);
        
        String url = urlBuilder.build().toUriString();
        System.out.println("TMDB API URL (Korean): " + url);
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            MovieSearchResponse searchResponse = new MovieSearchResponse();
            searchResponse.setPage((Integer) response.getBody().get("page"));
            searchResponse.setTotal_pages((Integer) response.getBody().get("total_pages"));
            searchResponse.setTotal_results((Integer) response.getBody().get("total_results"));
            
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            List<MovieDto> movies = results.stream()
                .map(this::convertToMovieDto)
                .collect(Collectors.toList());
            
            searchResponse.setResults(movies);
            return searchResponse;
        }
        return new MovieSearchResponse();
    }

    public List<MovieDto> searchMoviesForAutocomplete(String query) {
        if (query == null || query.length() < 2) {
            return new ArrayList<>();
        }

        List<MovieDto> allResults = new ArrayList<>();
        int maxPages = 3; // 최대 3페이지까지 검색

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
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                    if (results != null && !results.isEmpty()) {
                        List<MovieDto> pageResults = results.stream()
                            .map(this::convertToMovieDto)
                            .collect(Collectors.toList());
                        allResults.addAll(pageResults);
                    } else {
                        break; // 결과가 없으면 더 이상 페이지를 검색하지 않음
                    }
                }
            } catch (Exception e) {
                System.err.println("자동완성 검색 중 오류 발생: " + e.getMessage());
                break;
            }
        }

        // 최대 10개의 결과만 반환
        return allResults.stream()
            .limit(10)
            .collect(Collectors.toList());
    }

    public List<MovieDto> getRecommendedMovies(List<String> genres, List<String> keywords) {
        System.out.println("TMDB 추천 영화 검색 시작");
        System.out.println("입력된 장르: " + genres);
        System.out.println("입력된 키워드: " + keywords);

        UriComponentsBuilder urlBuilder = UriComponentsBuilder
            .fromHttpUrl(tmdbConfig.getBaseUrl() + "/discover/movie")
            .queryParam("api_key", tmdbConfig.getKey())
            .queryParam("language", "ko-KR")
            .queryParam("sort_by", "popularity.desc");

        // 장르 ID 매핑
        if (!genres.isEmpty()) {
            List<String> genreIds = genres.stream()
                .map(genre -> {
                    String id = switch (genre.toLowerCase()) {
                        case "액션" -> "28";
                        case "모험" -> "12";
                        case "애니메이션" -> "16";
                        case "코미디" -> "35";
                        case "범죄" -> "80";
                        case "다큐멘터리" -> "99";
                        case "드라마" -> "18";
                        case "가족" -> "10751";
                        case "판타지" -> "14";
                        case "역사" -> "36";
                        case "공포" -> "27";
                        case "음악" -> "10402";
                        case "미스터리" -> "9648";
                        case "로맨스" -> "10749";
                        case "SF" -> "878";
                        case "TV 영화" -> "10770";
                        case "스릴러" -> "53";
                        case "전쟁" -> "10752";
                        case "서부" -> "37";
                        default -> null;
                    };
                    System.out.println("장르 매핑: " + genre + " -> " + id);
                    return id;
                })
                .filter(id -> id != null)
                .collect(Collectors.toList());
            
            if (!genreIds.isEmpty()) {
                String genreParam = String.join(",", genreIds);
                System.out.println("장르 파라미터: " + genreParam);
                urlBuilder.queryParam("with_genres", genreParam);
            }
        }

        // 키워드 검색
        if (!keywords.isEmpty()) {
            List<String> keywordIds = new ArrayList<>();
            for (String keyword : keywords) {
                System.out.println("키워드 검색 시작: " + keyword);
                String keywordSearchUrl = UriComponentsBuilder
                    .fromHttpUrl(tmdbConfig.getBaseUrl() + "/search/keyword")
                    .queryParam("api_key", tmdbConfig.getKey())
                    .queryParam("query", keyword)
                    .build()
                    .toUriString();

                try {
                    ResponseEntity<Map> keywordResponse = restTemplate.getForEntity(keywordSearchUrl, Map.class);
                    if (keywordResponse.getStatusCode() == HttpStatus.OK && keywordResponse.getBody() != null) {
                        List<Map<String, Object>> keywordResults = (List<Map<String, Object>>) keywordResponse.getBody().get("results");
                        if (!keywordResults.isEmpty()) {
                            String keywordId = keywordResults.get(0).get("id").toString();
                            System.out.println("키워드 ID 찾음: " + keyword + " -> " + keywordId);
                            keywordIds.add(keywordId);
                        } else {
                            System.out.println("키워드 검색 결과 없음: " + keyword);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("키워드 검색 중 오류 발생: " + e.getMessage());
                }
            }

            if (!keywordIds.isEmpty()) {
                String keywordParam = String.join(",", keywordIds);
                System.out.println("키워드 파라미터: " + keywordParam);
                urlBuilder.queryParam("with_keywords", keywordParam);
            }
        }

        urlBuilder.queryParam("page", 1);
        String url = urlBuilder.build().toUriString();
        System.out.println("TMDB API URL: " + url);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                if (results != null) {
                    List<MovieDto> movies = results.stream()
                        .map(this::convertToMovieDto)
                        .limit(20)
                        .collect(Collectors.toList());
                    System.out.println("검색된 영화 수: " + movies.size());
                    return movies;
                }
            }
            System.out.println("TMDB API 응답이 없거나 결과가 없습니다.");
        } catch (Exception e) {
            System.err.println("추천 영화 검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
} 