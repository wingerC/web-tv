package com.winger.webtv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winger.webtv.entity.Movie;
import com.winger.webtv.repositories.MoviesRepository;
import com.winger.webtv.service.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@SuppressWarnings("unused")
public class MainRouter {

    @Autowired
    MoviesRepository repository;
    @Autowired
    Cache cache;

    @GetMapping("/list")
    @ResponseBody()
    public List<Movie> list(@RequestParam int offset, @RequestParam int length){

        int total = offset + length;
        return cache.getCachedMovies().subList(offset, Math.min(total, cache.getCachedMoviesSize()));
    }

    @GetMapping("/list_size")
    @ResponseBody()
    public int getListSize(@RequestParam String category){
        return cache.getCachedMoviesSize();
    }

    @GetMapping("/find")
    @ResponseBody
    //@CrossOrigin
    public List<Movie> searchTest(@RequestParam String title){
        return
            cache.getCachedMovies().stream().filter(m-> m.getEnTitle().toLowerCase().contains(title.toLowerCase())
                    || m.getRuTitle().toLowerCase().contains(title.toLowerCase())).toList();

    }

    @PostMapping("/fav")
    @ResponseBody
    public List<Movie> findByTitle (@RequestBody String req) throws JsonProcessingException {
        List<Movie> resList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String [] arr = mapper.readValue(req, String[].class);
        Arrays.stream(arr).forEach(s-> cache.getCachedMovies()
                .stream()
                .filter(m-> m.getEnTitle().equals(s))
                .findFirst()
                .ifPresent(resList::add));

        return resList;
    }

    @GetMapping("/movie")
    @ResponseBody
    public Movie movieInfo (@RequestParam String id){
        return cache.getById(id);
    }

    @GetMapping("/alt")
    @ResponseBody
    public List<Movie> findAltLinks (@RequestParam String title){
       return repository.findByEnTitle(title);
    }
}
