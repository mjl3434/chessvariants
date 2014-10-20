package com.marklarwill.chessVariants;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class ViewSquare extends JPanel {

	private int rank;
	private int file;
	
	public ViewSquare(BorderLayout bl, int file, int rank) {
		super(bl);
		this.file = file;
		this.rank = rank;
	}
	
	public ViewSquare(int file, int rank) {
		this.file = file;
		this.rank = rank;
	}

	public void setRankAndFile(int rank, int file) {
		this.file = file;
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
	
	public int getFile() {
		return file;
	}
}
