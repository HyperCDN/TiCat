import {AppBar, Grid, IconButton, Toolbar, Typography} from "@mui/material";
import {Link} from "react-router-dom";
import DashboardIcon from "@mui/icons-material/Dashboard";
import ViewKanbanIcon from "@mui/icons-material/ViewKanban";
import ManageSearchIcon from "@mui/icons-material/ManageSearch";
import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import "./AppHeader.scss"
import ProfileBtn from "../profilebtn/ProfileBtn";

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

class AppHeader extends Component<ComponentProps, ComponentLocalState> {

    render() {
        return (
            <div id="app-header">
                <AppBar id="app-header-bar">
                    <Toolbar>
                        <Grid container>
                            <Grid item id="app-header-bar-title">
                                <Typography>TiCat</Typography>
                            </Grid>
                        </Grid>
                        <Grid container spacing={2} justifyContent="center">
                            <Grid item>
                                <IconButton color="inherit" component={Link} to='/'>
                                    <DashboardIcon/>
                                </IconButton>
                            </Grid>
                            <Grid item>
                                <IconButton color="inherit" component={Link} to='/kanban'>
                                    <ViewKanbanIcon/>
                                </IconButton>
                            </Grid>
                            <Grid item>
                                <IconButton color="inherit" component={Link} to='/backlog'>
                                    <ManageSearchIcon/>
                                </IconButton>
                            </Grid>
                        </Grid>
                        <Grid container spacing={2} justifyContent="flex-end">
                            <Grid item>
                                <ProfileBtn/>
                            </Grid>
                        </Grid>
                    </Toolbar>
                </AppBar>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(AppHeader)

