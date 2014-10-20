package com.marklarwill.chessVariants;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public class Square {

	private Piece piece;
	private int rank;
	private int file;
	private int color;

	Square(int file, int rank, int color) {
		this.rank = rank;
		this.file = file;
		this.color = color;
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public int getRank() {
		return rank;
	}
	
	public int getFile() {
		return file;
	}
}
