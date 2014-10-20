package com.marklarwill.chessVariants;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public class Move {

	Piece pieceMoved;
    Square sourceSquare;
    Piece pieceCaptured;
    Square destinationSquare;
    boolean queensideCastle;
    boolean kingsideCastle;
    
    public boolean isQueensideCastle() {
    	return queensideCastle;
    }
    
    public boolean isKingsideCastle() {
    	return kingsideCastle;
    }
    
    Move(Piece pieceMoved, Square sourceSquare, 
    	 Piece pieceCaptured, Square destinationSquare) {

		// First set the references
        this.pieceMoved = pieceMoved;
        this.sourceSquare = sourceSquare;
        this.pieceCaptured = pieceCaptured;
        this.destinationSquare = destinationSquare;
        
        if (isQueensideCastle(pieceMoved, sourceSquare, destinationSquare)) {
            queensideCastle = true;
            kingsideCastle = false;
        } else if (isKingsideCastle(pieceMoved, sourceSquare, destinationSquare)) {
            queensideCastle = false;
            kingsideCastle = true;
    	} else {
            queensideCastle = false;
            kingsideCastle = false;
    	}
    }
    
    Move(PlayerInput pi, ChessGameModel game) throws IvalidPlayerInputException {
    	
    	if (!pi.getValid()) {
    		// This should never happen as long as we always validate the 
    		// player input before creating a move. The ChessGame API should
    		// enforce this internally.
    		throw new IvalidPlayerInputException(); 
    	}
    	
		int srcFile = pi.getSrcFile();
		int srcRank = pi.getSrcRank();
		int dstFile = pi.getDstFile();
		int dstRank = pi.getDstRank();
		
		pieceMoved = game.getPieceAt(srcFile, srcRank);
		sourceSquare = game.getSquareAt(srcFile, srcRank);
		pieceCaptured = game.getPieceAt(dstFile, dstRank);
		destinationSquare = game.getSquareAt(dstFile, dstRank);
    }
    
    private boolean isQueensideCastle(Piece pieceMoved, Square sourceSquare, Square destinationSquare) {
    	
        int moverColor = pieceMoved.getColor();
        int castlingRank = (moverColor == ChessGameModel.white) ? 0 : 7;
		int srcFile = sourceSquare.getFile();
		int srcRank = sourceSquare.getRank();
		int dstFile = destinationSquare.getFile();
		int dstRank = destinationSquare.getRank();
		
        // Now check for castling
    	if (pieceMoved.getType() == ChessGameModel.king &&
    		srcFile == 4 && srcRank == castlingRank &&
    		dstFile == 2 && dstRank == castlingRank) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isKingsideCastle(Piece pieceMoved, Square sourceSquare, Square destinationSquare) {
    	
        int moverColor = pieceMoved.getColor();
        int castlingRank = (moverColor == ChessGameModel.white) ? 0 : 7;
		int srcFile = sourceSquare.getFile();
		int srcRank = sourceSquare.getRank();
		int dstFile = destinationSquare.getFile();
		int dstRank = destinationSquare.getRank();
		
        // Now check for castling
    	if (pieceMoved.getType() == ChessGameModel.king &&
    		srcFile == 4 && srcRank == castlingRank &&
 			dstFile == 6 && dstRank == castlingRank) {
    		return true;
    	}
    	
    	return false;
 	}
}

