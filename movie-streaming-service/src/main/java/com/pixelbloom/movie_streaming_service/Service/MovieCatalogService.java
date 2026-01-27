package com.pixelbloom.movie_streaming_service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class MovieCatalogService {

    @Autowired
    private RestTemplate restTemplate;


    public static final String CATALOG_SERVICE="http://movie-catalog-service";
    public String getMoviePathById(Long movieId) {
        var response= restTemplate.getForEntity(CATALOG_SERVICE+ "/movie-info/getByPathId/{movieId}", String.class, movieId);
        return response.getBody();
    }


}
