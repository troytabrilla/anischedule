import './Filters.css';

import type { ActionDispatch, ChangeEvent } from 'react';

import type { State, Action } from '../util/types';

type Props = Pick<State, 'season' | 'year' | 'seasonRange'> & { dispatch: ActionDispatch<[Action]> };

// TODO add checkbox filter to include adult content? for now, filter out
function Filters({ season, year, seasonRange, dispatch }: Props) {
    const currentSeason = `${season} ${year}`;
    const handleChange = (e: ChangeEvent<HTMLSelectElement>) => {
        const [newSeason, newYear] = e.target.value.split(' ');
        dispatch({ type: 'season', payload: { season: newSeason, year: newYear } });
    };
    return (
        <section className="filters row centered">
            <label htmlFor="season" className="label">
                Season
            </label>
            <select id="season" value={currentSeason} onChange={handleChange}>
                {seasonRange.map((s) => (
                    <option key={s} value={s}>
                        {capitalize(s)}
                    </option>
                ))}
            </select>
        </section>
    );
}

function capitalize(s: String) {
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
}

export default Filters;
