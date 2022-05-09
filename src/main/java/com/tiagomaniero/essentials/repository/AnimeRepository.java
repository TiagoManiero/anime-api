package com.tiagomaniero.essentials.repository;

import com.tiagomaniero.essentials.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimeRepository extends JpaRepository<Anime, Long> {

    List<Anime> findByName(String name);

}
