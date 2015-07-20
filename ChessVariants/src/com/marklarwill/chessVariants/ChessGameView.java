package com.marklarwill.chessVariants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.marklarwill.chessVariants.ChessGameModel.Piece;

public class ChessGameView implements MouseListener, 
									  MouseMotionListener, 
									  PieceLocationsObserver, 
									  CheckmateObserver, 
									  StalemateObserver {
	
	static final int A = 0;
	static final int B = 1;
	static final int C = 2;
	static final int D = 3;
	static final int E = 4;
	static final int F = 5;
	static final int G = 6;
	static final int H = 7;
	
	private ChessGameModelInterface chessGameModel;
	private ControllerInterface chessGameController;
	private ChessVariantsView chessVariantsView;

    private Color darkSquare = Color.gray;
    private Color lightSquare = Color.white;
	JPanel chessBoardPanel;
    ViewSquare sourceSquare = new ViewSquare(0, 0);
    ViewPiece draggedPiece;
    int xAdjustment;
    int yAdjustment;
    Point parentLocation;
	
    /*
	public ChessGameView(ControllerInterface controller, ChessGameModelInterface model) {
		
		chessGameController = controller;
		chessGameModel = model;
		model.registerObserver((PieceLocationsObserver)this);
		model.registerObserver((CheckmateObserver)this);
		model.registerObserver((StalemateObserver)this);
	}
	*/
	
	public ChessGameView(ChessGameModelInterface model, ChessVariantsView view, ControllerInterface controller) {
		
		// Does the decorator make sense here???
		
		chessGameModel = model;
		chessVariantsView = view;
		chessGameController = controller;
		
		// The View and the Model should be totally independent of each other
		// however our implementation still requires us to register and 
		// unregister for events. This should be the only time we reference
		// the model directly.
		//chessGameModel.registerObserver((PieceLocationsObserver)this);
		//chessGameModel.registerObserver((CheckmateObserver)this);
		//chessGameModel.registerObserver((StalemateObserver)this);
		
        chessBoardPanel = new JPanel();
        chessBoardPanel.setLayout(new GridLayout(8, 8));
        chessBoardPanel.setPreferredSize(chessVariantsView.boardSize);
        chessBoardPanel.setBounds(0, 0, chessVariantsView.boardSize.width, chessVariantsView.boardSize.height);
        
        chessVariantsView.layeredPane.add(chessBoardPanel, JLayeredPane.DEFAULT_LAYER);
        chessVariantsView.layeredPane.addMouseListener(this);
        chessVariantsView.layeredPane.addMouseMotionListener(this);
        
        // The squares themselves are JPanels
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
            	ViewSquare square = new ViewSquare(new BorderLayout(), x, y);
                square.setBackground((x + y) % 2 == 1 ? lightSquare : darkSquare);
                chessBoardPanel.add(square);
            }
        }
        
        // Load all the chess pieces, and add them to the board
        addPiecesToBoard();
        
        chessVariantsView.frame.pack();
	}

	private void addPiecesToBoard() {
		
    	ImageIcon blackPawn = new ImageIcon(ChessGameView.class.getResource("resources/images/BP.png"));
    	ImageIcon blackBishop = new ImageIcon(ChessGameView.class.getResource("resources/images/BB.png"));
    	ImageIcon blackKnight = new ImageIcon(ChessGameView.class.getResource("resources/images/BN.png"));
        ImageIcon blackRook = new ImageIcon(ChessGameView.class.getResource("resources/images/BR.png"));
        ImageIcon blackQueen = new ImageIcon(ChessGameView.class.getResource("resources/images/BQ.png"));
        ImageIcon blackKing = new ImageIcon(ChessGameView.class.getResource("resources/images/BK.png"));
        ImageIcon whitePawn = new ImageIcon(ChessGameView.class.getResource("resources/images/WP.png"));
        ImageIcon whiteBishop = new ImageIcon(ChessGameView.class.getResource("resources/images/WB.png"));
        ImageIcon whiteKnight = new ImageIcon(ChessGameView.class.getResource("resources/images/WN.png"));
        ImageIcon whiteRook = new ImageIcon(ChessGameView.class.getResource("resources/images/WR.png"));
        ImageIcon whiteQueen = new ImageIcon(ChessGameView.class.getResource("resources/images/WQ.png"));
        ImageIcon whiteKing = new ImageIcon(ChessGameView.class.getResource("resources/images/WK.png"));
        
        // The pieces on the other hand are JLabels, put in the chessBoardViewView JPanels
        int squareIndex = 0;
        
        // A8
        JLabel piece = new ViewPiece(blackRook, ChessGameModel.rook, ChessGameModel.black);
        JPanel panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // B8
        piece = new ViewPiece(blackKnight, ChessGameModel.knight, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // C8
        piece = new ViewPiece(blackBishop, ChessGameModel.bishop, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // D8
        piece = new ViewPiece(blackQueen, ChessGameModel.queen, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // E8
        piece = new ViewPiece(blackKing, ChessGameModel.king, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // F8
        piece = new ViewPiece(blackBishop, ChessGameModel.bishop, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // G8
        piece = new ViewPiece(blackKnight, ChessGameModel.knight, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // H8
        piece = new ViewPiece(blackRook, ChessGameModel.rook, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        
        for (int i = 0; i < 8; i++) {
        	piece = new ViewPiece(blackPawn, ChessGameModel.pawn, ChessGameModel.black);
        	panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        	panel.add(piece);
        	//[i][7]
        }
        
        squareIndex = 8*6;
        
        for(int i = 0; i < 8; i++) {
        	// [i]2
        	piece = new ViewPiece(whitePawn, ChessGameModel.pawn, ChessGameModel.white);
        	panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        	panel.add(piece);
        }
        
        // A1
        piece = new ViewPiece(whiteRook, ChessGameModel.rook, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // B1
        piece = new ViewPiece(whiteKnight, ChessGameModel.knight, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // C1
        piece = new ViewPiece(whiteBishop, ChessGameModel.bishop, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // D1
        piece = new ViewPiece(whiteQueen, ChessGameModel.queen, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // E1
        piece = new ViewPiece(whiteKing, ChessGameModel.king, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // F1
        piece = new ViewPiece(whiteBishop, ChessGameModel.bishop, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // G1
        piece = new ViewPiece(whiteKnight, ChessGameModel.knight, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        // H1
        piece = new ViewPiece(whiteRook, ChessGameModel.rook, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);

	}
	
	@Override
	public void updatePieceLocations() {
		
		List<Piece> piecesOnBoard = chessGameModel.getPieceLocations();
		// Code to draw the pieces
	}

	@Override
	public void updateStalemate() {

		System.out.println("Game Over: Stalemate.");
		JOptionPane.showMessageDialog(chessVariantsView.frame, "Game Over: Stalemate.");
	}

	@Override
	public void updateCheckmate() {

		System.out.println("Game Over: Checkmate.");
		
		// Create a popup telling the user the game is over
		JOptionPane.showMessageDialog(chessVariantsView.frame, "Game Over: Checkmate.");
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		
        if (draggedPiece == null)
        	return;
        
        draggedPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
    	// Get the points where the user clicked
    	int x = e.getX();
    	int y = e.getY();
    	
    	// Get the origin of the square under where the user clicked
    	draggedPiece = null;
        Component c =  chessBoardPanel.findComponentAt(x, y);
        
        if (!(c instanceof ViewPiece))
        	return;

        // Save the source location
        ViewSquare parent = (ViewSquare)c.getParent();
        sourceSquare.setRankAndFile(parent.getRank(), parent.getFile());
        
        // When we drag and drop we need to draw the piece with the same origin
        // as the square it started in. If we don't then the piece will appear
        // to "jump", which isn't what we want.  
        parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - x;
        yAdjustment = parentLocation.y - y;
        draggedPiece = (ViewPiece)c;
        draggedPiece.setLocation(x + xAdjustment, y + yAdjustment);

        chessVariantsView.layeredPane.add(draggedPiece, JLayeredPane.DRAG_LAYER);
        chessVariantsView.layeredPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		chessVariantsView.layeredPane.setCursor(null);

        if (draggedPiece == null) return;
        
        int x = e.getX();
        int y = e.getY();

        draggedPiece.setVisible(false);
        chessVariantsView.layeredPane.remove(draggedPiece);
        draggedPiece.setVisible(true);

        // Find the components at the source, and destination of the drag & drop
        Component s = chessBoardPanel.findComponentAt(parentLocation.x, parentLocation.y);
        Component d = chessBoardPanel.findComponentAt(x, y);
        Container srcSquare = (s instanceof ViewPiece) ? s.getParent() : (Container)s;
        Container dstSquare = (d instanceof ViewPiece) ? d.getParent() : (Container)d;

         // If the destination is outside of the board
        if (x > chessVariantsView.layeredPane.getWidth() || x < 0 || y > chessVariantsView.layeredPane.getHeight() || y < 0) {
        	
        	// Then we should reject the player input and do nothing
            srcSquare.add(draggedPiece);
            srcSquare.validate();
            return;
        }
        
        // Find out the logical coordinate we moved to
        int dstRank = ((ViewSquare)dstSquare).getRank();
        int dstFile = ((ViewSquare)dstSquare).getFile();
        
        // Attempt to make the move with our model
        boolean validMove = false;
        PlayerInput pi = new PlayerInput(sourceSquare.getFile(), sourceSquare.getRank(), dstFile, dstRank);
        validMove = chessGameController.attemptMove(pi);
        
        if (validMove) {
        	
        	// If there is a piece (JLabel) at the destination
        	if (d instanceof JLabel) {
        		dstSquare.remove(0);
        	}
        	dstSquare.add(draggedPiece);
        	dstSquare.validate();
        }
        else {
        	// Then we should reject the player input and do nothing
            srcSquare.add(draggedPiece);
            srcSquare.validate();
        }
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) { }

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }
}
