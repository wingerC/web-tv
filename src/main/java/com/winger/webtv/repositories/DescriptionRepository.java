package com.winger.webtv.repositories;

import com.winger.webtv.entity.Description;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DescriptionRepository extends JpaRepository<Description, Integer> {

    Optional<Description> findByTitle(String title);
}
