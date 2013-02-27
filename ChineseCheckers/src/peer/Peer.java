package peer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.KeyStore;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import utils.Constants;
import utils.MyKeyStore;
import utils.Protocol;


public class Peer  {  

	private static InetAddress host;
	private static MyKeyStore ks;
	
	public static void main(String[] args) throws IOException {		
		new Peer();
	}
    
    public Peer() {
    	ks = new MyKeyStore();
    	displayLoginGui();
    }
    
    private void displayLoginGui() {
    	JFrame mainFrame = new JFrame("Chinese Checkers");
        mainFrame.setSize(385,200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JInternalFrame loginFrame = new JInternalFrame("Login");
        loginFrame.setSize(400,200);
        
        JPanel loginPanel = new JPanel();
        
        JTextField usernameTxt = new JTextField(25);
        JPasswordField passwordTxt = new JPasswordField(25);
        JPanel buttonPanel = new JPanel();
        JLabel usernameLbl = new JLabel("Username: ");
        JLabel passwordLbl = new JLabel("Password: ");
        
        JButton loginButton = new JButton("Login");
        JButton newUserButton = new JButton("New User");
        loginButton.addActionListener(new loginButtonListener());
        newUserButton.addActionListener(new newUserButtonListener());

        loginPanel.add(usernameLbl);
        loginPanel.add(usernameTxt);
        loginPanel.add(passwordLbl);
        loginPanel.add(passwordTxt);
        loginPanel.add(buttonPanel);
        loginPanel.add(loginButton);
        loginPanel.add(newUserButton);
        
        loginFrame.getContentPane().add(BorderLayout.CENTER,loginPanel);
        mainFrame.getContentPane().add(BorderLayout.CENTER,loginFrame);
  
        loginFrame.setVisible(true);
        mainFrame.setVisible(true);
    }
	
	private static Socket handleCreateSocket() {
		Socket s = null;		
		try {
			host = InetAddress.getLocalHost();
			s = new Socket(host, Constants.PORT_NUM);
		} catch (IOException e) {
			System.out.println("Error creating socket");
			e.printStackTrace();
			System.exit(1);
		}
		return s;
	}
    
    class loginButtonListener implements ActionListener {  
        public void actionPerformed(ActionEvent e) {  
            //if username and password is good hide child window  
            System.out.println("YOU CLICKED LOGIN");

        }
    }
    
    class newUserButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		//open a new gui for account creation
    		System.out.println("YOU CLICKED NEW USER");
            new UserRegistrationProtocol(ks);
    	}
    }
} 
