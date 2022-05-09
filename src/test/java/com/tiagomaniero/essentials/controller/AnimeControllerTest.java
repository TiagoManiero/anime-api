package com.tiagomaniero.essentials.controller;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.requests.AnimePostRequestBody;
import com.tiagomaniero.essentials.requests.AnimePutRequestBody;
import com.tiagomaniero.essentials.service.AnimeService;
import com.tiagomaniero.essentials.util.AnimeCreator;
import com.tiagomaniero.essentials.util.AnimePostRequestBodyCreator;
import com.tiagomaniero.essentials.util.AnimePutRequestBodyCreator;
import com.tiagomaniero.essentials.util.DateUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks //classe que será testada
    private AnimeController animeController;

    @Mock //dependências
    private AnimeService animeServiceMock;

    @Mock
    private DateUtil dateUtil;

    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdorThrowBadRequest(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing()
                .when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing()
                .when(animeServiceMock).deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("List returns list of anime inside page when successful")
    void listReturnsListOfAnimesInsidePageObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void listAllReturnsListOfAnimesObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.listAll().getBody();

        Assertions.assertThat(animeList).isNotNull();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindById returns list of anime when successful")
    void findByIdReturnsListOfAnimesObjectWhenSuccessful(){
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(1L).getBody();

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("FindByName returns list of anime when successful")
    void findByNameReturnsListOfAnimesObjectWhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.findByName("anime").getBody();

        Assertions.assertThat(animeList).isNotNull();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindByName returns empty list of anime when not found")
    void findByNameReturnsEmptyListOfAnimesObjectNotFound(){
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeController.findByName("anime").getBody();

        Assertions.assertThat(animeList).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("Save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful(){
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimeToBeSaved()).getBody();

        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());

    }

    @Test
    @DisplayName("Replace returns void and update anime when successful")
    void replaceupadteAnimeWhenSuccessful(){
        Assertions.assertThatCode(() ->
                animeController.replace(AnimePutRequestBodyCreator.createAnimeToBeUpadted()))
                .doesNotThrowAnyException();

        ResponseEntity<Void> voidResponseEntity = animeController
                .replace(AnimePutRequestBodyCreator.createAnimeToBeUpadted());

        Assertions.assertThat(voidResponseEntity).isNotNull();
        Assertions.assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("Delete returns void and removes anime when successful")
    void deleteRemovesAnimeWhenSuccessful(){
        Assertions.assertThatCode(() ->
                animeController.delete(1L))
                .doesNotThrowAnyException();

        ResponseEntity<Void> voidResponseEntity = animeController
                .delete(1L);

        Assertions.assertThat(voidResponseEntity).isNotNull();
        Assertions.assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

}