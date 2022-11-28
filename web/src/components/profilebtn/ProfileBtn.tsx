import {RootState} from "../../util/redux/ReduxStore";
import React, {Component} from "react";
import {connect} from "react-redux";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import {Button, IconButton, Menu, MenuItem, Typography} from "@mui/material";
import {Link} from "react-router-dom";
import "./ProfileBtn.scss"
import jwtDecode, {JwtPayload} from "jwt-decode";
import {initializeLogin, logout} from "../../util/api/Auth_Endpoint";
import {getSelfUserUUID} from "../../util/api/Self";

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

    private openMenu(event: React.MouseEvent<HTMLButtonElement>) {
        this.setState({anchorEl: event.currentTarget})
    }

    private closeMenu() {
        this.setState({anchorEl: null})
    }

    render() {

        if (this.props.accessToken){
            const selfUserId = getSelfUserUUID()
            return (
                <div id="login-btn">
                    <IconButton
                        color="inherit"
                        onClick={(event: React.MouseEvent<HTMLButtonElement>) => this.openMenu(event)}
                    >
                        <AccountCircleIcon/>
                    </IconButton>
                    <Menu
                        anchorEl={this.state.anchorEl}
                        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
                        transformOrigin={{ vertical: "top", horizontal: "center" }}
                        open={Boolean(this.state.anchorEl)}
                        onClose={() => this.closeMenu()}
                    >
                        <MenuItem onClick={() => this.closeMenu()} component={Link} to={`/profile/${selfUserId}`}>
                            <Typography>Profile</Typography>
                        </MenuItem>
                        <MenuItem onClick={() => {
                            this.closeMenu()
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