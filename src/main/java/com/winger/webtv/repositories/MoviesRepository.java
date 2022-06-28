package com.winger.webtv.repositories;

import com.winger.webtv.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, String> {

    @Query("select p from Movie p " +
            "where lower(p.ruTitle) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(p.enTitle) like lower(concat('%', :searchTerm, '%'))")
    List<Movie> searchByTitle(@Param("searchTerm") String searchTerm);

    List<Movie> findByEnTitle(String title);
}
