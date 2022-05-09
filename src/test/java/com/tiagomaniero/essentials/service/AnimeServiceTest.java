package com.tiagomaniero.essentials.service;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.exception.BadRequestException;
import com.tiagomaniero.essentials.repository.AnimeRepository;
import com.tiagomaniero.essentials.util.AnimeCreator;
import com.tiagomaniero.essentials.util.AnimePostRequestBodyCreator;
import com.tiagomaniero.essentials.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks //classe que será testada
    private AnimeService animeService;

    @Mock //dependências
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing()
                .when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("List returns list of anime inside page when successful")
    void listReturnsListOfAnimesInsidePageObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1,1));

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void listAllReturnsListOfAnimesObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAllNonPageable();

        Assertions.assertThat(animeList).isNotNull();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindById returns list of anime when successful")
    void findByIdReturnsListOfAnimesObjectWhenSuccessful(){
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findByIdorThrowBadRequest(1L);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("FindById throws BadRequestException when is not found")
    void findByIdThrownsBadRequestExceptionWhenAnimeIsNotFound(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdorThrowBadRequest(1L));

    }

    @Test
    @DisplayName("FindByName returns list of anime when successful")
    void findByNameReturnsListOfAnimesObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.findByName("anime");

        Assertions.assertThat(animeList).isNotNull();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindByName returns empty list of anime when not found")
    void findByNameReturnsEmptyListOfAnimesObjectNotFound(){
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeService.findByName("anime");

        Assertions.assertThat(animeList).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("Save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful(){
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimeToBeSaved());

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("Replace returns void and update anime when successful")
    void replaceupadteAnimeWhenSuccessful(){
        Assertions.assertThatCode(() ->
                animeService.replace(AnimePutRequestBodyCreator.createAnimeToBeUpadted()))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("Delete returns void and removes anime when successful")
    void deleteRemovesAnimeWhenSuccessful(){
        Assertions.assertThatCode(() ->
                animeService.deleteById(1L))
                .doesNotThrowAnyException();

    }

}