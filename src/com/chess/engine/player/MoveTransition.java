package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class MoveTransition {
    //from the move to the transitioning board state
    private final Board transitionBoard;
    private final Move move;
    private final Movestatus moveStatus;

    public MoveTransition(final Board transitionBoard,
                          final Move move,
                          final Movestatus moveStatus){
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public Movestatus getMoveStatus(){
        return this.moveStatus;
    }

    public Board getTransitionBoard(){
        return this.transitionBoard;
    }

}
