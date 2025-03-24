package com.movietrend.controller;

import com.movietrend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<Long>> getUserFavorites(Authentication authentication) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(authentication.getName()));
    }

    @GetMapping("/{movieId}/count")
    public ResponseEntity<Long> getFavoriteCount(@PathVariable Long movieId) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(movieId));
    }

    @GetMapping("/{movieId}/check")
    public ResponseEntity<Boolean> checkFavorite(
            @PathVariable Long movieId,
            Authentication authentication) {
        return ResponseEntity.ok(favoriteService.isFavorite(authentication.getName(), movieId));
    }

    @PostMapping("/{movieId}/toggle")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long movieId,
            Authentication authentication) {
        favoriteService.toggleFavorite(authentication.getName(), movieId);
        return ResponseEntity.ok().build();
    }
} 