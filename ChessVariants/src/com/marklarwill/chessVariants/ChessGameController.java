package com.marklarwill.chessVariants;

public class ChessGameController implements ControllerInterface {
	
	ChessGameModelInterface chessGameModel;
	ChessGameView chessGameView;
	
	public ChessGameController(ChessGameModelInterface model) {
		chessGameModel = model;
		chessGameView = new ChessGameView(this, model);
		chessGameView.createView();
	}

	@Override
	public boolean attemptMove(PlayerInput pi) {
		// TODO Auto-generated method stub
		return false;
	}

}
