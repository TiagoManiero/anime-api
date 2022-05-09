package com.tiagomaniero.essentials.integration;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.domain.DatabaseUser;
import com.tiagomaniero.essentials.repository.AnimeRepository;
import com.tiagomaniero.essentials.repository.DatabaseUserRepository;
import com.tiagomaniero.essentials.requests.AnimePostRequestBody;
import com.tiagomaniero.essentials.util.AnimeCreator;
import com.tiagomaniero.essentials.util.AnimePostRequestBodyCreator;
import com.tiagomaniero.essentials.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

//    @LocalServerPort
//    private int port;
    
    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private DatabaseUserRepository databaseUserRepository;

    private static final DatabaseUser ADMIN = DatabaseUser.builder()
            .name("Administrador")
                .password("{bcrypt}$2a$10$hSTIR1LEGbkA6US1B0IJVeoTsHrFKzPwXSeE40SvIFckopmMHoUTm")
                .username("admin")
                .authorities("ROLE_USER, ROLE_ADMIN")
                .build();

    private static final DatabaseUser USER = DatabaseUser.builder()
            .name("Tiago")
            .password("{bcrypt}$2a$10$hSTIR1LEGbkA6US1B0IJVeoTsHrFKzPwXSeE40SvIFckopmMHoUTm")
            .username("tiago")
            .authorities("ROLE_USER")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("tiago", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:"+port)
                    .basicAuthentication("admin", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }
    }
    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange(
                "/api/v1/animes", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
        
    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void listAllReturnsListOfAnimesObjectWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange(
                "/api/v1/animes/all", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes)
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindById returns list of anime when successful")
    void findByIdReturnsListOfAnimesObjectWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        Long expectedId = savedAnime.getId();
        Anime anime = testRestTemplateRoleUser.getForObject("/api/v1/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("FindByName returns list of anime when successful")
    void findByNameReturnsListOfAnimesObjectWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        String url = String.format("/api/v1/animes?name=%s",expectedName);
        List<Anime> animes = testRestTemplateRoleUser.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes).isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("FindByName returns empty list of anime when not found")
    void findByNameReturnsEmptyListOfAnimesObjectNotFound(){
        databaseUserRepository.save(USER);
        List<Anime> animes = testRestTemplateRoleUser.exchange(
                "/api/v1/animes?name=dbz",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("Save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful(){
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimeToBeSaved();
        databaseUserRepository.save(ADMIN);
        ResponseEntity<Anime> anime = testRestTemplateRoleAdmin.postForEntity("/api/v1/animes",
                animePostRequestBody,
                Anime.class
                );

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(anime.getBody()).isNotNull();
        Assertions.assertThat(anime.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("Replace returns void and update anime when successful")
    void replaceupadteAnimeWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        savedAnime.setName("new name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/api/v1/animes",
                HttpMethod.PUT,
                new HttpEntity<>(savedAnime),
                Void.class
        );

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("Delete returns void and removes anime when successful")
    void deleteRemovesAnimeWhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(ADMIN);
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/api/v1/animes/admin/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId()
        );

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("Delete returns 403 when user is not admin")
    void deleteReturns403WhenUserIsNotAdmin(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        databaseUserRepository.save(USER);
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/api/v1/admin/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
