package com.pixelbloom.movie_catalog_service.Repository;


import com.pixelbloom.movie_catalog_service.Model.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieInfo, Long> {
}
