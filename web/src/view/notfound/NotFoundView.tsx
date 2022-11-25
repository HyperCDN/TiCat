import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import Hero from "../../components/hero/Hero";
import "./NotFoundView.scss"

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

class NotFoundView extends Component<ComponentProps, ComponentLocalState> {

    state = {

    }

    render() {
        return (
            <div key="not-found-view" id="not-found-view">
                <Hero
                    title="404"
                    description="Page Not Found"
                />
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(NotFoundView)