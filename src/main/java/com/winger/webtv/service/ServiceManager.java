package com.winger.webtv.service;

import com.winger.webtv.entity.Description;
import com.winger.webtv.entity.Movie;
import com.winger.webtv.repositories.DescriptionRepository;
import com.winger.webtv.repositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServiceManager {

    MoviesRepository moviesRepository;
    DescriptionRepository descriptionRepository;
    Cache cache;
    DescriptionGetter descriptionGetter;
    Integer pagesToLoad;

    @Autowired
    public ServiceManager(MoviesRepository moviesRepository, DescriptionRepository descriptionRepository,
                          Cache cache, DescriptionGetter descriptionGetter) {
        this.moviesRepository = moviesRepository;
        this.descriptionRepository = descriptionRepository;
        this.cache = cache;
        this.descriptionGetter = descriptionGetter;
        try {
        this.pagesToLoad = Integer.parseInt(System.getenv().get("MAX_PAGE"));
        }catch (Exception e){
            System.out.println("No property MAX_PAGE, set to 1. Err: " + e.getMessage());
            this.pagesToLoad = 1;
        }

    }

    public void refreshContent(){
        List<Movie> list = new Aggregator(moviesRepository).getMovies(pagesToLoad);
        List<String> idList = new ArrayList<>();
        System.out.println("-------------");

        list.forEach(movie -> {
                    idList.add(movie.getId());
                    Optional<Description> optionalDescription = descriptionRepository.findByTitle(movie.getEnTitle());
                    optionalDescription.ifPresentOrElse(description -> {
                        movie.setDescription(description);
                        moviesRepository.save(movie);
                            },
                            () -> {
                                Description newDescription;
                                try {
                                    //System.out.println("New description in progress: " + movie.getEnTitle());
                                    newDescription = descriptionGetter.getDescription(movie);
                                    descriptionRepository.save(newDescription);
                                    movie.setDescription(newDescription);
                                    moviesRepository.save(movie);
                                    System.out.println("New Description added: " + newDescription.getTitle());
                                } catch (IOException | InterruptedException e) {
                                    System.out.println("Exception in Runnable clause on Description parsing");
                                }
                            });
                });

        filterStorage(idList);

    }

    public void filterStorage (List<String> list){
        AtomicInteger count = new AtomicInteger();
        moviesRepository.findAll().forEach(movie-> {
            if (!list.contains(movie.getId()) && movie.getPeers() != 11 &&  movie.getPeers() > 20) {
                System.out.println("Repiring: " + movie.getId());
                movie.setPeers(11);
                moviesRepository.saveAndFlush(movie);
                count.getAndIncrement();
            }
        });
        System.out.println("^^^^^^^^^^^^^^^^^^^");
        System.out.println("Repeering items: " + count);
    }

    public void fillCacheList (){
        cache.fillCache();
    }
}
