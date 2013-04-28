package test;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GuiTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Picture Test");
		JPanel panel = new JPanel(new GridLayout(3,3));
		
		ImageIcon sq = new ImageIcon("img/square.png");
		ImageIcon em = new ImageIcon("img/empty.png");
		JPanel test = new JPanel();
		Graphics g = test.getGraphics();
		if (g == null) {
			System.out.println("True");
		}
		JPanel sq1 = new SquarePanel();
		JLabel sq2 = new JLabel("", sq, JLabel.CENTER);
		JLabel sq3 = new JLabel("", sq, JLabel.CENTER);
		JLabel em1 = new JLabel("", em, JLabel.CENTER);
		
		panel.add(sq1);
		frame.setContentPane(panel);
		
		frame.pack();
		frame.setVisible(true);
	}
}

class SquarePanel extends JPanel implements MouseListener {
	
	SquarePanel() {
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		g.fillRect(5, 5, 10, 10);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("You clicked the square");
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
