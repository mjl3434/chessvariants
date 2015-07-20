package com.marklarwill.chessVariants;

public interface ControllerInterface {

	// The controller interface defines actions the view can ask the model to do
	
	boolean attemptMove(PlayerInput pi);

	ChessGameModelInterface createTwoPlayerChessGame();
	
	ChessGameModelInterface createOnelayerChessGame();
}
