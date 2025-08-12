import APIError from '../errors/APIError';

import type { Season } from '../util/types';

const BASE_API_URL = import.meta.env.VITE_BASE_API_URL;

async function fetchAnime(season?: Season, year?: number, includeAdultContent?: boolean) {
    const url = new URL('/anime', BASE_API_URL);
    if (season) {
        url.searchParams.append('season', season);
    }
    if (year) {
        url.searchParams.append('year', `${year}`);
    }
    if (includeAdultContent) {
        url.searchParams.append('includeAdultContent', 'true');
    }
    const response = await fetch(url);
    const json = await response.json();
    if (!response.ok) {
        throw new APIError('Invalid API Response', json);
    }
    return json;
}

export default fetchAnime;
