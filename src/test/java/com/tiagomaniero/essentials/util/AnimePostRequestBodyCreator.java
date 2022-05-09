package com.tiagomaniero.essentials.util;

import com.tiagomaniero.essentials.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody createAnimeToBeSaved(){
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createAnimeToBeSaved().getName())
                .build();
    }
}
