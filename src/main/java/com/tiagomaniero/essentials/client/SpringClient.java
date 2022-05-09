package com.tiagomaniero.essentials.client;

import com.tiagomaniero.essentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {

    public static void main(String[] args) {
        String url = "http://localhost:8080/api/v1/animes";
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity(
                url+"/2", Anime.class);

        Anime object = new RestTemplate().getForObject(
                url+"/{id}", Anime.class,2);

        log.info(entity);
        log.info(object);

        Anime[] animes = new RestTemplate().getForObject(
                url+"/all", Anime[].class);

        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> animeList = new RestTemplate().exchange(
                url+"/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        log.info(animeList.getBody());
        Anime kingdom = Anime.builder().name("kingdom").build();

        Anime kingdomSave = new RestTemplate().postForObject(
                url,
                kingdom,
                Anime.class);
        log.info(kingdomSave);

        Anime samurai = Anime.builder().name("samurai").build();
        ResponseEntity<Anime> samuraiSave = new RestTemplate().exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(samurai, createJsonHeader()),
                Anime.class);
        log.info(samuraiSave);

        Anime samuraiChange = samuraiSave.getBody();
        samuraiChange.setName("Samurai 2");
        ResponseEntity<Void> samuraiUpdated = new RestTemplate().exchange(url,
                HttpMethod.PUT,
                new HttpEntity<>(samuraiChange, createJsonHeader()),
                Void.class);
        log.info(samuraiUpdated);

        ResponseEntity<Void> samuraiDeleted = new RestTemplate().exchange(url+"/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                samuraiChange.getId());
        log.info(samuraiDeleted);

    }

    private static HttpHeaders createJsonHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;

    }
}
