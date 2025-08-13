import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';

import Filters from '../../src/components/Filters';

describe('Filters', () => {
    it('renders', () => {
        localStorage.setItem('feature-flag:enable-adult-content-filter', 'true');

        const season = 'SUMMER';
        const year = 2025;
        const seasonRange = ['SPRING 2025', 'SUMMER 2025', 'FALL 2025'];
        const includeAdultContent = false;
        const dispatch = () => {};

        render(
            <Filters
                season={season}
                year={year}
                seasonRange={seasonRange}
                includeAdultContent={includeAdultContent}
                dispatch={dispatch}
            />
        );

        expect(screen.getByRole('combobox')).toBeInTheDocument();
        expect(screen.getByRole('checkbox')).toBeInTheDocument();
    });

    it('dispatches season change', async () => {
        const season = 'SUMMER';
        const year = 2025;
        const seasonRange = ['SPRING 2025', 'SUMMER 2025', 'FALL 2025'];
        const includeAdultContent = false;
        const dispatch = vi.fn(() => {});

        render(
            <Filters
                season={season}
                year={year}
                seasonRange={seasonRange}
                includeAdultContent={includeAdultContent}
                dispatch={dispatch}
            />
        );
        const select = screen.getByRole('combobox');
        fireEvent.change(select, { target: { value: 'SPRING 2025' } });

        expect(dispatch).toBeCalledTimes(1);
        expect(dispatch).toBeCalledWith({ type: 'season', payload: { season: 'SPRING', year: '2025' } });
    });

    it('dispatches adult content change', async () => {
        localStorage.setItem('feature-flag:enable-adult-content-filter', 'true');

        const season = 'SUMMER';
        const year = 2025;
        const seasonRange = ['SPRING 2025', 'SUMMER 2025', 'FALL 2025'];
        const includeAdultContent = false;
        const dispatch = vi.fn(() => {});

        render(
            <Filters
                season={season}
                year={year}
                seasonRange={seasonRange}
                includeAdultContent={includeAdultContent}
                dispatch={dispatch}
            />
        );
        const checkbox = screen.getByRole('checkbox');
        fireEvent.click(checkbox);

        expect(dispatch).toBeCalledTimes(1);
        expect(dispatch).toBeCalledWith({ type: 'includeAdultContent', payload: { includeAdultContent: true } });
    });
});
