package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MyGui extends UI {

	public MyGui() {
		setTitle("Chinese Checkers 0.1");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//set size
		int windowWidth = 300;
		int windowHeight = 300;
		setSize(windowWidth, windowHeight);
		
		//set location
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (screen.width / 2) - (windowWidth / 2);
		int centerY = (screen.height / 2) - (windowHeight / 2);
		setLocation(centerX, centerY);
		
		//add the contnet
		Container contentPane = this.getContentPane();
		MyPanel panel = new MyPanel();
		contentPane.add(panel);
	}
	
	@Override
	public void promptUser() {
		// TODO Auto-generated method stub
		this.setVisible(true);
	}

	@Override
	public String getUserInput() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		MyGui g = new MyGui();
		g.promptUser();
	}
}

class MyPanel extends JPanel implements ActionListener {
	
	private static final String createUserString = "Create New User";
	
	private JButton createUsrBtn;
	
	public MyPanel() {
		createUsrBtn = new JButton(createUserString);
		add(createUsrBtn);
		createUsrBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals(createUserString)) {
			
		}
	}
}
