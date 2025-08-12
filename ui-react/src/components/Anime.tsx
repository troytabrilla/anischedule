import dayjs from '../util/dayjs';

import type { Anime as AnimeType } from '../util/types';

interface Props {
    anime: AnimeType;
}

function Anime({ anime }: Props) {
    const title = truncate(anime.englishTitle ?? anime.romajiTitle ?? anime.nativeTitle ?? '');
    const nextEpisodeAiringAt = anime.nextEpisodeAiringAt ? dayjs.unix(anime.nextEpisodeAiringAt) : null;
    return (
        <section className="column centered">
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

function truncate(title?: string) {
    if (!title || title.length <= 25) {
        return title;
    }
    return title.substring(0, 22) + '...';
}

export default Anime;
