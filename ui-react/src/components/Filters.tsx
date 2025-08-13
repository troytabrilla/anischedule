import './Filters.css';

import type { ActionDispatch, ChangeEvent } from 'react';

import type { State, Action } from '../util/types';

type Props = Pick<State, 'season' | 'year' | 'seasonRange' | 'includeAdultContent'> & {
    dispatch: ActionDispatch<[Action]>;
};

function Filters({ season, year, seasonRange, includeAdultContent, dispatch }: Props) {
    const currentSeason = `${season} ${year}`;
    const handleSeasonChange = (e: ChangeEvent<HTMLSelectElement>) => {
        const [newSeason, newYear] = e.target.value.split(' ');
        dispatch({ type: 'season', payload: { season: newSeason, year: newYear } });
    };
    const adultContentFilterEnabled = localStorage.getItem('feature-flag:enable-adult-content-filter') === 'true';
    const handleAdultContentChange = (e: ChangeEvent<HTMLInputElement>) => {
        const newIncludeAdultContent = e.target.checked;
        dispatch({ type: 'includeAdultContent', payload: { includeAdultContent: newIncludeAdultContent } });
    };
    return (
        <section className="filters row centered justify-centered">
            <fieldset className="filter">
                <label htmlFor="season" className="label">
                    Season
                </label>
                <select id="season" value={currentSeason} onChange={handleSeasonChange}>
                    {seasonRange.map((s) => (
                        <option key={s} value={s}>
                            {capitalize(s)}
                        </option>
                    ))}
                </select>
            </fieldset>
            {adultContentFilterEnabled && (
                <fieldset className="filter">
                    <label htmlFor="includeAdultContent" className="label small row centered">
                        Include Adult Content
                        <input
                            id="includeAdultContent"
                            type="checkbox"
                            defaultChecked={includeAdultContent}
                            onChange={handleAdultContentChange}
                        />
                    </label>
                </fieldset>
            )}
        </section>
    );
}

function capitalize(s: string) {
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
}

export default Filters;
