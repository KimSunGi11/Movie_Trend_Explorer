package com.movietrend.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.Data;

@Data
@Document(indexName = "movies")
@Setting(settingPath = "elasticsearch/movie-settings.json")
public class MovieDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String originalTitle;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String overview;

    @Field(type = FieldType.Keyword)
    private String posterPath;

    @Field(type = FieldType.Keyword)
    private String backdropPath;

    @Field(type = FieldType.Keyword)
    private String releaseDate;

    @Field(type = FieldType.Double)
    private Double voteAverage;

    @Field(type = FieldType.Integer)
    private Integer voteCount;

    @Field(type = FieldType.Text)
    private Double popularity;

    @Field(type = FieldType.Text)
    private String[] suggestions;
} 