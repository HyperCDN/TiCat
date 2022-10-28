import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {connect} from "react-redux";
import {Grid, Typography} from "@mui/material";
import "./Hero.scss"

type StateProps = {

}

type DispatchProps = {

}

type OwnProps = {
    title: string,
    description?: string
}

type ComponentLocalState = {

}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    return {}
}

const mapDispatchToProps: DispatchProps = {

}

class Hero extends Component<ComponentProps, ComponentLocalState> {

    state = {

    }

    render() {
        return (
            <div className="hero">
                <Grid className="hero-grid" container direction="column">
                    <Grid className="hero-title" item>
                        <Typography variant="h1">
                            {this.props.title}
                        </Typography>
                    </Grid>
                    <Grid className="hero-description" item>
                        <Typography variant="h3">
                            {this.props.description}
                        </Typography>
                    </Grid>
                </Grid>
            </div>
        )
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(Hero)