import {Component} from "react";
import {BrowserRouter} from "react-router-dom";
import {RootState} from "../util/redux/ReduxStore";
import {connect} from "react-redux";
import AppHeader from "./header/AppHeader";

import "./App.scss"
import {Route, Routes} from "react-router";
import HomeView from "../view/home/HomeView";
import AuthView from "../view/auth/AuthView";
import ProfileView from "../view/profile/ProfileView";
import BoardView from "../view/board/BoardView";
import NotFoundView from "../view/notfound/NotFoundView";

type StateProps = {

}

type DispatchProps = {

}

type OwnProps = {

}

type ComponentLocalState = {

}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    return {}
}

const mapDispatchToProps: DispatchProps = {

}

class App extends Component<ComponentProps, ComponentLocalState> {

    render() {
        return (
            <div id="app">
                <BrowserRouter>
                    <AppHeader/>
                    <div id="app-routes">
                        <Routes>
                            <Route path={'/'} element={<HomeView/>}/>
                            <Route path={'/auth_response'} element={<AuthView/>}/>
                            <Route path={'/profile/:userUUID'} element={<ProfileView/>}/>
                            <Route path={'/board/:boardId'} element={<BoardView/>}/>
                            <Route path={'*'} element={<NotFoundView/>}/>
                        </Routes>
                    </div>
                </BrowserRouter>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(App)