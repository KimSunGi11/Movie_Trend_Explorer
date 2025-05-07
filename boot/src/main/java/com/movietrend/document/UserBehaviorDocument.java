package com.movietrend.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(indexName = "user_behaviors")
public class UserBehaviorDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Long)
    private Long movieId;

    @Field(type = FieldType.Keyword)
    private String movieTitle;

    @Field(type = FieldType.Keyword)
    private List<String> genres;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private LocalDate viewedAt;

    @Field(type = FieldType.Integer)
    private Integer viewDuration; // 영화 상세 페이지 체류 시간(초)

    @Field(type = FieldType.Boolean)
    private Boolean isFavorite;

    @Field(type = FieldType.Double)
    private Double userRating;
} 