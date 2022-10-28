import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import NotFoundView from "../../view/notfound/NotFoundView";
import HomeView from "../../view/home/HomeView";
import "./AppRoutes.scss";
import AuthView from "../../view/auth/AuthView";
import {Route, Routes} from "react-router";

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

class AppRoutes extends Component<ComponentProps, ComponentLocalState> {

    state = {

    }

    render() {
        return (
            <div id="app-routes">
                <Routes>
                    <Route path={'/'} element={<HomeView/>}/>
                    <Route path={'/auth_response'} element={<AuthView/>}/>
                    <Route path={'*'} element={<NotFoundView/>}/>
                </Routes>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(AppRoutes)

