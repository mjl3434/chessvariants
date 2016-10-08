package com.marklarwill.chessVariants;

import java.util.List;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public interface ChessGameModelInterface {
	
	int onePlayerGame = 1;
	int twoPlayerGame = 2;
	
	boolean makeMove(PlayerInput pi);
	
	List<Piece> getPieceLocations();
	
	void registerObserver(PieceLocationsObserver o);

	void removeObserver(PieceLocationsObserver o);
	
	void registerObserver(CheckmateObserver o);

	void removeObserver(CheckmateObserver o);
	
	void registerObserver(StalemateObserver o);

	void removeObserver(StalemateObserver o);
}
