import APIError from '../errors/APIError';

import type { Season } from '../util/types';

const BASE_API_URL = import.meta.env.VITE_BASE_API_URL;
const SCHEDULE_API_PATH = import.meta.env.VITE_SCHEDULE_API_PATH;

async function fetchAnime(season?: Season, year?: number, includeAdultContent?: boolean) {
    const url = new URL(`${SCHEDULE_API_PATH}/anime`, BASE_API_URL);
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
