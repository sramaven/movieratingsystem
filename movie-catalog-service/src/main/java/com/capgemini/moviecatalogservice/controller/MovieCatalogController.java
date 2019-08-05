package com.capgemini.moviecatalogservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.capgemini.moviecatalogservice.model.CatalogClass;
import com.capgemini.moviecatalogservice.model.Movie;
import com.capgemini.moviecatalogservice.model.MovieCatalog;
import com.capgemini.moviecatalogservice.model.Rating;
import com.capgemini.moviecatalogservice.model.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/{userId}")
	public MovieCatalog getMovieCatalog(@PathVariable String userId)
	{
		//call movie-rating-service and get the rating details
		UserRating ratings=restTemplate.getForObject("http://movie-rating-service/movierating/"+userId, UserRating.class);
		
		List<Rating> movieRatings=ratings.getRatings();
		List<CatalogClass> catalogItems=new ArrayList<CatalogClass>();
		
		//call movie-service and get movie details
	    for(Rating movieRating:movieRatings)
	    {
	    	Movie movie=
	    			restTemplate.getForObject("http://movie-info-service/movies/"+movieRating.getMovieId(), Movie.class);
	    	 
	          catalogItems.add(new CatalogClass(movie,movieRating.getRating())); 	
	    }
		
		//We have to return MovieCatalog to client
	    MovieCatalog movieCatalog=new MovieCatalog(catalogItems);
	    return movieCatalog;
	
	}

}
