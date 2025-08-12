import type { ActionDispatch } from 'react';
import type { State, Action } from '../reducers/app';

type Props = Omit<State, 'anime' | 'error'> & { dispatch: ActionDispatch<[Action]> };

// TODO add season selector
// TODO add timezone toggle (local/utc)?
function Filters({ season, year, timezone, seasonRange, dispatch }: Props) {
    const currentSeason = `${season} ${year}`;
    return (
        <section className="column centered">
            <h2>Filters</h2>
            <p>
                {currentSeason} {seasonRange.join(',')} {timezone}
            </p>
        </section>
    );
}

export default Filters;
