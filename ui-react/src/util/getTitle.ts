import type { Anime } from './types';

function getTitle(anime: Anime) {
    return anime.englishTitle ?? anime.romajiTitle ?? anime.nativeTitle ?? '';
}

export default getTitle;
