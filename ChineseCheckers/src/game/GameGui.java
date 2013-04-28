package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameGui {
	
	private static final int NUM_ROWS = 25;
	private static final int NUM_COLS = 17;
	
	private JPanel[][] boardGui = new JPanel[NUM_ROWS][NUM_COLS];
	private HashMap<Player, Color> colorMap = new HashMap<Player, Color>();
	
	private JFrame window = new JFrame("Chinese Checkers");
	
	GameGui(List<Player> players) {
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				boardGui[i][j] = new CircPanel(Color.white);
			}
		}
		
		colorMap.put(Board.empty, Color.white);
		for (int i = 0; i < players.size(); i++) {
			switch (i) {
			case 0:
				colorMap.put(players.get(i), Color.red);
				break;
			case 1:
				colorMap.put(players.get(i), Color.blue);
				break;
			case 2:
				colorMap.put(players.get(i), Color.green);
				break;
			case 3:
				colorMap.put(players.get(i), Color.yellow);
				break;
			case 4:
				colorMap.put(players.get(i), Color.orange);
				break;
			case 5:
				colorMap.put(players.get(i), Color.magenta);
				break;
			}
		}
	}
	
	void update(Board b) {
		JPanel p = new JPanel(new GridLayout(NUM_COLS, NUM_ROWS));
		for (int i = 0; i < b.state.length; i++) {
			for (int j = 0; j < b.state[0].length; j++) {
				if (i % 2 == 0) {
					if (b.state[i][j] != null) {
						boardGui[j * 2][i] = new CircPanel(colorMap.get(b.state[i][j]));
					}
				} else {
					if (b.state[i][j] != null) {
						boardGui[(j * 2) + 1][i] = new CircPanel(colorMap.get(b.state[i][j]));
					}
				}
			}
		}
		
		for (int j = 0; j < boardGui[0].length; j++) {
			for (int i = 0; i < boardGui.length; i++) {
				p.add(boardGui[i][j]);
			}
		}
		
		window.getContentPane().removeAll();
		window.setContentPane(p);
		window.setSize(800, 800);
		window.setVisible(true);
	}
}

class CircPanel extends JPanel {
	Color c; 
	
	CircPanel(Color c) {
		this.c = c;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(c);
		g.fillOval(5, 5, 10, 10);
	}
}
