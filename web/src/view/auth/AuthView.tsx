import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import {withRouter, WithRouterProps} from "../../util/router/withRouter";
import {setAuthDetails} from "../../util/redux/slice/AuthSlice";
import "./AuthView.scss"
import {login} from "../../util/api/Auth_Endpoint";


type StateProps = {
    resourceServerURL?: string
    clientId?: string
    accessToken?: string
    refreshToken?: string
}

type DispatchProps = {
    setAuthDetails: (value: {access_token: string, refresh_token: string} | undefined) => void
}

type OwnProps = {

}

type ComponentLocalState = {

}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    const resourceServerURL = state.auth.resourceServerURL
    const clientId = state.auth.clientId
    const accessToken = state.auth.accessToken
    const refreshToken = state.auth.refreshToken
    return {resourceServerURL, clientId, accessToken, refreshToken}
}

const mapDispatchToProps: DispatchProps = {
    setAuthDetails
}

class AuthView extends Component<WithRouterProps<ComponentProps>, ComponentLocalState> {

    state = {

    }

    async componentDidMount() {
        const queryParams = new URLSearchParams(this.props.location.search)
        const authCode = queryParams.get('code')
        window.history.replaceState({}, document.title, window.location.pathname)
        if (!(authCode == null || (this.props.accessToken|| this.props.refreshToken))) {
            await login(authCode)
        }
        this.props.navigate("/", { replace: true })
    }

    render() {
        return (
            <div key="auth-view" id="auth-view"/>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(withRouter<ComponentProps>(AuthView))