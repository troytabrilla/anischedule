import Anime from './Anime';

import type { State, Anime as AnimeType } from '../util/types';

type Props = Pick<State, 'anime'> & { day: number };

function Day({ anime, day }: Props) {
    return (
        <section className="column centered">
            <h2>{dayToString(day)}</h2>
            {anime.sort(comparator).map((a) => (
                <Anime key={a.id} anime={a} />
            ))}
        </section>
    );
}

function comparator(a: AnimeType, b: AnimeType) {
    return (a.nextEpisodeAiringAt ?? 0) - (b.nextEpisodeAiringAt ?? 0);
}

function dayToString(day: number) {
    switch (day) {
        case 0:
            return 'Sunday';
        case 1:
            return 'Monday';
        case 2:
            return 'Tuesday';
        case 3:
            return 'Wednesday';
        case 4:
            return 'Thursday';
        case 5:
            return 'Friday';
        case 6:
            return 'Saturday';
    }
}

export default Day;
