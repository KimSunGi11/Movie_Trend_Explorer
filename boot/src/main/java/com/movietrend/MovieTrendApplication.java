package com.movietrend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.movietrend.service.MovieIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class MovieTrendApplication {

	private final MovieIndexingService movieIndexingService;

	public static void main(String[] args) {
		SpringApplication.run(MovieTrendApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			// 애플리케이션 시작 시 영화 데이터 인덱싱
			movieIndexingService.indexTrendingMovies();
		};
	}
} 