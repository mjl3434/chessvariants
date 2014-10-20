package com.marklarwill.chessVariants;

import javax.swing.Icon;
import javax.swing.JLabel;

public class ViewPiece extends JLabel {

	private int typeOfPiece;
	private int color;
	
	public ViewPiece(Icon image, int piece, int color) {
		super(image);
		typeOfPiece = piece;
		this.color = color;
	}

}
