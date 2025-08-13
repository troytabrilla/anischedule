import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';

import Schedule from '../../src/components/Schedule';
import { mockAnime } from '../mockData';

describe('Schedule', () => {
    it('renders', () => {
        render(<Schedule anime={mockAnime} />);

        expect(screen.getByText('Sunday')).toBeInTheDocument();
        expect(screen.getByText('Monday')).toBeInTheDocument();
        expect(screen.getByText('Tuesday')).toBeInTheDocument();
        expect(screen.getByText('Wednesday')).toBeInTheDocument();
        expect(screen.getByText('Thursday')).toBeInTheDocument();
        expect(screen.getByText('Friday')).toBeInTheDocument();
        expect(screen.getByText('Saturday')).toBeInTheDocument();
        expect(screen.getByText('title0')).toBeInTheDocument();
        expect(screen.getByText('title1')).toBeInTheDocument();
        expect(screen.getByText('title2')).toBeInTheDocument();
        expect(screen.getByText('title3')).toBeInTheDocument();

        const sunday = screen.getByText('Sunday').parentNode;
        expect(sunday?.children.length).toEqual(1);
        const monday = screen.getByText('Monday').parentNode;
        expect(monday?.children.length).toEqual(4);
        const tuesday = screen.getByText('Tuesday').parentNode;
        expect(tuesday?.children.length).toEqual(2);
    });
});
