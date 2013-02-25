package peer;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MyGui extends JFrame implements ActionListener {
	
	private static final String createUsrString = "Create New User";
	private Button createUsrBtn;
	private JTextField userName;
	private JPasswordField password;

	public MyGui() {
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setTitle("Chinese Checkers 0.1");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//set size
		int windowWidth = 300;
		int windowHeight = 100;
		setSize(windowWidth, windowHeight);
		
		//set location
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (screen.width / 2) - (windowWidth / 2);
		int centerY = (screen.height / 2) - (windowHeight / 2);
		setLocation(centerX, centerY);
		
		userName = new JTextField("Username");
		userName.setSize(new Dimension(100, 50));
		password = new JPasswordField("Password");
		createUsrBtn = new Button(createUsrString);

		getContentPane().add(userName);
		getContentPane().add(password);
		getContentPane().add(createUsrBtn);
		createUsrBtn.addActionListener(this);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals(createUsrString)) {
			//Create new window for login
			Peer.setProtocol("register");
			Peer.executeProtocol();
		}
		
	}
	
	public static void main(String[] args) {
		MyGui g = new MyGui();
	}
}
