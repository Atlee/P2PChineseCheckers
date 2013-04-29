package test;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GuiTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Picture Test");
		JPanel panel = new JPanel(new GridLayout(3,3));
		
		JPanel test = new JPanel();
		Graphics g = test.getGraphics();
		if (g == null) {
			System.out.println("True");
		}
		JPanel sq1 = new SquarePanel();
		
		panel.add(sq1);
		frame.setContentPane(panel);
		
		frame.pack();
		frame.setVisible(true);
	}
}

@SuppressWarnings("serial")
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
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
}
