import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import Hero from "../../components/hero/Hero";
import "./HomeView.scss"
import {getAllAvailableBoards} from "../../util/api/Board_Endpoints";
import {Board} from "../../util/api/entities/Board";

type StateProps = {

}

type DispatchProps = {

}

type OwnProps = {

}

type ComponentLocalState = {
    boards?: Board[]
}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    return {}
}

const mapDispatchToProps: DispatchProps = {

}

class HomeView extends Component<ComponentProps, ComponentLocalState> {

    state = {
        boards: undefined
    }

    componentDidMount() {
        getAllAvailableBoards()
            .then(boards => {
                this.setState({
                    boards: boards
                })
            })
    }

    render() {
        if (this.state.boards) {
            return (
                <div key="home-view" id="home-view">
                    Now we got something! <br/>
                    {JSON.stringify(this.state.boards)}
                </div>
            )
        } else {
            return (
                <div key="home-view" id="home-view">
                    <Hero title={"TiCat"}/>
                </div>
            )
        }
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(HomeView)