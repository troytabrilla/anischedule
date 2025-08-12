import './Anime.css';

import dayjs from '../util/dayjs';

import type { Anime as AnimeType } from '../util/types';

interface Props {
    anime: AnimeType;
}

function Anime({ anime }: Props) {
    const title = truncate(anime.englishTitle ?? anime.romajiTitle ?? anime.nativeTitle ?? '');
    const nextEpisodeAiringAt = anime.nextEpisodeAiringAt ? dayjs.unix(anime.nextEpisodeAiringAt) : null;
    return (
        <section className="anime column centered">
            <div>
                {anime.thumbnailLarge && (
                    <a href={anime.url} target="_blank">
                        <img src={anime.thumbnailLarge} height="120px" alt={title} />
                    </a>
                )}
            </div>
            <div>
                <a href={anime.url} target="_blank">
                    {title}
                </a>
            </div>
            <div>Episode {anime.nextAiringEpisode}</div>
            <div>{nextEpisodeAiringAt?.format('YYYY-MM-DD HH:mm')}</div>
        </section>
    );
}

function truncate(title: string, length: number = 40) {
    if (title.length <= length) {
        return title;
    }
    return title.substring(0, length - 3) + '...';
}

export default Anime;
