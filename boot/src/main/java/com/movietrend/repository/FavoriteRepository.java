package com.movietrend.repository;

import com.movietrend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndMovieId(Long userId, Long movieId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.movieId = :movieId")
    long countByMovieId(Long movieId);

    @Query("SELECT f.movieId, COUNT(f) FROM Favorite f WHERE f.movieId IN :movieIds GROUP BY f.movieId")
    List<Object[]> countByMovieIds(@Param("movieIds") List<Long> movieIds);
} 