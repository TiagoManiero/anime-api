package com.tiagomaniero.essentials.mapper;

import com.tiagomaniero.essentials.domain.Anime;
import com.tiagomaniero.essentials.requests.AnimePostRequestBody;
import com.tiagomaniero.essentials.requests.AnimePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {

    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);

    public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
