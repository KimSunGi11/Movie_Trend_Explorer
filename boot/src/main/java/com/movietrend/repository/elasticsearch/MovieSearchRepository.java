package com.movietrend.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.movietrend.document.MovieDocument;
import java.util.List;
 
public interface MovieSearchRepository extends ElasticsearchRepository<MovieDocument, Long> {
    List<MovieDocument> findByTitleContainingIgnoreCase(String title);
} 