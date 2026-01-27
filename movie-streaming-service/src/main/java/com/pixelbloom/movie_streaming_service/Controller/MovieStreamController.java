package com.pixelbloom.movie_streaming_service.Controller;

import com.pixelbloom.movie_streaming_service.Service.MovieCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

@RestController
public class MovieStreamController {

    @Autowired
    public MovieCatalogService movieCatalogService;


    public static final Logger logger = Logger.getLogger(MovieStreamController.class.getName());
    public static final String VIDEO_DIRECTORY = "D:\\stream\\";

    @GetMapping("/stream/{videoPath}")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable String videoPath) throws FileNotFoundException {
        // Logic to retrieve movie stream from the service
        File videoFile = new File(VIDEO_DIRECTORY + videoPath);
        if (videoFile.exists()) {
            InputStreamResource streamingService = new InputStreamResource(new FileInputStream(videoFile));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(streamingService);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

       @GetMapping("/stream/with-id/{videoInfoId}")
               public ResponseEntity<InputStreamResource> streamVideoWithId(@PathVariable Long videoInfoId) throws FileNotFoundException {
           // Logic to retrieve movie stream from the service
           String videoPath = movieCatalogService.getMoviePathById(videoInfoId);
           logger.log(java.util.logging.Level.INFO, "Resolved movie Path is:={0} " + videoPath);
        return streamVideo(videoPath);
          }
}
