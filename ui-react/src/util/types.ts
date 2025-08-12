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
    isAdult?: boolean;
}

export interface State {
    anime: Anime[];
    season: Season;
    year: number;
    seasonRange: string[];
    timezone: string;
    includeAdultContent: boolean;
    loading: boolean;
    error: Error | null;
}

export interface Action {
    type: string;
    payload?: unknown;
}
