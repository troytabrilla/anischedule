import { ToastContainer } from 'react-toastify';

function Toast() {
    return <ToastContainer className="toast" position="top-center" limit={1} autoClose={3000} />;
}

export default Toast;
