import dayjs from '../util/dayjs';
import Day from './Day';

import type { Anime, State } from '../util/types';

type Props = Pick<State, 'anime' | 'timezone'>;

// TODO add days and anime components
function Schedule({ anime }: Props) {
    return (
        <section className="row">
            {[...Array(7).keys()].map((day) => (
                <Day key={day} anime={animeByDay(anime, day)} day={day} />
            ))}
        </section>
    );
}

function animeByDay(anime: Anime[], day: number) {
    return anime.filter((a) => {
        const nextEpisodeAiringAt = a.nextEpisodeAiringAt;
        if (nextEpisodeAiringAt) {
            const date = dayjs.unix(nextEpisodeAiringAt);
            return date.day() === day;
        }
        return false;
    });
}

export default Schedule;
