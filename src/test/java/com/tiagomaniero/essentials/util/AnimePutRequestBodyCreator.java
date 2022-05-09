package com.tiagomaniero.essentials.util;

import com.tiagomaniero.essentials.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimeToBeUpadted(){
        return AnimePutRequestBody.builder()
                .id(AnimeCreator.createValidUpdateddAnime().getId())
                .name(AnimeCreator.createValidUpdateddAnime().getName())
                .build();
    }
}
