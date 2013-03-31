package peer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Protocol;


public class Peer  {  

	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	
	public static void main(String[] args) throws IOException {		
		new Peer();
	}
    
    public Peer() {
    	displayLoginGui();
    }
    
    private void displayLoginGui() {
    	JFrame mainFrame = new JFrame("Chinese Checkers");
        mainFrame.setSize(385,200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JInternalFrame loginFrame = new JInternalFrame("Login");
        loginFrame.setSize(400,200);
        
        JPanel loginPanel = new JPanel();
        
        usernameTxt = new JTextField(25);
        passwordTxt = new JPasswordField(25);
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
    
    class loginButtonListener implements ActionListener {  
        public void actionPerformed(ActionEvent e) {
            String username = usernameTxt.getText();
        	Socket s = NetworkUtils.handleCreateSocket();
            final Key sharedKey = EncryptUtils.handleCreateSharedKey();
            
            char[] password = passwordTxt.getPassword();
            UserLoginProtocol login = new UserLoginProtocol();
            login.sendCredentials(s, sharedKey, username, password);
            //eliminate the password from memory as fast as possible
            Arrays.fill(password, '_');
            try {
	            if (login.isAuthenticated(s, sharedKey)) {
	            	//dispose of the gui for logging in and display the gui for the hub
	            	JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
	            	loginFrame.setVisible(false);
	            	loginFrame.dispose();
	            	//displayHub();
	                HubGui.createAndShowGUI(sharedKey);
	            } else {
	            	displayFailWindow();
	            }
            } catch (IOException ex) {
            	System.out.println("Error getting authentication result from hub");
            	ex.printStackTrace();
            	System.exit(1);
            }
        }
    }
    
    private static void displayHub() {
    	JFrame frame = new JFrame("Chinese Checkers");
		JLabel label = new JLabel("Welcome to the hub", SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    private static void displayFailWindow() {
    	JFrame frame = new JFrame("Error");
		JLabel label = new JLabel("Login Unsuccessful", SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    class newUserButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		//open a new gui for account creation
    		System.out.println("YOU CLICKED NEW USER");
            new UserRegistrationProtocol();
    	}
    }
} 
