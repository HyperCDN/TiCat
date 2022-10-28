import {RootState} from "../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";

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

class CopyPasteClass extends Component<ComponentProps, ComponentLocalState> {

    state = {

    }

    render() {
        return (
            <></>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(CopyPasteClass)