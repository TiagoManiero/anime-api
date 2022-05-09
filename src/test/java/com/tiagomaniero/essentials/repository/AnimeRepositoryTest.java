package com.tiagomaniero.essentials.repository;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.util.AnimeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save creates anime when successful")
    public void savePersistAnimeWhenSuccessful(){
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        Assertions.assertThat(savedAnime).isNotNull();
        Assertions.assertThat(savedAnime.getId()).isNotNull();
        Assertions.assertThat(savedAnime.getName()).isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("Update anime when successful")
    public void saveUpdateAnimeWhenSuccessful(){
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        savedAnime.setName("Overlord");
        Anime updatedAnime = this.animeRepository.save(savedAnime);
        Assertions.assertThat(updatedAnime).isNotNull();
        Assertions.assertThat(updatedAnime.getId()).isNotNull();
        Assertions.assertThat(updatedAnime.getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("Delete removes anime when successful")
    public void deleteRemovesAnimeWhenSuccessful(){
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        this.animeRepository.delete(savedAnime);
        Optional<Anime> animeOptional = this.animeRepository.findById(savedAnime.getId());

        Assertions.assertThat(animeOptional).isEmpty();

    }

    @Test
    @DisplayName("Find by name returns list of animes when successful")
    public void findByNameReturnsListAnimeWhenSuccessful(){
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);

        String name = savedAnime.getName();
        List<Anime> animesByName = this.animeRepository.findByName(name);

        Assertions.assertThat(animesByName)
                .isNotEmpty()
                .contains(savedAnime);

    }

    @Test
    @DisplayName("Find by name returns empty list when anime is not found")
    public void findByNameReturnsEmptyListWhenNoAnimeIsFound(){
        List<Anime> animesByName = this.animeRepository.findByName("Xaxa");

        Assertions.assertThat(animesByName).isEmpty();

    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when name is empty")
    public void saveThrowsConstraintViolationExceptionWhenNameIsEmpty(){
        Anime anime = new Anime();
//        Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
//                .isInstanceOf(ConstraintViolationException.class);

        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.animeRepository.save(anime))
                .withMessageContaining("The name cannot be empty");
    }



}