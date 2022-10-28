import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import Hero from "../../components/hero/Hero";
import "./HomeView.scss"

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

class HomeView extends Component<ComponentProps, ComponentLocalState> {

    state = {

    }

    render() {
        return (
            <div key="home-view" id="home-view">
                <Hero title={"TiCat"}/>
                <div id="home-view-container">
                    AAAAAAAAAAAAAAAA
                </div>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(HomeView)