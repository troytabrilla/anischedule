import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';

import App from '../../src/components/App';
import { mockAnime } from '../mockData';
import * as fetchAnime from '../../src/util/fetchAnime';

vi.mock('.././src/util/fetchAnime', async () => {
    return async () => {
        return {
            anime: mockAnime,
        };
    };
});

describe('App', () => {
    it('renders', async () => {
        const spy = vi.spyOn(fetchAnime, 'default');

        render(<App />);

        expect(screen.getByRole('main')).toBeInTheDocument();
        expect(spy).toBeCalledTimes(1);
        expect(spy).toBeCalledWith('SUMMER', 2025, false);
    });
});
