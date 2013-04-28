package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameGui {
	
	private static final int NUM_ROWS = 25;
	private static final int NUM_COLS = 17;
	
	private JPanel[][] boardGui = new JPanel[NUM_ROWS][NUM_COLS];
	private HashMap<Player, Color> colorMap = new HashMap<Player, Color>();
	
	private static JFrame window = new JFrame("Chinese Checkers");
	private Board b;
	
	GameGui(List<Player> players, Board b) {
		this.b = b;
		
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				if (b.board[j][i / 2]) {
					boardGui[i][j] = new CircPanel(Color.white, i, j);
				} else {
					boardGui[i][j] = new JPanel();
				}
				//fill in gaps in the rows
				if (j % 2 == 0 && i % 2 == 1) {
					boardGui[i][j] = new JPanel();
				} else if (j % 2 == 1 && i % 2 == 0) {
					boardGui[i][j] = new JPanel();
				}
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
		update();
	}
	
	void update() {
		JPanel p = new JPanel(new GridLayout(NUM_COLS, NUM_ROWS));
		for (int i = 0; i < b.state.length; i++) {
			for (int j = 0; j < b.state[0].length; j++) {
				if (i % 2 == 0) {
					if (b.state[i][j] != null) {
						boardGui[j * 2][i] = new CircPanel(colorMap.get(b.state[i][j]), i, j);
					}
				} else {
					if (b.state[i][j] != null) {
						boardGui[(j * 2) + 1][i] = new CircPanel(colorMap.get(b.state[i][j]), i, j);
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

class CircPanel extends JPanel implements MouseListener {
	Color c;
	int boardIndexI;
	int boardIndexJ;
	
	CircPanel(Color c, int i, int j) {
		this.c = c;
		this.boardIndexI = i;
		this.boardIndexJ = j;
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(c);
		g.fillOval(5, 5, 10, 10);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Action involving " + this.boardIndexI +","+this.boardIndexJ);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
