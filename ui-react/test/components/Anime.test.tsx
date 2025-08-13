import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';

import Anime from '../../src/components/Anime';
import { Anime as AnimeType } from '../../src/util/types';
import { anime as mockAnime } from '../data/anime';

describe('Anime', () => {
    it('renders', () => {
        render(<Anime anime={mockAnime[0]} />);

        expect(screen.getByText('title1')).toBeInTheDocument();
        expect(screen.getByRole('img')).toBeInTheDocument();
        expect(screen.getAllByRole('link')).toHaveLength(2);

        const divs = screen.getAllByRole('generic');
        expect(divs[4].textContent).toEqual('Episode 1');
        expect(divs[5].textContent).toContain('2025-08-11');
    });

    it('truncates long titles', () => {
        const anime: AnimeType = {
            ...mockAnime[0],
            englishTitle: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
        };

        render(<Anime anime={anime} />);

        expect(screen.getByText('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...')).toBeInTheDocument();
    });
});
