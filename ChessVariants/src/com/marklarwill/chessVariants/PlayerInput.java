package com.marklarwill.chessVariants;

public class PlayerInput {

	private int srcFile;
	private int srcRank;
	private int dstFile;
	private int dstRank;
	private boolean valid;
	
    public PlayerInput(int srcFile, int srcRank, int dstFile, int dstRank) {
    	this.srcFile = srcFile;
    	this.srcRank = srcRank;
    	this.dstFile = dstFile;
    	this.dstRank = dstRank;
    	valid = false;
    }
    
    public void setValid(boolean validity) {
    	valid = validity;
    	
    	// future code which copies down local data that states exactly what
    	// game / sequence of moves this move is vaid for, and which move it
    	// is in that sequence
    }
    
    public boolean getValid() {
    	return valid;
    }
    
    // TODO / FIXME - "valid" only makes sense if we know exactly what the
    // entire game state is. The same castling move may be valid in one game
    // but not in another, if for example a rook has moved out of position, but
    // moved back then castling with that rook is illegal. In the future come
    // back and find a way to reference the moves list and generate a unique
    // hash value that encompasses all of the moves. Also store a variable
    // indicating which move this is valid for.
    //private int validForMoveNumber;
    
    public int getSrcFile() {
    	return srcFile;
    }
    
    public int getSrcRank() {
    	return srcRank;
    }
    
    public int getDstFile() {
    	return dstFile;
    }
    
    public int getDstRank() {
    	return dstRank;
    }
    

}
