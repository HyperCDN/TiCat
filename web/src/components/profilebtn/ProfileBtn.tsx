import {RootState} from "../../util/redux/ReduxStore";
import React, {Component} from "react";
import {connect} from "react-redux";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import {Button, IconButton, Menu, MenuItem, Typography} from "@mui/material";
import {Link} from "react-router-dom";
import "./ProfileBtn.scss"
import {initializeLogin, logout} from "../../util/auth/Auth";
import jwtDecode, {JwtPayload} from "jwt-decode";

type StateProps = {
    accessToken?: string
}

type DispatchProps = {

}

type OwnProps = {

}

type ComponentLocalState = {
    anchorEl: HTMLElement | null
}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    const accessToken = state.auth.accessToken
    return {
        accessToken
    }
}

const mapDispatchToProps: DispatchProps = {

}

class ProfileBtn extends Component<ComponentProps, ComponentLocalState> {

    state = {
        anchorEl: null
    }

    render() {

        if (this.props.accessToken){
            const selfUserId = jwtDecode<JwtPayload>(this.props.accessToken)["sub"];
            return (
                <div id="login-btn">
                    <IconButton
                        color="inherit"
                        onClick={(event: React.MouseEvent<HTMLButtonElement>) => {this.setState({anchorEl: event.currentTarget})}}
                    >
                        <AccountCircleIcon/>
                    </IconButton>
                    <Menu
                        anchorEl={this.state.anchorEl}
                        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
                        transformOrigin={{ vertical: "top", horizontal: "center" }}
                        open={Boolean(this.state.anchorEl)}
                        onClose={() => {this.setState({anchorEl: null})}}
                    >
                        <MenuItem onClick={() => {this.setState({anchorEl: null})}} component={Link} to={'/profile/' + selfUserId}>
                            <Typography>Profile</Typography>
                        </MenuItem>
                        <MenuItem onClick={() => {
                            this.setState({anchorEl: null})
                            logout()
                        }} >
                            <Typography>Logout</Typography>
                        </MenuItem>
                    </Menu>
                </div>
            )
        } else {
            return (
                <div id="login-btn">
                    <Button color="inherit" onClick={() => initializeLogin()}>Login</Button>
                </div>
            )
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProfileBtn)