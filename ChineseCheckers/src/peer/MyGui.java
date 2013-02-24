package peer;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MyGui extends UI implements ActionListener {
	
	private static final String createUsrString = "Create New User";
	Button createUsrBtn;
	String userAction = null;

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
		
		createUsrBtn = new Button(createUsrString);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals(createUsrString)) {
			//Create new window for loggin
			
		}
		
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
			this.
		}
	}
}
