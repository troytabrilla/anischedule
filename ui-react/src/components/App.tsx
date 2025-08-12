import './App.css';

import { useReducer, useEffect } from 'react';

import reducer, { initialState } from '../reducers/app';
import fetchAnime from '../util/fetchAnime';

import Filters from './Filters';
import Schedule from './Schedule';
import ErrorHandler from './ErrorHandler';

function App() {
    const [state, dispatch] = useReducer(reducer, initialState());

    useEffect(() => {
        fetchAnime()
            .then((data) => dispatch({ type: 'anime', payload: data }))
            .catch((err) => dispatch({ type: 'error', payload: err }));
    }, []);

    useEffect(() => {
        fetchAnime(state.season, state.year, state.includeAdultContent)
            .then((data) => dispatch({ type: 'anime', payload: data }))
            .catch((err) => dispatch({ type: 'error', payload: err }));
    }, [state.season, state.year, state.includeAdultContent]);

    return (
        <>
            <header>
                <h1>AniSchedule</h1>
            </header>
            <main className="app column centered">
                <Filters
                    season={state.season}
                    year={state.year}
                    seasonRange={state.seasonRange}
                    includeAdultContent={state.includeAdultContent}
                    dispatch={dispatch}
                />
                <Schedule anime={state.anime} timezone={state.timezone} />
                <ErrorHandler error={state.error} />
            </main>
            <footer>
                <em>
                    Created by{' '}
                    <a href="https://github.com/troytabrilla" target="_blank">
                        troytabrilla
                    </a>
                    . Data sourced by the{' '}
                    <a href="https://docs.anilist.co/" target="_blank">
                        AniList API
                    </a>
                    .
                </em>
            </footer>
        </>
    );
}

export default App;
