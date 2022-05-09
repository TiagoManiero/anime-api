package com.tiagomaniero.essentials.service;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.exception.BadRequestException;
import com.tiagomaniero.essentials.mapper.AnimeMapper;
import com.tiagomaniero.essentials.repository.AnimeRepository;
import com.tiagomaniero.essentials.requests.AnimePostRequestBody;
import com.tiagomaniero.essentials.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Page<Anime> listAll(Pageable pageable){
        return animeRepository.findAll(pageable);
    }

    public List<Anime> listAllNonPageable() {
        return animeRepository.findAll();
    }

    public List<Anime> findByName(String name){
        return animeRepository.findByName(name);
    }

    public Anime findByIdorThrowBadRequest(Long id){
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime ID not found"));
    }

    @Transactional(rollbackOn = Exception.class)
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        return animeRepository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
    }

    public void deleteById(Long id) {
        animeRepository.delete(findByIdorThrowBadRequest(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime savedAnime = findByIdorThrowBadRequest(animePutRequestBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        anime.setId(savedAnime.getId());
        animeRepository.save(anime);
    }

}
