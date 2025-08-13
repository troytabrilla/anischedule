import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';

import Day from '../../src/components/Day';
import { Anime as AnimeType } from '../../src/util/types';
import { mockAnime } from '../mockData';

describe('Day', () => {
    it('renders', () => {
        const anime: AnimeType[] = mockAnime.slice(0);

        render(<Day anime={anime} day={0} />);

        expect(screen.getByText('Sunday')).toBeInTheDocument();
        expect(screen.getByText('title1')).toBeInTheDocument();
    });

    it('sorts by next airing at and title', () => {
        render(<Day anime={mockAnime.slice(0, 3)} day={0} />);

        const links = screen.getAllByRole('link');

        expect(links[1].textContent).toEqual('title1');
        expect(links[3].textContent).toEqual('title0');
        expect(links[5].textContent).toEqual('title2');
    });
});
