package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameGui {

	private static final int NUM_ROWS = 25;
	private static final int NUM_COLS = 17;

	private JPanel[][] boardGui = new JPanel[NUM_ROWS][NUM_COLS];
	private HashMap<Player, Color> colorMap = new HashMap<Player, Color>();

	private static JFrame window = new JFrame("Chinese Checkers");
	private Board b;

	@SuppressWarnings("static-access")
	GameGui(List<Player> players, Board b) {
		this.b = b;

		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				if (b.board[j][i / 2]) {
					boardGui[i][j] = new CircPanel(Color.white, false, i, j);
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
		update(false);
	}

	Move getMove() {
		update(true);
		Move move = null;
		return move;
	}

	void update(boolean reactive) {
		JPanel p = new JPanel(new GridLayout(NUM_COLS, NUM_ROWS));
		for (int i = 0; i < b.state.length; i++) {
			for (int j = 0; j < b.state[0].length; j++) {
				if (i % 2 == 0) {
					if (b.state[i][j] != null) {
						boardGui[j * 2][i] = new CircPanel(colorMap.get(b.state[i][j]), reactive, i, j);
					}
				} else {
					if (b.state[i][j] != null) {
						boardGui[(j * 2) + 1][i] = new CircPanel(colorMap.get(b.state[i][j]), reactive, i, j);
					}
				}
			}
		}

		for (int j = 0; j < boardGui[0].length; j++) {
			for (int i = 0; i < boardGui.length; i++) {
				p.add(boardGui[i][j]);
			}
		}

		JPanel p3 = new JPanel(new BorderLayout());
		p3.add(p, BorderLayout.CENTER);

		if(reactive) {
			JPanel p2 = new JPanel(new BorderLayout());

			JButton submit = new JButton("Submit Move");
			submit.setPreferredSize(new Dimension(25, 25));
			submit.setActionCommand("Submit Move");
			submit.addActionListener(new SubmitListener(p));
			
			p2.add(submit);
			p3.add(p2, BorderLayout.PAGE_END);
		}

		window.getContentPane().removeAll();
		window.getContentPane().setLayout(new BorderLayout());
		window.setContentPane(p3);
		window.setSize(800, 800);
		window.setVisible(true);
	}


}

class SubmitListener implements ActionListener {

	JPanel p;

	public SubmitListener(JPanel p) {
		this.p = p;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {


	}

}

@SuppressWarnings("serial")
class CircPanel extends JPanel implements MouseListener {

	final Color c;
	final boolean reactive;
	final int boardIndexI;
	final int boardIndexJ;

	boolean selected = false;
	Long selectTime = null;

	CircPanel(Color c, boolean reactive, int i, int j) {
		this.c = c;
		this.reactive = reactive;
		this.boardIndexI = i;
		this.boardIndexJ = j;
		this.addMouseListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(c);
		g.fillOval(5, 5, 10, 10);
		if(selected) {
			g.setColor(Color.BLACK);
			g.drawOval(4, 4, 12, 12);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(reactive) {
			System.out.println("You clicked on " + this.boardIndexI +","+this.boardIndexJ);
			selected = !selected;
			selectTime = System.currentTimeMillis();
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
}
