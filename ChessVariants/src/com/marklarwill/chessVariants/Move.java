package com.marklarwill.chessVariants;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public class Move {

	// NEW MOVE BELOW
	
	// Notes for re-factoring
	// Need to do two methods:
	//public boolean isValidMove(int srcFile, int srcRank, int dstFile, int dstRank) {
	//public boolean makeMove(int srcFile, int srcRank, int dstFile, int dstRank) {
	//
	// Valid moves are dependent on:
	//     Game state.
	//         king in check after move?
	//         can piece move like that?
	//     Game state belongs to what? An instance of a game right? Not a board, or collection of squares.
	//     So what does game state consist of?
	//         A list of moves
	//         All captured pieces
	//         All remaining pieces
	//
	// 2 possible approaches for model:
	// A) makeMove does no validation, simply does what it is told, up to class user to call isValidMove
	// B) makeMove does validation, user is not guarnteed success when a move is made
	// Do we want to make the controller do the logic of validating a move? Or is that a model responsiblity?
	// Should making moves always succeed? Do we value determistism?
	// The model can encapsulate move validation at the expense of determinsm.
	// We have to make sure the user doesn't make stupid moves ANYWAY as part of the view / controller right?
	// What does the view do? drag & drop? coordinates? tranlate real x,y to rank and file?
	//
	// "playerInput"
	
	
	// OLD MOVE BELOW
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
    
    /*
    Move(Piece pieceMoved, Square sourceSquare, Square destinationSquare,
       	 boolean queensideCastle, boolean kingsideCastle) {
           
           this.pieceMoved = pieceMoved;
           this.sourceSquare = sourceSquare;
           this.pieceCaptured = null;
           this.destinationSquare = destinationSquare;
           this.queensideCastle = queensideCastle;
           this.kingsideCastle = kingsideCastle;
    }
    */
}

