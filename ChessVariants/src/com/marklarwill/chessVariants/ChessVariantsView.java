package com.marklarwill.chessVariants;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class ChessVariantsView {
	
	private ControllerInterface chessGameController;
	//private ChessGameModelInterface chessGameModel;
	
	private ChessGameView chessGameView;

	// FIXME: I exposed these members as package, so the sub-views can access 
	// them, but this seems wrong because it allows everyone to access them 
	// when we really want access restricted to the sub-views only.
	JFrame frame;
    Dimension boardSize = new Dimension(600, 600);
	JLayeredPane layeredPane = new JLayeredPane();
	
	public ChessVariantsView(ControllerInterface controller) {
		chessGameController = controller;
	}

	 public void createView() {

		/* Schedule UI updates for the Event Dispatch Thread */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
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
				startNewOnePlayerChessGame();
			}
		});
		
		JMenuItem newTwoPlayerGame = new JMenuItem("Two Player Game");
		newTwoPlayerGame.setMnemonic(KeyEvent.VK_T);
		newTwoPlayerGame.setToolTipText("Start a new chess game with two human players.");
		game.add(newTwoPlayerGame);
		newTwoPlayerGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startNewTwoPlayerChessGame();
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
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}

	private void saveGame() {
		System.out.println("User requested save game (not yet implemented)");
	}
	
	private void loadGame() {
		System.out.println("User requested load game (not yet implemented)");
	}
	
	private void startNewOnePlayerChessGame() {
		
		System.out.println("One player games are not implmented yet. AI is required.");
		
		chessGameController.createOnePlayerChessGame();
	}
	
	private void startNewTwoPlayerChessGame() {
		
		System.out.println("User requested two player chess game");
		
		chessGameController.createTwoPlayerChessGame();
	}
}
