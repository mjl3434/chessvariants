package com.marklarwill.chessVariants;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public class ChessBoard {

	public Square[][] board = new Square[8][8];
    public Square whiteKingLocation;
    public Square blackKingLocation;
        
    ChessBoard() {
         
    	// Create squares at each rank and file, 
        for (int rank = 0; rank < 8; rank++) {
        	for (int file = 0; file < 8; file++) {
                   
        		// file = x, rank = y
                int color = ((rank)*8 + (file) + ((rank+1)%2)) % 2;
                board[file][rank] = new Square(file, rank, color);
            }
        }
    }
    
    public void addPieceToBoardAt(Piece piece, int file, int rank) {
    	
    	// The square on the board references and the piece both reference each other
    	board[file][rank].setPiece(piece);
    	piece.setSquare(board[file][rank]);
    	
    	if (piece.getType() == ChessGameModel.king) {
    		if (piece.getColor() == ChessGameModel.white) {
    			whiteKingLocation = board[file][rank];
    		}
    		else {
    			blackKingLocation = board[file][rank];
    		}
    	}
    }
    
    // This method does nothing except actually execute the move by physically
    // moving the pieces around the board. It does not check to see whether or
    // not doing this move actually makes sense.
    public Piece doMove(Move move) {
    	
    	int srcRank = move.sourceSquare.getRank();
    	int srcFile = move.sourceSquare.getFile();
    	int dstRank = move.destinationSquare.getRank();
    	int dstFile = move.destinationSquare.getFile();
    	Piece pieceCaptured = move.pieceCaptured;
    	Piece pieceMoved = move.pieceMoved;
    	
    	// Move the piece into position 
    	board[dstFile][dstRank].setPiece(pieceMoved);
    	board[srcFile][srcRank].setPiece(null);
    	pieceMoved.setSquare(board[dstFile][dstRank]);
    	
    	// Move the rook too, if this is a castle
    	if (move.isQueensideCastle() && pieceMoved.getColor() == ChessGameModel.white) {
    		Piece rook = board[0][0].getPiece();
    		rook.setSquare(board[3][0]);
    		board[3][0].setPiece(rook);
    		board[0][0].setPiece(null);
    	}
    	else if (move.isQueensideCastle() && pieceMoved.getColor() == ChessGameModel.black) {
    		Piece rook = board[0][7].getPiece();
    		rook.setSquare(board[3][7]);
    		board[3][7].setPiece(rook);
    		board[0][7].setPiece(null);
    	}
    	else if (move.isKingsideCastle() && pieceMoved.getColor() == ChessGameModel.white) {
    		Piece rook = board[7][0].getPiece();
    		rook.setSquare(board[5][0]);
    		board[5][0].setPiece(rook);
    		board[7][0].setPiece(null);
    	}
    	else if (move.isKingsideCastle() && pieceMoved.getColor() == ChessGameModel.black) {
    		Piece rook = board[7][7].getPiece();
    		rook.setSquare(board[5][7]);
    		board[5][7].setPiece(rook);
    		board[7][7].setPiece(null);
    	}
    	
    	// Update convenience variables with king's position
    	if (pieceMoved.getType() == ChessGameModel.king) {
    		
    		if (pieceMoved.getColor() == ChessGameModel.white)
    			whiteKingLocation = board[dstFile][dstRank];
    		else
    			blackKingLocation = board[dstFile][dstRank];
    	}
    	
    	return pieceCaptured;
    }

    public String generateFen() {
    	String FEN = "";
    	int blankSquares = 0;
    	
    	// Start from the 8th rank and descend to the 1st rank
    	for (int rank = 7; rank > 0; rank--) {
    		// And follow the files left to right
    		for (int file = 0; file < 8; file++) {
    			
    		}
    	}
    	
    	return FEN;
    }
    
    // FIXME: Ideally we should have a list of moves, we should be able to 
    // get the last move from there.
    public void undoMove(Move lastMove) {
    	
    	int srcRank = lastMove.sourceSquare.getRank();
    	int srcFile = lastMove.sourceSquare.getFile();
    	int dstRank = lastMove.destinationSquare.getRank();
    	int dstFile = lastMove.destinationSquare.getFile();
    	Piece pieceCaptured = lastMove.pieceCaptured; 
    	Piece pieceMoved = lastMove.pieceMoved;
    	
    	// Move the piece back to its original location
    	board[srcFile][srcRank].setPiece(pieceMoved);
    	pieceMoved.setSquare(board[srcFile][srcRank]);
    	
    	// Restore the captured piece
    	board[dstFile][dstRank].setPiece(pieceCaptured);
    	if(pieceCaptured != null) {
    		pieceCaptured.setSquare(board[dstFile][dstRank]);
    	}
    	
    	// If castle restore the position of the rook too
    	// Move the rook too, if this is a castle
    	if (lastMove.isQueensideCastle() && pieceMoved.getColor() == ChessGameModel.white) {
    		Piece rook = board[3][0].getPiece();
    		board[0][0].setPiece(rook);
    		rook.setSquare(board[0][0]);
    	}
    	else if (lastMove.isQueensideCastle() && pieceMoved.getColor() == ChessGameModel.black) {
    		Piece rook = board[3][7].getPiece();
    		board[0][7].setPiece(rook);
    		rook.setSquare(board[0][7]);
    	}
    	else if (lastMove.isKingsideCastle() && pieceMoved.getColor() == ChessGameModel.white) {
    		Piece rook = board[5][0].getPiece();
    		board[7][0].setPiece(rook);
    		rook.setSquare(board[7][0]);
    	}
    	else if (lastMove.isKingsideCastle() && pieceMoved.getColor() == ChessGameModel.black) {
    		Piece rook = board[5][7].getPiece();
    		board[7][7].setPiece(rook);
    		rook.setSquare(board[7][7]);
    	}
    	
    	// Update position of kings if relevant
    	// FIXME: Not really the board's responsibility
        // game state should be stored in the model, and done/undone in the model
    	if (pieceMoved.getType() == ChessGameModel.king) {
    		
    		if (pieceMoved.getColor() == ChessGameModel.white)
    			whiteKingLocation = board[srcFile][srcRank];
    		else
    			blackKingLocation = board[srcFile][srcRank];
    	}
    }
    
    public Square getKingLocation(int color) {
    	return (color == ChessGameModel.white) ? whiteKingLocation : blackKingLocation;
    }
    
    public Piece getPieceAt(int file, int rank) {
    	return board[file][rank].getPiece();
    }
    
    public Square getSquareAt(int file, int rank) {
    	return board[file][rank];
    }
}

