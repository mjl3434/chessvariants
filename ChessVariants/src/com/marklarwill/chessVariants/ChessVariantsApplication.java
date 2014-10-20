package com.marklarwill.chessVariants;

public class ChessVariantsApplication {
	
    public static void main (String[] args) {
		ChessGameModel chessGameModel = new ChessGameModel();
		ControllerInterface controller = new ChessGameController(chessGameModel);
		
		while(true) {
			System.out.print(".");
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

}
