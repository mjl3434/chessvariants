package com.marklarwill.chessVariants;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ChessGameModel implements ChessGameModelInterface {

    static final int none = 0;
    static final int pawn = 1;
    static final int knight = 2;
    static final int bishop = 3;
    static final int rook = 4;
    static final int queen = 5;
    static final int king = 6;
    static final int white  = 0;
    static final int black  = 1;
    static final int human = 0;
    static final int computer = 1;
	
	ArrayList<PieceLocationsObserver> pieceLocationsObservers = new ArrayList<PieceLocationsObserver>();
	ArrayList<CheckmateObserver> checkmateObservers = new ArrayList<CheckmateObserver>();
	ArrayList<StalemateObserver> stalemateObservers = new ArrayList<StalemateObserver>();
    private int turn;							// Used to tell who's turn it is white or black
    private boolean whiteCheck;					// FIXME: This is used to see of castling is legal... do we update this always?
    private boolean blackCheck;					// -> updateConvienceVariables
    private boolean whiteKingMoved;
    private boolean whiteQueensideRookMoved;
	private boolean whiteKingsideRookMoved;
    private boolean blackKingMoved;
    private boolean blackQueensideRookMoved;
    private boolean blackKingsideRookMoved;
    private int whitePlayerType;
    private int blackPlayerType;
    private int halfMoveClock;
    private List<Move> movesList;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private List<Piece> capturedWhitePieces;
    private List<Piece> capturedBlackPieces;
    private ChessBoard chessBoard;

    // This data is required to calculate the 50 move rule
    private int lastPawnMove;
    private int lastPieceCaptured;
    
	public ChessGameModel(int gameType) {

        chessBoard = new ChessBoard();
        movesList = new ArrayList<Move>(64);
        whitePieces = new ArrayList<Piece>(16);
        blackPieces = new ArrayList<Piece>(16);
        capturedWhitePieces = new ArrayList<Piece>(16);
        capturedBlackPieces = new ArrayList<Piece>(16);
        turn = white;
        whiteCheck = false;
        blackCheck = false;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteQueensideRookMoved = false;
        whiteKingsideRookMoved = false;
        blackQueensideRookMoved = false;
        blackKingsideRookMoved = false;
        halfMoveClock = 1;
        lastPawnMove = 0;
        lastPieceCaptured = 0;
        
        if (gameType == onePlayerGame) {
        	whitePlayerType = human;
        	blackPlayerType = computer;
        }
        else {
        	whitePlayerType = human;
        	blackPlayerType = human;
        }
        
        // Add the pieces to the chess board
        Piece piece = new Rook(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 0, 0);
        piece = new Knight(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 1, 0);
        piece = new Bishop(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 2, 0);
        piece = new Queen(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 3, 0);
        piece = new King(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 4, 0);
        piece = new Bishop(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 5, 0);
        piece = new Knight(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 6, 0);
        piece = new Rook(white);
        whitePieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 7, 0);

        for(int i = 0; i < 8; i++) {
        	piece = new Pawn(white);
        	whitePieces.add(piece);
        	chessBoard.addPieceToBoardAt(piece, i, 1);
        }
        
        for(int i = 0; i < 8; i++) {
        	piece = new Pawn(black);
        	blackPieces.add(piece);
        	chessBoard.addPieceToBoardAt(piece, i, 6);
        }
        
        piece = new Rook(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 0, 7);
        piece = new Knight(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 1, 7);
        piece = new Bishop(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 2, 7);
        piece = new Queen(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 3, 7);
        piece = new King(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 4, 7);
        piece = new Bishop(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 5, 7);
        piece = new Knight(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 6, 7);
        piece = new Rook(black);
        blackPieces.add(piece);
        chessBoard.addPieceToBoardAt(piece, 7, 7);
	}

	@Override
    public boolean makeMove(PlayerInput input) {
    	
		Move move = null;
		Piece capturedPiece = null;
		int nextPlayer = (turn == white) ? black : white;
		
		// Validate the input
    	if (!playerInputIsValid(input)) {
    	   	return false;
    	}

    	// Construct a move
		try {
			move = new Move(input, this);
		} catch (IvalidPlayerInputException e) {
			// As long as the PlayerInput passed to the Move constructor 
			// is validated as it is above, this should never happen.
			e.printStackTrace();
		}
		
		// Make the move
		capturedPiece = chessBoard.doMove(move);
		updatePerMoveGameState(move);
		movesList.add(move);
		
		// If a piece was captured move it to a different list
		if (capturedPiece != null) {

			// Needed to enforce 50 move rule
			lastPieceCaptured = halfMoveClock;

			if (turn == white) {
				blackPieces.remove(capturedPiece);
				capturedBlackPieces.add(capturedPiece);
			} else {
				whitePieces.remove(capturedPiece);
				capturedWhitePieces.add(capturedPiece);
			}
		}
		
    	// If the next player doesn't have any legal moves then the game is
		// over and we have encountered either checkmate or stalemate
    	if (!playerHasLegalMove(nextPlayer)) {
    		
    		if (getCheck(nextPlayer)) {
    			// If the player is in check, and has no legal moves that is checkmate
    			notifyCheckmateObservers();
    			System.out.printf("Checkmate! %s wins.\n", (turn == white) ? "white" : "black");
    		}
    		else {
    			// If the player is not in check, and has no legal moves that is stalemate
    			notifyStalemateObservers();
        		System.out.println("Stalemate! The game is a draw.");
    		}
    	}	
    	else {
    		
    		// Even if the player does have legal moves the game could still be over from
    		// - The 50 move rule
    		// - Threefold repetition 

    		// FIXME: Check for 50 move rule

    		// FIXME: Check for 3-fold repitition
    	}
		

    	// Advance the turn to the next player
		halfMoveClock++;
    	turn = (turn == white) ? black : white;
    	
    	return true;
    }

	@Override
	public List<Piece> getPieceLocations() {
    	List<Piece> allPieces = new ArrayList<Piece>(whitePieces);
    	allPieces.addAll(blackPieces);
    	return allPieces;
	}

	@Override
	public void registerObserver(PieceLocationsObserver o) {
		pieceLocationsObservers.add(o);
	}

	@Override
	public void removeObserver(PieceLocationsObserver o) {
		pieceLocationsObservers.remove(o);
		
	}

	@Override
	public void registerObserver(CheckmateObserver o) {
		checkmateObservers.add(o);
	}

	@Override
	public void removeObserver(CheckmateObserver o) {
		checkmateObservers.remove(o);
	}

	@Override
	public void registerObserver(StalemateObserver o) {
		stalemateObservers.add(o);
	}

	@Override
	public void removeObserver(StalemateObserver o) {
		stalemateObservers.remove(o);
	}
	
	public void notifyPieceLocationsObservers() {
		for (PieceLocationsObserver o : pieceLocationsObservers) {
			o.updatePieceLocations();
		}
	}
	
	public void notifyCheckmateObservers() {
		for (CheckmateObserver o : checkmateObservers) {
			o.updateCheckmate();
		}
	}

	public void notifyStalemateObservers() {
		for (StalemateObserver o : stalemateObservers) {
			o.updateStalemate();
		}
	}
	
	public boolean getCheck(int player) {
		if (player == white)
			return whiteCheck;
		else
			return blackCheck;
	}
	
    public boolean getWhiteCheck() {
    	return whiteCheck;
    }

    public boolean getBlackCheck() {
    	return blackCheck;
    }
    
    public void setWhitePlayerType(int type) {
    	whitePlayerType = (type == computer) ? computer : human;
    }
    
    public void setBlackPlayerType(int type) {
    	blackPlayerType = (type == computer) ? computer : human;
    }
    
    public Piece getPieceAt(int file, int rank) {
    	return chessBoard.getPieceAt(file, rank);
    }

    public Square getSquareAt(int file, int rank) {
    	return chessBoard.getSquareAt(file, rank);
    }
    
    public ChessBoard getChessBoard() {
    	return chessBoard;
    }
    
    public List<Move> getMovesList() {
    	return movesList;
    }
    
	public void getComputerMove() {
		// TODO Auto-generated method stub
	}
    
    public boolean playerHasLegalMove(int player) {
    	
		boolean legalMove = false;
    	
		// FIXME: Logic can be super simplified!
		
    	if (player == white) {

    		// For each of white's pieces
    		Iterator<Piece> whitePiece = whitePieces.iterator(); 
    		while (whitePiece.hasNext() && !legalMove) {
    			
    			// For each move that piece can make
    			Iterator<Move> possibleMoves = whitePiece.next().getPossibleMoves().iterator();
    			while (possibleMoves.hasNext() && !legalMove) {
    				
    				Move nextMove = possibleMoves.next();
    				
    				// If the king is not in check after the move, then we found a legal move
    		    	if (!kingInCheckAfterMove(nextMove.pieceMoved, nextMove.pieceCaptured,
    		    			                  nextMove.sourceSquare, nextMove.destinationSquare, 
    		    			                  white, black)) {
    		    		legalMove = true;
    		    		break;
    		    	}
    			}
    		}
    	}
    	else if(player == black) {
    		
    		// For each of black's pieces
    		Iterator<Piece> blackPiece = blackPieces.iterator(); 
    		while (blackPiece.hasNext() && !legalMove) {
    			
    			// For each move that piece can make
    			Iterator<Move> possibleMoves = blackPiece.next().getPossibleMoves().iterator();
    			while (possibleMoves.hasNext() && !legalMove) {
    				
    				Move nextMove = possibleMoves.next();
    				
    				// If the king is not in check after the move, then we found a legal move
    		    	if (!kingInCheckAfterMove(nextMove.pieceMoved, nextMove.pieceCaptured,
    		    			                  nextMove.sourceSquare, nextMove.destinationSquare, 
    		    			                  black, white)) {
    		    		legalMove = true;
    		    		break;
    		    	}
    			}
    		}
    	}
    	

    	
    	return legalMove;
    }    
    
    private boolean playerInputIsValid(PlayerInput pi) {
    	
    	int moverColor = turn;
    	int opponentColor = (moverColor == white) ? black : white;
    	Piece pieceMoved = chessBoard.getPieceAt(pi.getSrcFile(), pi.getSrcRank());
    	Piece pieceCaptured = chessBoard.getPieceAt(pi.getDstFile(), pi.getDstRank());
    	Square sourceSquare = chessBoard.getSquareAt(pi.getSrcFile(), pi.getSrcRank());
    	Square destinationSquare = chessBoard.getSquareAt(pi.getDstFile(), pi.getDstRank());
    	
    	// You have to move something
    	if (pieceMoved == null)
    		return false;
    	
    	// You can only move your own pieces
    	if (pieceMoved.getColor() != moverColor)
    		return false;
    	
    	// You can't capture your own pieces either
    	if (pieceCaptured != null && pieceMoved.getColor() == pieceCaptured.getColor())
    		return false;
    	
    	// This checks that the type of movement being made is valid for that 
    	// particular piece. This includes checking for odd cases which are 
    	// dependent on the game state like En passant and castling.
    	if (!pieceMoved.isValidMovementForPiece(sourceSquare, destinationSquare, this))
    		return false;
    	
    	// Regardless of if a player is in check or not, after their move
    	// their king cannot be in check. I.e. A player with a king in check
    	// must remove their king from check, and a player cannot move their 
    	// king into check.
    	if (kingInCheckAfterMove(pieceMoved, pieceCaptured, sourceSquare, destinationSquare, moverColor, opponentColor))
    		return false;

    	// Finally we know this move is valid, so save that information
    	pi.setValid(true);
    	
    	return true;
    }
	
    private boolean kingInCheckAfterMove(Piece pieceMoved, Piece pieceCaptured, 
    									 Square sourceSquare, Square destinationSquare, 
    									 int moverColor, int opponentColor) {
    	boolean kingInCheck = false;
		Move move = new Move(pieceMoved, sourceSquare, pieceCaptured, destinationSquare);
    	
		// Temporarily make the move, so we can verify king is not in check.
    	// Save a reference to any captured pieces, so they are not garbage
    	// collected.
		Piece capturedPiece = chessBoard.doMove(move);
		
		// Is the king still in check?
		if (squareIsUnderAttackBy(chessBoard.getKingLocation(moverColor), opponentColor)) {
			kingInCheck = true;
		}
		
		// Regardless of result undo the temporary move
		chessBoard.undoMove(move);

		return kingInCheck;
    }
    
    private boolean squareIsUnderAttackBy(Square square, int attackerColor) {
    	
    	int rank = square.getRank();
    	int file = square.getFile();
    	Piece piece;

       	// check for pawn attacks of opposing color
    	if (attackerColor == white) {
    		
    		if (rank > 0 && file > 0) {
    			piece = chessBoard.getPieceAt(file-1, rank-1);
    			if (piece != null && piece.getColor() == white && piece.getType() == pawn)
    				return true;
    		}
    		
    		if (rank > 0 && file < 7) {
	    		piece = chessBoard.getPieceAt(file+1, rank-1);
	    		if (piece != null && piece.getColor() == white && piece.getType() == pawn)
	    			return true;
    		}
    	}
    	else { // attackerColor == black
    	
    		if (rank < 7 && file > 0) {
    			piece = chessBoard.getPieceAt(file-1, rank+1);
    			if (piece != null && piece.getColor() == black && piece.getType() == pawn)
    				return true;
    		}
    		
    		if (rank < 7 && file < 7) {
    			piece = chessBoard.getPieceAt(file+1, rank+1);
    			if (piece != null && piece.getColor() == black && piece.getType() == pawn)
    				return true;
    		}
    	}
    	
    	// check for rook (or queen) attacks ... no occupied squares in between
    	// check along x from piece to right edge of board
    	for (int x = file+1; x < 8; x++) {
    		piece = chessBoard.getPieceAt(x, rank);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == rook || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along x from piece to the left edge of the board
    	for (int x = file-1; x > 0; x--) {
    		piece = chessBoard.getPieceAt(x, rank);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == rook || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along y from piece to the top edge of the board
    	for (int y = rank+1; y < 8; y++) {
    		piece = chessBoard.getPieceAt(file, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == rook || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along y from piece to the bottom edge of the board
    	for (int y = rank-1; y > 0; y--) {
    		piece = chessBoard.getPieceAt(file, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == rook || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check for bishop (or queen) attacks ... no occupied squares in between
    	// check along +x/+y from piece to top right of diagonal
    	for (int x = file+1, y = rank+1; x < 8 && y < 8; x++, y++) {
    		piece = chessBoard.getPieceAt(x, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == bishop || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along +x/-y from piece to bottom right of diagonal
    	for (int x = file+1, y = rank-1; x < 8 && y > 0; x++, y--) {
    		piece = chessBoard.getPieceAt(x, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == bishop || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along -x/+y from piece to top left of diagonal
    	for (int x = file-1, y = rank+1; x > 0 && y < 8; x--, y++) {
    		piece = chessBoard.getPieceAt(x, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == bishop || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check along -x/-y from piece to bottom left of diagonal
    	for (int x = file-1, y = rank-1; x > 0 && y > 0; x--, y--) {
    		piece = chessBoard.getPieceAt(x, y);
    		if (piece == null)
    			continue;
    		if (piece.getColor() == attackerColor && 
    			(piece.getType() == bishop || piece.getType() == queen))
    			return true;
    		else
    			break;
    	}
    	
    	// check for knight attacks
    	
    	// X+1 Y+2
    	if (file <= 6 && rank <= 5) {
    		piece = chessBoard.getPieceAt(file+1, rank+2);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X+2 Y+1
    	if (file <= 5 && rank <= 6) {
    		piece = chessBoard.getPieceAt(file+2, rank+1);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X+2, Y-1
    	if (file <= 5 && rank >= 1) {
    		piece = chessBoard.getPieceAt(file+2, rank-1);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X+1, Y-2
    	if (file <= 6 && rank >= 2) {
    		piece = chessBoard.getPieceAt(file+1, rank-2);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X-1, Y-2
    	if (file >= 1 && rank >= 2) {
    		piece = chessBoard.getPieceAt(file-1, rank-2);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X-2, Y-1
    	if (file >= 2 && rank >= 1) {
    		piece = chessBoard.getPieceAt(file-2, rank-1);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X-2, Y+1
    	if (file >= 2 && rank <= 6) {
    		piece = chessBoard.getPieceAt(file-2, rank+1);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// X-1, Y+2
    	if (file >= 1 && rank <= 5) {
    		piece = chessBoard.getPieceAt(file-1, rank+2);
    		if (piece != null && piece.getColor() == attackerColor && piece.getType() == knight)
    			return true;
    	}
    	
    	// check for king attacks
    	
    	// Cover a 3x3 square around our coordinate
    	for (int x = file-1; x <= file+1; x++) {		
    		for (int y = rank-1; y <= rank+1; y++) {
    			
    			if ((x >= 0 && x <= 7 && y >= 0 && y <= 7) &&	// As long as the coordinates are valid
    				!(x == file && y == file)) {				// And we aren't on the square itself

    				piece = chessBoard.getPieceAt(x, y);
            		if (piece != null && piece.getColor() == attackerColor && piece.getType() == king)
            			return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    private void updatePerMoveGameState(Move move) {
    	
		int colorOfMover = move.pieceMoved.getColor();
		int colorOfOpponent = (colorOfMover == white) ? black : white;
		int typeOfPieceMoved = move.pieceMoved.getType();
		int rankOfPieceMoved = move.sourceSquare.getRank();
		int fileOfPieceMoved = move.sourceSquare.getFile();
		int rankOfDestination = move.destinationSquare.getRank();
		int fileOfDestination = move.destinationSquare.getFile();
		
		// Check to see if we put the opponent in check (or not).
		if (colorOfMover == white) {
			if (squareIsUnderAttackBy(chessBoard.getKingLocation(black), white)) {
				blackCheck = true;
			}
			else {
				blackCheck = false;
			}
		}
		else {
			if (squareIsUnderAttackBy(chessBoard.getKingLocation(white), black)) {
				whiteCheck = true;
			}
			else {
				whiteCheck = false;
			}
		}
		
		// Check for king moving & castling
		if (colorOfMover == white) {
			
    		if (typeOfPieceMoved == king) {
    			
    			whiteKingMoved = true;
    			
    			if (fileOfPieceMoved == 4 && rankOfPieceMoved == 0 &&
    			    fileOfDestination == 2 && rankOfDestination == 0) {
    				whiteQueensideRookMoved = true;
    			}
    			
    			if (fileOfPieceMoved == 4 && rankOfPieceMoved == 0 &&
    				fileOfDestination == 6 && rankOfDestination == 0) {
    				whiteKingsideRookMoved = true;
    			}
    		}
    		
    		if (typeOfPieceMoved == rook) {
				if (fileOfPieceMoved == 0 && rankOfPieceMoved == 0)
					whiteQueensideRookMoved = true;
				if (fileOfPieceMoved == 7 && rankOfPieceMoved == 0)
					whiteKingsideRookMoved = true;
    		}
		}
		else {

    		if (typeOfPieceMoved == king) {
    			blackKingMoved = true;
    			
    			if (fileOfPieceMoved == 4 && rankOfPieceMoved == 7 &&
        			fileOfDestination == 2 && rankOfDestination == 7) {
    				blackQueensideRookMoved = true;
        		}
        			
        		if (fileOfPieceMoved == 4 && rankOfPieceMoved == 7 &&
        			fileOfDestination == 6 && rankOfDestination == 7) {
        			blackKingsideRookMoved = true;
        		}
    		}
    		
    		if (typeOfPieceMoved == rook) {
				if (fileOfPieceMoved == 0 && rankOfPieceMoved == 7)
					blackQueensideRookMoved = true;
				if (fileOfPieceMoved == 7 && rankOfPieceMoved == 7)
					blackKingsideRookMoved = true;
    		}
		}
	
		// Needed to enforce 50 move rule
		if (typeOfPieceMoved == pawn) {
			lastPawnMove = halfMoveClock;
		}
    }
    
    public abstract class Piece {
    	
    	protected int type;
    	protected int color;
    	protected Square square;
    	
    	Piece(int color) {
    		this.color = color;
    	}
    	
    	public int getType() {
    		return type;
    	}
    	
    	public int getColor() {
    		return color;
    	}
    	
    	public void setSquare(Square square) {
    		this.square = square;
    	}

    	public Square getSquare() {
    		return square;
    	}
    	
    	abstract public boolean isValidMovementForPiece(Square source, Square destination,
    													ChessGameModel chessGame);
    	
    	/**
    	 * This method is used as part of a larger algorithm to determine
    	 * whether or not a player has any legal moves. This means that it is
    	 * not necessary to check whether or not the king is in check. We must
    	 * return a list of all of the moves that particular piece can make
    	 * which are in-bounds.
    	 * @return Returns a list of moves a piece can make which are in bounds
    	 */
    	abstract public List<Move> getPossibleMoves();
    }
    
    public class King extends Piece {

    	public King(int color) {
    		super(color);
    		type = ChessGameModel.king;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		
    		List<Move> moves = new ArrayList<Move>();
    		int file = square.getFile();
    		int rank = square.getRank();
    		
    		// Get all of the moves which are on square in any direction
    		for (int x = file-1; x <= file+1; x++) {
    			for (int y = rank-1; y <= rank+1; y++) {
    				
    				if (inBounds(x, y)) {
	    				Piece dstPiece = chessBoard.getPieceAt(x, y);
	    				
	    				// If the move is on the board, is different than our original position
	    				// and the destination is either empty, or contains an opponent's piece
	    				if (!(x == file && y == rank) &&
	    					(dstPiece == null || dstPiece.color != color)) {
	    					moves.add(new Move(this, square, dstPiece, chessBoard.getSquareAt(x, y)));
	    				}
    				}
    			}
    		}
    		
    		// We castle at different ranks depending on our color
    		int castlingRank = (color == white) ? 0 : 7;
    		
    		if (queensideCastleIsLegal(color)) {
    			moves.add(new Move(this, square, null, chessBoard.getSquareAt(2, castlingRank)));
    		}
    		
    		if (kingsideCastleIsLegal(color)) {
    			moves.add(new Move(this, square, null, chessBoard.getSquareAt(6, castlingRank)));
    		}
     		
    		return moves;
    	}

    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination,
    			ChessGameModel chessGame) {
    		
    		int srcFile = source.getFile();
    		int srcRank = source.getRank();
    		int dstFile = destination.getFile();
    		int dstRank = destination.getRank();
        	
    		// King moved one square in any direction
    		if ((dstRank <= srcRank + 1 && dstRank >= srcRank - 1) &&
     			(dstFile <= srcFile + 1 && dstFile >= srcFile - 1) &&
     	 		!(dstRank == srcRank && dstFile == srcFile)) {
     	 	    return true;
     		}
        	
    		// We castle at different ranks depending on our color
    		int castlingRank = (color == white) ? 0 : 7;
    		
    		// Check for Queenside castle
			if (dstFile == 2 && dstRank == castlingRank && queensideCastleIsLegal(color)) {
				return true;
			}
			
			// Check for Kingside castle
			if (dstFile == 6 && dstRank == castlingRank && kingsideCastleIsLegal(color)) {
				return true;
			}
    		
    		return false;
    	}

    }

    public class Queen extends Piece {

    	public Queen(int color) {
    		super(color);
    		type = ChessGameModel.queen;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		
    		List<Move> moves = new ArrayList<Move>();
    		moves.addAll(getRookLikeMoves(this));
    		moves.addAll(getBishopLikeMoves(this));
    		return moves;
    	}
    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination,
    			ChessGameModel chessGame) {
    		
    		if (isValidMovmentForBishop(source, destination, chessGame) ||
    		    isValidMovmentForRook(source, destination, chessGame))
    			return true;
    		
    		return false;
    	}

    }
    
    public class Rook extends Piece {

    	Rook(int color) {
    		super(color);
    		type = ChessGameModel.rook;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		List<Move> moves = new ArrayList<Move>();
    		moves.addAll(getRookLikeMoves(this));
    		return moves;
    	}
    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination,
    			ChessGameModel chessGame) {
    		
    		if (isValidMovmentForRook(source, destination, chessGame))
    			return true;
    		
    		return false;

    	}
    }
    
    public class Knight extends Piece {

    	public Knight(int color) {
    		super(color);
    		type = ChessGameModel.knight;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		
    		List<Move> moves = new ArrayList<Move>();	
    		int file = square.getFile();
    		int rank = square.getRank();

    		// Right one, Up two
    		if (file + 1 <= 7 && rank + 2 <= 7) {
    			Piece piece = chessBoard.getPieceAt(file+1, rank+2);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file+1, rank+2)));
    		}
 
    		// Right two, up one
    		if (file + 2 <= 7 && rank + 1 <= 7) {
    			Piece piece = chessBoard.getPieceAt(file+2, rank+1);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file+2, rank+1)));
    		}
    		
    		// Right two, down one
    		if (file + 2 <= 7 && rank - 1 >= 0) {
    			Piece piece = chessBoard.getPieceAt(file+2, rank-1);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file+2, rank-1)));
    		}
    		
    		// Right one, down two
    		if (file + 1 <= 7 && rank - 2 >= 0) {
    			Piece piece = chessBoard.getPieceAt(file+1, rank-2);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file+1, rank-2)));
    		}
    		
    		// Left one, down two
    		if (file - 1 >= 0 && rank - 2 >= 0) {
    			Piece piece = chessBoard.getPieceAt(file-1, rank-2);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file-1, rank-2)));
    		}
    		
    		// Left two, down one
    		if (file - 2 >= 0 && rank - 1 >= 0) {
    			Piece piece = chessBoard.getPieceAt(file-2, rank-1);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file-2, rank-1)));
    		}
    		
    		// Left two, up one
    		if (file - 2 >= 0 && rank + 1 <= 7) {
    			Piece piece = chessBoard.getPieceAt(file-2, rank+1);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file-2, rank+1)));
    		}
    		
    		// Left one, up two
    		if (file - 1 >= 0 && rank + 2 <= 7) {
    			Piece piece = chessBoard.getPieceAt(file-1, rank+2);
    			if (piece == null || piece.color != this.color)
    				moves.add(new Move(this, square, piece, chessBoard.getSquareAt(file-1, rank+2)));
    		}
    		
    		return moves;
    	}
    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination,
    									       ChessGameModel chessGame) {

    		int srcFile = source.getFile();
    		int srcRank = source.getRank();
    		int dstFile = destination.getFile();
    		int dstRank = destination.getRank();
    		
    		if ((Math.abs(srcRank - dstRank) == 2 && Math.abs(srcFile - dstFile) == 1) ||
        		(Math.abs(srcRank - dstRank) == 1 && Math.abs(srcFile - dstFile) == 2))
    			return true;
    		
    		return false;
    	}

    }
    
    public class Bishop extends Piece {

    	public Bishop(int color) {
    		super(color);
    		type = ChessGameModel.bishop;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		List<Move> moves = new ArrayList<Move>();
    		moves.addAll(getBishopLikeMoves(this));
    		return moves;
    	}
    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination,
    			ChessGameModel chessGame) {

    		if (isValidMovmentForBishop(source, destination, chessGame))
    			return true;
    		
    		return false;
    	}

    }
    
    public class Pawn extends Piece {

    	Pawn(int color) {
    		super(color);
    		type = ChessGameModel.pawn;
    	}

    	@Override
		public List<Move> getPossibleMoves() {
    		
    		List<Move> moves = new ArrayList<Move>();
    		Move previousMove = null;
    		int file = square.getFile();
    		int rank = square.getRank();
    		
			try {
				previousMove = movesList.get(movesList.size()-1);
				
			}
			catch (NoSuchElementException e) { }
    		
    		if (color == white) {
    			
    			// If we are on first rank, and square two forward is free
    			if (rank == 1 && chessBoard.getPieceAt(file, 2) == null && chessBoard.getPieceAt(file, 3) == null) {
    				moves.add(new Move(this, square, chessBoard.getPieceAt(file, 3), chessBoard.getSquareAt(file, 3)));
    			}
    			
    			// If the square one forward is free
    			if (chessBoard.getPieceAt(file, rank+1) == null) {
    				moves.add(new Move(this, square, chessBoard.getPieceAt(file, rank+1), chessBoard.getSquareAt(file, rank+1)));
    			}

				if (previousMove != null && 
					previousMoveEnablesEnPassantFor(this)) {
					int prevFile = previousMove.destinationSquare.getFile();
					moves.add(new Move(this, square, null, chessBoard.getSquareAt(prevFile, rank+1)));
				}
    		}
    		else { // color == black
    			
    			// If we are on first rank, and square two forward is free
    			if (rank == 6 && chessBoard.getPieceAt(file, 5) == null && chessBoard.getPieceAt(file, 4) == null) {
    				moves.add(new Move(this, square, chessBoard.getPieceAt(file, 4), chessBoard.getSquareAt(file, 4)));
    			}
    			
    			// If the square one forward is free
    			if (chessBoard.getPieceAt(file, rank-1) == null) {
    				moves.add(new Move(this, square, chessBoard.getPieceAt(file, rank-1), chessBoard.getSquareAt(file, rank-1)));
    			}

				if (previousMove != null && 
					previousMoveEnablesEnPassantFor(this)) {
					int prevFile = previousMove.destinationSquare.getFile();
					moves.add(new Move(this, square, null, chessBoard.getSquareAt(prevFile, rank-1)));
				}
    		}

    		return moves;
    	}
    	
    	@Override
    	public boolean isValidMovementForPiece(Square source, Square destination, ChessGameModel chessGame) {
    		
    		int srcFile = source.getFile();
    		int srcRank = source.getRank();
    		int dstFile = destination.getFile();
    		int dstRank = destination.getRank();
        	boolean destinationEmpty;
        	ChessBoard chessBoard = chessGame.getChessBoard();
        	   	
        	destinationEmpty = (destination.getPiece() == null) ? true : false;

    		if (color == white) {
    			
    			// Pawn moved forward one to an empty square
    			if (srcFile == dstFile &&
    			    dstRank - srcRank == 1 &&
    			    destinationEmpty) {
    				return true;
    			}
    			
    			// Pawn moved forward two on first move
    			if (srcFile == dstFile && 										// Pawn stayed in same column
    			    srcRank == 1 &&												// Pawn was on starting row
    			    ((dstRank - srcRank) == 2) && 								// Pawn moved forward two
    			    chessBoard.getPieceAt(srcFile, srcRank+1) == null &&		// Square in between is empty
    			    destinationEmpty) {
    				return true;
    			}

    			// Pawn moved in a capturing motion
    			if ((dstRank == srcRank + 1) && 
    				((dstFile == srcFile + 1) || (dstFile == srcFile - 1))) {
    				
    				// Pawn captured a piece
    				if (!destinationEmpty) {
    					return true;
    				}
    				
    				if (previousMoveEnablesEnPassantFor(this)) {
    					return true;
    				}
    				
    			}
    		}
    		else { // pieceColor == black
    			
    			// Pawn moved forward one to an empty square
    			if (srcFile == dstFile &&
    				srcRank - dstRank == 1 &&
    				destinationEmpty) {
    				return true;
    			}
    			
    			// Pawn moved forward two on first move
    			if (srcFile == dstFile &&										// Pawn stayed in same column
    			    srcRank == 6 &&												// Pawn was on starting row
    				((srcRank - dstRank) == 2) &&								// Pawn moved forward two
    				chessBoard.getPieceAt(srcFile, srcRank-1) == null &&  		// Square in between is empty
    				destinationEmpty) {
    				return true;
    			}
    			
    			// Pawn captured a piece
    			if ((dstRank == srcRank - 1) &&
    				((dstFile == srcFile +1 ) || (dstFile == srcFile - 1))) {
    				
    				// Pawn captured a piece
    				if (!destinationEmpty) {
    					return true;
    				}
    				
    				if (previousMoveEnablesEnPassantFor(this)) {
    					return true;
    				}
    			}
    		}
    		
    		return false;
    	}

    }
    
	private boolean isValidMovmentForBishop(Square source, Square destination,
			ChessGameModel chessGame) {
		int srcRank = source.getRank();
		int srcFile = source.getFile();
		int dstRank = destination.getRank();
		int dstFile = destination.getFile();
		int largerRank, largerFile, smallerRank, smallerFile;
		ChessBoard chessBoard = chessGame.getChessBoard();

		// Calculate which rank and files are smaller, and which ones are larger
		largerRank = Math.max(srcRank, dstRank);
		smallerRank = (largerRank == srcRank) ? dstRank : srcRank;
		largerFile = Math.max(srcFile, dstFile);
		smallerFile = (largerFile == srcFile) ? dstFile : srcFile;

		// Check the squares in the middle of the diagonal in the +x +y direction
		if ((srcFile < dstFile && srcRank < dstRank) ||
			(srcFile > dstFile && srcRank > dstRank)) {

			// It is okay if the 
			for (int x = smallerFile+1, y = smallerRank+1; x < largerFile; x++, y++) {
				if (chessBoard.getPieceAt(x, y) != null)
					return false;
			}
			return true;
		}

		// If moving along the "\" diagonal
		if ((srcFile > dstFile && srcRank < dstRank) ||
			(srcFile < dstFile && srcRank > dstRank)) {

			// Check the squares in the middle of the diagonal in the +x -y direction 
			for (int x = smallerFile+1, y = largerRank-1; x < largerFile; x++, y--) {
				if (chessBoard.getPieceAt(x, y) != null)
					return false;
			}
			return true;
		}

		return false;
	}
    
	private boolean isValidMovmentForRook(Square source, Square destination,
			ChessGameModel chessGame) {

		int srcRank = source.getRank();
		int srcFile = source.getFile();
		int dstRank = destination.getRank();
		int dstFile = destination.getFile();
		ChessBoard chessBoard = chessGame.getChessBoard();	

		// Rook moved in x direction
		if (srcRank == dstRank && srcFile != dstFile) {

			// Piece moved in +x direction
			if (dstFile > srcFile) {	
				for (int x = srcFile+1; x < dstFile; x++) {
					// Make sure in-between squares are not occupied
					if (chessBoard.getPieceAt(x, srcRank) != null)
						return false;
				}
			}
			else {	// Piece moved in -x direction
				for (int x = srcFile-1; x > dstFile; x--) {
					// Make sure in-between squares are not occupied
					if (chessBoard.getPieceAt(x, srcRank) != null)
						return false;
				}
			}
			return true;
		}

		// Rook moved in y direction
		if (srcFile == dstFile && srcRank != dstRank) {

			// Piece moved in +y direction
			if (dstRank > srcRank) {	
				for (int y = srcRank+1; y < dstRank; y++) {
					// Make sure in-between squares are not occupied
					if (chessBoard.getPieceAt(srcFile, y) != null)
						return false;
				}
			}
			else {	// Piece moved in -x direction
				for (int y = srcRank-1; y > dstRank; y--) {
					// Make sure in-between squares are not occupied
					if (chessBoard.getPieceAt(srcFile, y) != null)
						return false;
				}
			}
			return true;
		}

		return false;
	}
	
	private boolean previousMoveEnablesEnPassantFor(Pawn p) {
		
		int opponentColor, opponentStartingRank, opponentRankPlusTwo;
		int srcRank = p.getSquare().getRank();
		int srcFile = p.getSquare().getFile();
		Move previousMove;
		
		// Black and white pawns start on different ranks
		if (p.getColor() == white) {
			opponentColor = black;
			opponentStartingRank = 6;
			opponentRankPlusTwo = 4;
		}
		else { // black
			opponentColor = white;
			opponentStartingRank = 1;
			opponentRankPlusTwo = 3;
		}
		
		try {
			previousMove = movesList.get(movesList.size()-1);
		}
		catch (NoSuchElementException e){
			// En passant isn't possible if nobody has moved
			return false;
		}
		
		// Pawn captured en passant
		if (previousMove.pieceMoved.getColor() == opponentColor &&
			previousMove.pieceMoved.getType() == pawn &&
			previousMove.sourceSquare.getRank() == opponentStartingRank &&
			previousMove.destinationSquare.getRank() == opponentRankPlusTwo &&
			srcRank == opponentRankPlusTwo &&
			((srcFile == previousMove.destinationSquare.getFile() + 1) ||
			 (srcFile == previousMove.destinationSquare.getFile() - 1))) {
			return true;
		}
		
		return false;
	}
	
	private boolean inBounds(int x, int y) {
		if (x >= 0 && x <= 7 && y >= 0 && y <= 7)
			return true;
		return false;
	}
	
	private Deque<Move> getRookLikeMoves(Piece rook) {
		
		Deque<Move> moves = new ArrayDeque<Move>(14);
		int file = rook.square.getFile();
		int rank = rook.square.getRank();

		
		// Move leftward from rook to left edge of the board
		for (int x = file-1; x >= 0; x--) {
			Piece capture = chessBoard.getPieceAt(x, rank);
			if (capture == null) {
				moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, rank)));
			}
			else { // There is a piece there
				if (capture.color != rook.color) // And it's the opponent's
					moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, rank)));
				break;
			}
		}
		
		// Move rightward from rook to right edge of the board
		for (int x = file+1; x <= 7; x++) {
			Piece capture = chessBoard.getPieceAt(x, rank);
			if (capture == null) {
				moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, rank)));
			}
			else { // There is a piece there
				if (capture.color != rook.color) // And it's the opponent's
					moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, rank)));
				break;
			}
		}
		
		// Move upward from rook to the top edge of the board
		for (int y = rank+1; y <= 7; y++) {
			Piece capture = chessBoard.getPieceAt(file, y);
			if (capture == null) {
				moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(file, y)));
			}
			else { // There is a piece there
				if (capture.color != rook.color) // And it's the opponent's
					moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(file, y)));
				break;
			}
		}
		
		// Move downward from rook to the bottom edge of the board
		for (int y = rank-1; y >= 0; y--) {
			Piece capture = chessBoard.getPieceAt(file, y);
			if (capture == null) {
				moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(file, y)));
			}
			else { // There is a piece there
				if (capture.color != rook.color) // And it's the opponent's
					moves.add(new Move(rook, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(file, y)));
				break;
			}
		}
		
		return moves;
	}
	
	private Deque<Move> getBishopLikeMoves(Piece bishop) {
		
		Deque<Move> moves = new ArrayDeque<Move>(13);
		int file = bishop.square.getFile();
		int rank = bishop.square.getRank();
		
		// Move up and to the right
		for (int x = file+1, y = rank+1; inBounds(x, y); x++, y++) {
			Piece capture = chessBoard.getPieceAt(x, y);
			if (capture == null) {
				moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, y)));
			}
			else { // There is a piece there
				if (capture.color != bishop.color) // And it's the opponent's
					moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, y)));
				break;
			}
		}
		
		// Move down and to the right
		for (int x = file+1, y = rank-1; inBounds(x, y); x++, y--) {
			Piece capture = chessBoard.getPieceAt(x, y);
			if (capture == null) {
				moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, y)));
			}
			else { // There is a piece there
				if (capture.color != bishop.color) // And it's the opponent's
					moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, y)));
				break;
			}
		}
		
		// Move up and to the left
		for (int x = file-1, y = rank+1; inBounds(x, y); x--, y++) {
			Piece capture = chessBoard.getPieceAt(x, y);
			if (capture == null) {
				moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, y)));
			}
			else { // There is a piece there
				if (capture.color != bishop.color) // And it's the opponent's
					moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, y)));
				break;
			}
		}
		
		// Move down and to the left
		for (int x = file-1, y = rank-1; inBounds(x, y); x--, y--) {
			Piece capture = chessBoard.getPieceAt(x, y);
			if (capture == null) {
				moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), null, chessBoard.getSquareAt(x, y)));
			}
			else { // There is a piece there
				if (capture.color != bishop.color) // And it's the opponent's
					moves.add(new Move(bishop, chessBoard.getSquareAt(file, rank), capture, chessBoard.getSquareAt(x, y)));
				break;
			}
		}
		
		return moves;
	}
	
	private boolean kingsideCastleIsLegal(int playerColor) {
		
		// FIXME: / Note:
		// This code relies on "whiteCheck" and "blackCheck" game state variables, this is an optimization
		// that takes advantage of the fact that whether or not a king is in check, is calculated after
		// a move has been made. If future code gets re-factored then it might be wise just to bite the
		// bullet and manually calculate check here.
		
		if (playerColor == white) {
			// Kingside-castle White
			if (whiteCheck == false &&											// White's king is not in check
				whiteKingMoved == false && 										// King hasn't moved
				whiteKingsideRookMoved == false &&								// Rook hasn't moved
				chessBoard.getPieceAt(5, 0) == null &&							// F1 is empty
				chessBoard.getPieceAt(6, 0) == null &&							// G1 is empty
				!squareIsUnderAttackBy(chessBoard.getSquareAt(5, 0), black) &&	// Nobody is attacking C1
				!squareIsUnderAttackBy(chessBoard.getSquareAt(6, 0), black)) {	// Nobody is attacking D1
				return true;
   			}
    	}
    	else {
			// Kingside-castle Black
			if (blackCheck == false &&											// Black's king is not in check
				blackKingMoved == false && 										// King hasn't moved
				blackKingsideRookMoved == false &&								// Rook hasn't moved
				chessBoard.getPieceAt(5, 7) == null &&							// B8 is empty
				chessBoard.getPieceAt(6, 7) == null &&							// C8 is empty
				!squareIsUnderAttackBy(chessBoard.getSquareAt(5, 7), white) &&	// Nobody is attacking C8
				!squareIsUnderAttackBy(chessBoard.getSquareAt(6, 7), white)) {	// Nobody is attacking D8
				return true;
   			}
    	}
    	
		return false;
	}
	
	private boolean queensideCastleIsLegal(int playerColor) {
		
		// FIXME: / Note:
		// This code relies on "whiteCheck" and "blackCheck" game state variables, this is an optimization
		// that takes advantage of the fact that whether or not a king is in check, is calculated after
		// a move has been made. If future code gets re-factored then it might be wise just to bite the
		// bullet and manually calculate check here.
		
		if (playerColor == white) {
			// Queenside-castle White
			if (whiteCheck == false &&											// White's king is not in check
				whiteKingMoved == false && 										// King hasn't moved
				whiteQueensideRookMoved == false &&								// Rook hasn't moved
				chessBoard.getPieceAt(1, 0) == null &&							// B1 is empty
				chessBoard.getPieceAt(2, 0) == null &&							// C1 is empty
				chessBoard.getPieceAt(3, 0) == null &&							// D1 is empty
				!squareIsUnderAttackBy(chessBoard.getSquareAt(2, 0), black) &&	// Nobody is attacking C1
				!squareIsUnderAttackBy(chessBoard.getSquareAt(3, 0), black)) {	// Nobody is attacking D1
				return true;
   			}
    	}
    	else {
			// Queenside-castle Black
			if (blackCheck == false &&											// Black's king is not in check
				blackKingMoved == false && 										// King hasn't moved
				blackQueensideRookMoved == false &&								// Rook hasn't moved
				chessBoard.getPieceAt(1, 7) == null &&							// B8 is empty
				chessBoard.getPieceAt(2, 7) == null &&							// C8 is empty
				chessBoard.getPieceAt(3, 7) == null &&							// D8 is empty
				!squareIsUnderAttackBy(chessBoard.getSquareAt(2, 7), white) &&	// Nobody is attacking C8
				!squareIsUnderAttackBy(chessBoard.getSquareAt(3, 7), white)) {	// Nobody is attacking D8
				return true;
   			}	
    	}
    	
		return false;
	}
}
