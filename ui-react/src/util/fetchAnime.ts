import APIError from '../errors/APIError';

import type { Season } from '../reducers/app';

const BASE_API_URL = import.meta.env.VITE_API_URL;

async function fetchAnime(season?: Season, year?: number) {
    const url = new URL('/anime', BASE_API_URL);
    if (season) {
        url.searchParams.append('season', season);
    }
    if (year) {
        url.searchParams.append('year', `${year}`);
    }
    const response = await fetch(url);
    const json = await response.json();
    if (!response.ok) {
        throw new APIError('Invalid API Response', json);
    }
    return json;
}

export default fetchAnime;
