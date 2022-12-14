import {RootState} from "../../util/redux/ReduxStore";
import {Component} from "react";
import {withRouter, WithRouterProps} from "../../util/router/withRouter";
import {connect} from "react-redux";
import {getBoardInfo} from "../../util/api/Board_Endpoints";
import {getAllTicketsFor, getTicketsFor} from "../../util/api/Ticket_Endpoints";
import {Board} from "../../util/api/entities/Board";
import {Ticket} from "../../util/api/entities/Ticket";

type StateProps = {

}

type DispatchProps = {

}

type OwnProps = {

}

type ComponentLocalState = {
    board?: Board,
    tickets?: Ticket[]
}

type ComponentProps = StateProps & DispatchProps & OwnProps

const mapStateToProps = (state: RootState): StateProps => {
    return { }
}

const mapDispatchToProps: DispatchProps = {

}

class BoardView extends Component<WithRouterProps<ComponentProps>, ComponentLocalState> {

    state = {
        board: undefined,
        tickets: []
    }

    async componentDidMount() {
        getBoardInfo(this.props.params.boardId!)
            .then(board => {
                this.setState({
                    board: board
                })
                return getAllTicketsFor(board.id!)
            })
            .then(tickets => {
                this.setState({
                    tickets: tickets
                })
            })
    }

    render() {
        if (this.state.board) {
            return (
                <div key="board-view" id="board-view">
                    Now we got something! <br/>
                    {JSON.stringify(this.state.board)} <br/>
                    And the tickets! <br/>
                    {JSON.stringify(this.state.tickets)} <br/>
                </div>
            )
        } else {
            return (
                <div key="board-view" id="board-view">
                    Nothing here just yet
                </div>
            )
        }

    }

}

export default withRouter<WithRouterProps<ComponentProps>>(connect(mapStateToProps, mapDispatchToProps)(BoardView))