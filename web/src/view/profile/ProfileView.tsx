import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import {withRouter, WithRouterProps} from "../../util/router/withRouter";
import {getDataForUserWithId} from "../../util/api/User_Endpoints";
import {User} from "../../util/api/entities/User";

type StateProps = {

}

type DispatchProps = {

}

type OwnProps = {

}

type ComponentLocalState = {
    user?: User
}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    return { }
}

const mapDispatchToProps: DispatchProps = {

}

class ProfileView extends Component<WithRouterProps<ComponentProps>, ComponentLocalState> {

    state = {
        user: undefined
    }

    componentDidMount() {
        getDataForUserWithId(this.props.params.userUUID!)
        .then(user => {
            this.setState({
                user: user
            })
        })
    }

    render() {
        if (this.state.user) {
            return (
                <div key="profile-view" id="profile-view">
                    Now we got something! <br/>
                    {JSON.stringify(this.state.user)}
                </div>
            )
        } else {
            return (
                <div key="profile-view" id="profile-view">
                    Nothing here just yet
                </div>
            )
        }

    }

}

export default withRouter<WithRouterProps<ComponentProps>>(connect(mapStateToProps, mapDispatchToProps)(ProfileView))