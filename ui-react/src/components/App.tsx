import './App.css';

import { useReducer, useEffect } from 'react';
import { ToastContainer, toast } from 'react-toastify';

import reducer, { initialState } from '../reducers/app';
import fetchAnime from '../util/fetchAnime';

import Header from './Header';
import Filters from './Filters';
import Schedule from './Schedule';
import Footer from './Footer';

function App() {
    const [state, dispatch] = useReducer(reducer, initialState());

    useEffect(() => {
        const promise = fetchAnime(state.season, state.year, state.includeAdultContent)
            .then((data) => dispatch({ type: 'anime', payload: data }))
            .catch((error) => {
                dispatch({ type: 'error', payload: { error } });
                throw error;
            });
        toastify(promise, `${state.season}|${state.year}|${state.includeAdultContent}`);
    }, [state.season, state.year, state.includeAdultContent]);

    return (
        <>
            <Header />
            <main className="main column centered">
                <Filters
                    season={state.season}
                    year={state.year}
                    seasonRange={state.seasonRange}
                    includeAdultContent={state.includeAdultContent}
                    dispatch={dispatch}
                />
                <Schedule anime={state.anime} />
                <ToastContainer className="toast" position="top-center" limit={1} autoClose={3000} />
            </main>
            <Footer />
        </>
    );
}

function toastify(promise: Promise<unknown>, toastId: string) {
    toast.promise(
        promise,
        {
            pending: 'Loading...',
            success: 'Finished!',
            error: 'Whoops! Something weird happened. Please try again in a bit.',
        },
        {
            toastId,
        }
    );
}

export default App;
