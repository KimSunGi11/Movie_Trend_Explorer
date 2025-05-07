package com.movietrend.repository.elasticsearch;

import com.movietrend.document.UserBehaviorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.Optional;
 
public interface UserBehaviorRepository extends ElasticsearchRepository<UserBehaviorDocument, String> {
    Optional<UserBehaviorDocument> findByUsernameAndMovieId(String username, Long movieId);
} 