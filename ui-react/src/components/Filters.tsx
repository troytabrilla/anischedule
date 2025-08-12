import type { ActionDispatch, ChangeEvent } from 'react';

import type { State, Action } from '../util/types';

type Props = Pick<State, 'season' | 'year' | 'seasonRange'> & { dispatch: ActionDispatch<[Action]> };

function Filters({ season, year, seasonRange, dispatch }: Props) {
    const currentSeason = `${season} ${year}`;
    const handleChange = (e: ChangeEvent<HTMLSelectElement>) => {
        const [newSeason, newYear] = e.target.value.split(' ');
        dispatch({ type: 'season', payload: { season: newSeason, year: newYear } });
    };
    return (
        <section className="row centered">
            <select id="season" value={currentSeason} onChange={handleChange}>
                {seasonRange.map((s) => (
                    <option key={s} value={s}>
                        {s}
                    </option>
                ))}
            </select>
        </section>
    );
}

export default Filters;
