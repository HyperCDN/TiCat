import {Component} from "react";
import {BrowserRouter} from "react-router-dom";
import {RootState} from "../util/redux/ReduxStore";
import {connect} from "react-redux";
import AppRoutes from "./router/AppRoutes";
import AppHeader from "./header/AppHeader";

import "./App.scss"
import {Container} from "@mui/material";

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
                    <AppRoutes/>
                </BrowserRouter>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(App)