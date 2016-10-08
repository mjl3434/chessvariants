package com.marklarwill.chessVariants;

public class ChessGameController implements ControllerInterface {
	
	ChessVariantsView chessVariantsView;
	
	ChessGameModelInterface chessGameModel;
	ChessGameView chessGameView;
	
	// In the future the controller will have references to all of the various 
	// chess variants models and views.
	// Chess960GameModelInterface chess960GameModel;
	
	
	/*
	public ChessGameController(ChessGameModelInterface model) {
		chessGameModel = model;
		chessGameView = new ChessGameView(this, model);
		chessGameView.createView();
	}
	*/
	
	public ChessGameController() {
		
		// This view is a generic view, which supports spawning other models and sub-views for those models
		chessVariantsView = new ChessVariantsView(this);
		chessVariantsView.createView();
	}
	
	public ChessGameModelInterface createTwoPlayerChessGame() {
		
		chessGameModel = new ChessGameModel(chessGameModel.twoPlayerGame);
		chessGameView = new ChessGameView(chessGameModel, chessVariantsView, this);
		
		// The view is interested in knowing these things from the model
		chessGameModel.registerObserver((PieceLocationsObserver)chessGameView);
		chessGameModel.registerObserver((CheckmateObserver)chessGameView);
		chessGameModel.registerObserver((StalemateObserver)chessGameView);
		
		// FIXME: do we even need to return anything?
		// The sub-view needs to be able to be an observer
		// that means it needs to be able to register and unregister
		return chessGameModel;
	}
	
	public ChessGameModelInterface createOnePlayerChessGame() {
		
		chessGameModel = new ChessGameModel(chessGameModel.onePlayerGame);
		chessGameView = new ChessGameView(chessGameModel, chessVariantsView, this);
		
		// The view is interested in knowing these things from the model
		chessGameModel.registerObserver((PieceLocationsObserver)chessGameView);
		chessGameModel.registerObserver((CheckmateObserver)chessGameView);
		chessGameModel.registerObserver((StalemateObserver)chessGameView);
		
		// FIXME: do we even need to return anything?
		// The sub-view needs to be able to be an observer
		// that means it needs to be able to register and unregister
		return chessGameModel;
	}
	

	@Override
	public boolean attemptMove(PlayerInput pi) {

		return chessGameModel.makeMove(pi);
	}

}
