package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {

    private final int[] CANDIDATE_MOVE_COORD = {7,8,9,16};

    public Pawn(int piecePos, Alliance pieceAlliance) {
        super(piecePos, pieceAlliance,PieceType.PAWN,true);
    }

    public Pawn(int piecePos, Alliance pieceAlliance, boolean isFirstMove) {
        super(piecePos, pieceAlliance, PieceType.PAWN, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int candidateOffset : CANDIDATE_MOVE_COORD){
            int candidateDestinationCoord =  this.piecePos +
                    (candidateOffset * this.getPieceAlliance().getDirection());
            if(!BoardUtils.isValidTileCoord(candidateDestinationCoord)){
                continue;
            }
            if(candidateOffset == 8 && !board.getTile(candidateDestinationCoord).isTileOccupied()){
                //pawn promotions
                if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoord)));
                }
                else{
                    legalMoves.add(new PawnMove(board,this,candidateDestinationCoord));
                }
            }
            else if(candidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePos] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePos] && this.getPieceAlliance().isWhite()))){
                final int behindCandidateDestinationCoord = this.piecePos +
                        (this.pieceAlliance.getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoord).isTileOccupied() &&
                !board.getTile(candidateDestinationCoord).isTileOccupied()){
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoord));
                }

            }
            // attacks that fall apart for the add/subtract 7 rule
            else if (candidateOffset == 7 && !((BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceAlliance.isWhite())
                    || BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceAlliance.isBlack())) {
                if(board.getTile(candidateDestinationCoord).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoord).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove
                                                            (board,this,candidateDestinationCoord,pieceOnCandidate)));
                        }
                        else {
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
                        }
                    }
                }else if(board.getEnPassantPawn()!=null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePos +
                            (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new PawnEnPassantMove(board,this,candidateDestinationCoord,pieceOnCandidate));
                        }
                    }
                }
            }
            else if (candidateOffset == 9 && !((BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceAlliance.isBlack())
                    || BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceAlliance.isWhite())){
                if(board.getTile(candidateDestinationCoord).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoord).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                        if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove
                                    (board,this,candidateDestinationCoord,pieceOnCandidate)));
                        }else{
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
                        }
                    }
                }else if(board.getEnPassantPawn()!=null){
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePos -
                            (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new PawnEnPassantMove(board,this,candidateDestinationCoord,pieceOnCandidate));
                        }
                    }
                }
            }
        }


        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoord(),move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    // because only bitches don't choose queen on promotion
    public Piece getPromotionPiece(){
        return new Queen(this.piecePos,this.pieceAlliance,false);
    }
}
