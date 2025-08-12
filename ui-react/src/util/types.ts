export type Season = 'WINTER' | 'SPRING' | 'SUMMER' | 'FALL';

export interface Anime {
    id?: number;
    nativeTitle?: string;
    romajiTitle?: string;
    englishTitle?: string;
    description?: string;
    episodes?: number;
    season?: Season;
    year?: number;
    nextEpisodeAiringAt?: number;
    nextAiringEpisode?: number;
    url?: string;
    thumbnailExtraLarge?: string;
    thumbnailLarge?: string;
}

export interface State {
    anime: Anime[];
    season: Season;
    year: number;
    seasonRange: string[];
    timezone: string;
    error?: Error;
}

export interface Action {
    type: string;
    payload?: unknown;
}
