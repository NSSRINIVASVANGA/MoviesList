package com.example.movie.service;

import com.example.movie.model.*;
import com.example.movie.repository.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MovieH2Service implements MovieRepository {
    @Autowired
    private JdbcTemplate db;

    @Override
    public ArrayList<Movie> getMovies() {
        List<Movie> movieList = db.query("SELECT * FROM MOVIELIST", new MovieRowMapper());
        ArrayList<Movie> movies = new ArrayList<>(movieList);
        return movies;
    }

    @Override
    public Movie addMovie(Movie movie) {
        db.update("INSERT INTO MOVIELIST(movieName,leadActor) VALUES(?,?)", movie.getMovieName(), movie.getLeadActor());
        Movie savedMovie = db.queryForObject("SELECT * FROM MOVIELIST WHERE movieName LIKE ? AND leadActor LIKE ?",
                new MovieRowMapper(), movie.getMovieName(), movie.getLeadActor());
        return savedMovie;
    }

    @Override
    public Movie getMovieById(int movieId) {
        try {
            Movie movie = db.queryForObject("SELECT * FROM MOVIELIST WHERE movieId = ?", new MovieRowMapper(), movieId);
            return movie;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Movie updateMovie(int movieId, Movie movie) {
        if (movie.getMovieName() != null) {
            db.update("UPDATE MOVIELIST SET movieName = ? WHERE movieId = ?", movie.getMovieName(), movieId);
        }
        if (movie.getLeadActor() != null) {
            db.update("UPDATE MOVIELIST SET leadActor = ? WHERE movieId = ?", movie.getLeadActor(), movieId);
        }
        return getMovieById(movieId);
    }

    @Override
    public void deleteMovie(int movieId) {
        db.update("DELETE FROM MOVIELIST WHERE movieId = ?", movieId);
    }
}
