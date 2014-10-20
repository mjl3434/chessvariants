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
	
	ChessGameModelInterface chessGameModel;
	ControllerInterface chessGameController;
	JLayeredPane layeredPane = new JLayeredPane();
	JPanel chessBoardPanel;
	JFrame frame;
    Color darkSquare = Color.gray;
    Color lightSquare = Color.white;
    Dimension boardSize = new Dimension(600, 600);
    ViewSquare sourceSquare = new ViewSquare(0, 0);
    ViewPiece draggedPiece;
    int xAdjustment;
    int yAdjustment;
    Point parentLocation;
	
	public ChessGameView(ControllerInterface controller, ChessGameModelInterface model) {
		
		chessGameController = controller;
		chessGameModel = model;
		model.registerObserver((PieceLocationsObserver)this);
		model.registerObserver((CheckmateObserver)this);
		model.registerObserver((StalemateObserver)this);
	}

	private void createAndShowGUI()	{
		
		// Create the object which all other objects go inside of
		frame = new JFrame();
		
		// Add everything inside of it
		addComponentsToPane(frame.getContentPane());
		
        // Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void addComponentsToPane(Container pane) {
		
		// Create an empty Menu Bar
		JMenuBar menubar = new JMenuBar();
		
		// Add: File Menu
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		// File > Save game
		JMenuItem saveGame = new JMenuItem("Save game");
		saveGame.setMnemonic(KeyEvent.VK_S);
		saveGame.setToolTipText("Save the current game");
		file.add(saveGame);
		saveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGame();
			}
		});
		
		// File > Load saved game
		JMenuItem loadGame = new JMenuItem("Load saved game");
		loadGame.setMnemonic(KeyEvent.VK_L);
		loadGame.setToolTipText("Load a saved game");
		file.add(loadGame);
		loadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadGame();
			}
		});
		
		// Add: File > Exit
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		file.add(exit);
		
		// Add: Game Menu
		JMenu game = new JMenu("Game");
		game.setMnemonic(KeyEvent.VK_G);
		
		// Add: Game > New Game
		JMenuItem newOnePlayerGame = new JMenuItem("One Player Game");
		newOnePlayerGame.setMnemonic(KeyEvent.VK_O);
		newOnePlayerGame.setToolTipText("Start a new chess game vs. a computer opponent.");
		game.add(newOnePlayerGame);
		newOnePlayerGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startNewOnePlayerGame();
			}
		});
		
		JMenuItem newTwoPlayerGame = new JMenuItem("Two Player Game");
		newTwoPlayerGame.setMnemonic(KeyEvent.VK_T);
		newTwoPlayerGame.setToolTipText("Start a new chess game with two human players.");
		game.add(newTwoPlayerGame);
		newTwoPlayerGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startNewTwoPlayerGame();
			}
		});
		
		// Fill empty Menu Bar
		menubar.add(file);
		menubar.add(game);
		
		// Add the Menu Bar to the top-center section
		pane.add(menubar, BorderLayout.PAGE_START);
		
		// The layered pane holds the board, but add it now so that our app looks good
		layeredPane.setPreferredSize(boardSize);
		frame.add(layeredPane);
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
        
        JLabel piece = new ViewPiece(blackRook, ChessGameModel.rook, ChessGameModel.black);
        JPanel panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        
        piece = new ViewPiece(blackKnight, ChessGameModel.knight, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[B][8]
        
        piece = new ViewPiece(blackBishop, ChessGameModel.bishop, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[C][8]
        
        piece = new ViewPiece(blackQueen, ChessGameModel.queen, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[D][8]
        
        piece = new ViewPiece(blackKing, ChessGameModel.king, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[E][8]
        
        piece = new ViewPiece(blackBishop, ChessGameModel.bishop, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[F][8]
        
        piece = new ViewPiece(blackKnight, ChessGameModel.knight, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[G][8]
        
        piece = new ViewPiece(blackRook, ChessGameModel.rook, ChessGameModel.black);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[H][8]
        
        for (int i = 0; i < 8; i++) {
        	piece = new ViewPiece(blackPawn, ChessGameModel.pawn, ChessGameModel.black);
        	panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        	panel.add(piece);
        	//[i][7]
        }
        
        squareIndex = 8*6;
        
        for(int i = 0; i < 8; i++) {
        	piece = new ViewPiece(whitePawn, ChessGameModel.pawn, ChessGameModel.white);
        	panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        	panel.add(piece);
        	//[i][2]
        }
        
        piece = new ViewPiece(whiteRook, ChessGameModel.rook, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[A][1]
        
        piece = new ViewPiece(whiteKnight, ChessGameModel.knight, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[B][1]
        
        piece = new ViewPiece(whiteBishop, ChessGameModel.bishop, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[C][1]
        
        piece = new ViewPiece(whiteQueen, ChessGameModel.queen, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[D][1]
        
        piece = new ViewPiece(whiteKing, ChessGameModel.king, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[E][1]
        
        piece = new ViewPiece(whiteBishop, ChessGameModel.bishop, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[F][1]
        
        piece = new ViewPiece(whiteKnight, ChessGameModel.knight, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[G][1]
        
        piece = new ViewPiece(whiteRook, ChessGameModel.rook, ChessGameModel.white);
        panel = (JPanel)chessBoardPanel.getComponent(squareIndex++);
        panel.add(piece);
        //[H][1]
	}
	
	@Override
	public void updatePieceLocations() {
		List<Piece> piecesOnBoard = chessGameModel.getPieceLocations();
		// Code to draw the pieces
	}

	@Override
	public void updateStalemate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCheckmate() {
		// TODO Auto-generated method stub
		
	}

	 public void createView() {


		/* Schedule UI updates for the Event Dispatch Thread */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
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

        // If we found a JPanel then it's an empty square w/o any piece
        //if (c instanceof JPanel)
        //	return;
        
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

        layeredPane.add(draggedPiece, JLayeredPane.DRAG_LAYER);
        layeredPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		layeredPane.setCursor(null);

        if (draggedPiece == null) return;
        
        int x = e.getX();
        int y = e.getY();

        draggedPiece.setVisible(false);
        layeredPane.remove(draggedPiece);
        draggedPiece.setVisible(true);

        // Find the components at the source, and destination of the drag & drop
        Component s = chessBoardPanel.findComponentAt(parentLocation.x, parentLocation.y);
        Component d = chessBoardPanel.findComponentAt(x, y);
        Container srcSquare = (s instanceof ViewPiece) ? s.getParent() : (Container)s;
        Container dstSquare = (d instanceof ViewPiece) ? d.getParent() : (Container)d;

         // If the destination is outside of the board
        if (x > layeredPane.getWidth() || x < 0 || y > layeredPane.getHeight() || y < 0) {
        	
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
        validMove = chessGameModel.makeMove(pi);
        
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
	
	private void saveGame() {
		System.out.println("User requested save game (not yet implemented)");
	}
	
	private void loadGame() {
		System.out.println("User requested load game (not yet implemented)");
	}
	
	private void startNewOnePlayerGame() {
		System.out.println("User requested one player chess game");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
        chessBoardPanel = new JPanel();
        chessBoardPanel.setLayout(new GridLayout(8, 8));
        chessBoardPanel.setPreferredSize(boardSize);
        chessBoardPanel.setBounds(0, 0, boardSize.width, boardSize.height);
        
        layeredPane.add(chessBoardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);
        
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
        
		frame.pack();
	}
	
	private void startNewTwoPlayerGame() {
		System.out.println("User requested two player chess game");
		
		//model.startNewGame(model.chessGameType, 2);
		//myframe.add(model.game);
		//myframe.pack();
	}
}
