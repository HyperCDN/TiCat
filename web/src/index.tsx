import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import {Provider} from 'react-redux';
import reduxStore from './util/redux/ReduxStore';
import App from "./components/App";
import {setResourceServerURL} from "./util/redux/slice/AuthSlice";
import "./index.scss"
import {testAuth} from "./util/auth/Auth";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

fetch('/api/config')
.then(r => {
    if(!r.ok){
        throw new Error('Failed to get configuration from api')
    }
    return r.json()
})
.then(json => {
    // assign config vars as needed
    reduxStore.dispatch(setResourceServerURL(json['auth']))
})
.then(() => {
    // test auth
    setInterval(() => testAuth(), 1000 * 30)
})
.then(() => {
    root.render(
        <Provider store={reduxStore}>
            <App/>
        </Provider>
    )
    reportWebVitals()
})
.catch(e => {
    console.error(e)
})










