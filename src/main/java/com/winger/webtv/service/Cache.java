package com.winger.webtv.service;

import com.winger.webtv.entity.Movie;
import com.winger.webtv.repositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class Cache {

    private List<Movie> cachedMovies = new ArrayList<>();
    private final MoviesRepository repository;

    @Autowired
    public Cache(MoviesRepository repository) {
        this.repository = repository;
    }

    public List<Movie> getCachedMovies() {
        if (cachedMovies.isEmpty()){
           fillCache();
        }
        return cachedMovies;
    }

    public int getCachedMoviesSize(){
        return cachedMovies.size();
    }

    public synchronized void fillCache() {
        this.cachedMovies = repository
                .findAll(Sort.by(Sort.Direction.DESC, "peers"))
                .stream().distinct().toList();

        System.out.println("Cache is initialized. Number of items: " + cachedMovies.size());
    }

    public Movie getById (String id){
        return repository.findById(id).orElse(new Movie());
    }
}
