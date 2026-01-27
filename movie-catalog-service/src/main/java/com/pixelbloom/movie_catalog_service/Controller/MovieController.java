package com.pixelbloom.movie_catalog_service.Controller;


import com.pixelbloom.movie_catalog_service.Model.MovieInfo;
import com.pixelbloom.movie_catalog_service.Repository.MovieRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MovieController {

 @Autowired
 MovieRepository MovieRepository;


         @PostMapping("/movie-info/save")
        public List<MovieInfo> saveAll(@RequestBody List<MovieInfo> movieInfoList){
        return MovieRepository.saveAll(movieInfoList);
    }

    @GetMapping("/movie-info/getAll")
    public List<MovieInfo> getAll(){
        return MovieRepository.findAll();
    }

    @GetMapping("/movie-info/getByPathId/{movieId}")
    public String getById(@PathVariable Long movieId) {

        var movieIdOptional = MovieRepository.findById(movieId);
        if (movieIdOptional.isPresent()) {

        }
        return movieIdOptional.map(MovieInfo::getPath).orElse(null);
    }

}
