import { useState, useEffect } from 'react';
import { ToastContainer, toast } from 'react-toastify';

import type { State } from '../util/types';

type Props = Pick<State, 'error'>;

function ErrorHandler({ error }: Props) {
    const [debounced, setDebounced] = useState(error);

    useEffect(() => {
        const interval = setTimeout(() => setDebounced(error), 500);
        return () => clearInterval(interval);
    }, [error]);

    useEffect(() => {
        if (debounced) {
            toast.error('Whoops! Something weird happened. Please try again in a bit.');
        }
    }, [debounced]);

    return <ToastContainer className="errors" position="top-center" limit={1} />;
}

export default ErrorHandler;
