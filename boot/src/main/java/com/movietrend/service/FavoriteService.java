package com.movietrend.service;

import com.movietrend.entity.Favorite;
import com.movietrend.entity.User;
import com.movietrend.repository.FavoriteRepository;
import com.movietrend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Long> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return favoriteRepository.findByUserId(user.getId())
            .stream()
            .map(Favorite::getMovieId)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return favoriteRepository.existsByUserIdAndMovieId(user.getId(), movieId);
    }

    @Transactional(readOnly = true)
    public long getFavoriteCount(Long movieId) {
        return favoriteRepository.countByMovieId(movieId);
    }

    @Transactional
    public void toggleFavorite(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        favoriteRepository.findByUserIdAndMovieId(user.getId(), movieId)
            .ifPresentOrElse(
                favorite -> favoriteRepository.delete(favorite),
                () -> {
                    Favorite newFavorite = new Favorite();
                    newFavorite.setUser(user);
                    newFavorite.setMovieId(movieId);
                    favoriteRepository.save(newFavorite);
                }
            );
    }
} 