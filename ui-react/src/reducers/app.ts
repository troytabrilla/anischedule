import _debug from 'debug';

import dayjs from '../util/dayjs';

import type { Action, Season, State } from '../util/types';

const debug = _debug('reducers:app');

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case 'anime': {
            const response = action.payload as Pick<State, 'anime'>;
            return {
                ...state,
                anime: response.anime,
            };
        }
        case 'error': {
            const error = action.payload as Error;
            debug(error.toString());
            return {
                ...state,
                error,
            };
        }
        case 'season': {
            const updates = action.payload as Pick<State, 'season' | 'year'>;
            return {
                ...state,
                season: updates.season,
                year: updates.year,
            };
        }
    }
    throw new Error(`Unknown Action: ${action.type}`);
}

export function initialState(): State {
    const now = dayjs();
    const month = now.month();
    const year = now.year();
    return {
        anime: [],
        season: monthToSeason(month),
        year,
        seasonRange: getSeasonRange(month, year),
        timezone: dayjs.tz.guess(),
    };
}

function monthToSeason(month: number): Season {
    switch (month) {
        case 0:
        case 1:
        case 2:
            return 'WINTER';
        case 3:
        case 4:
        case 5:
            return 'SPRING';
        case 6:
        case 7:
        case 8:
            return 'SUMMER';
        default:
            return 'FALL';
    }
}

function getSeasonRange(month: number, year: number): string[] {
    const season = monthToSeason(month);
    const currentSeason = `${season} ${year}`;
    const previousSeason = `${monthToSeason(rotateMonths(month, -3))} ${season === 'WINTER' ? year - 1 : year}`;
    const nextSeason = `${monthToSeason(rotateMonths(month, 3))} ${season === 'FALL' ? year + 1 : year}`;
    return [previousSeason, currentSeason, nextSeason];
}

function rotateMonths(month: number, offset: number): number {
    return (((month + offset) % 12) + 12) % 12;
}

export default reducer;
