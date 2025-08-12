package com.anischedule.records;

public record Anime(
    Integer id,
    String nativeTitle,
    String romajiTitle,
    String englishTitle,
    String description,
    Integer episodes,
    String season,
    Integer year,
    Integer nextEpisodeAiringAt,
    Integer nextAiringEpisode,
    String url,
    String thumbnailExtraLarge,
    String thumbnailLarge
) {}
