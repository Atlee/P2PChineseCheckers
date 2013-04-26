package peer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

import utils.Constants;
import utils.EncryptUtils;
import utils.Protocol;
import utils.NetworkUtils;


public class UserRegistrationProtocol extends Protocol {
	
	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	
	public UserRegistrationProtocol() {
		displayCreateUserGui();
	}
	
	private void displayCreateUserGui() {
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
        
        JButton loginButton = new JButton("Create");
        JButton newUserButton = new JButton("Cancel");
        loginButton.addActionListener(new createButtonListener());
        newUserButton.addActionListener(new cancelButtonListener());

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
 	
    class createButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		Socket s = NetworkUtils.handleCreateSocket();
    		
    		String newUsername = usernameTxt.getText(), response;
    		
    		//if the username is invalid
    		if (!Constants.verifyUsername(newUsername)) {
    			displayFailWindow("User Registration Failed", "Invalid Username");
    			return;
    		}
    		char[] password = passwordTxt.getPassword();
    		System.out.println(new String(password));
    		if (!Constants.verifyPassword(password)) {
    			displayFailWindow("User Registration Failed", "Invalid Password");
    			return;
    		}
    		
    		Key sharedKey = EncryptUtils.handleCreateSharedKey();   		
    		
    		sendSharedKey(s, sharedKey);
    		
    		NetworkUtils.sendProtocolID(s, Constants.REGISTER);
    		
    		try {
	    		NetworkUtils.sendEncryptedMessage(s, newUsername.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
	    		NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(passwordTxt.getPassword()), sharedKey, Constants.SHARED_ENCRYPT_ALG);
    		} catch (IOException ex) {
    			System.out.println("Error sending credentails to hub");
    			ex.printStackTrace();
    			System.exit(1);
    		}
    		
    		try {
	    		response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
	    		
	    		if (response.equals(Constants.REGISTRATION_SUCCESS + newUsername)) {
					JFrame createUserFrame = (JFrame) SwingUtilities.getWindowAncestor((JButton) e.getSource());
					displaySuccessWindow(createUserFrame);
	    		} else if (response.equals(Constants.REGISTRATION_IN_USE)) {
	    			displayFailWindow("User Registration Failed", "Username already in use");
	    		} else {
	    			displayFailWindow("User Registration Failed", "Unknown response from server");
	    		}
    		} catch (IOException ex) {
    			System.out.println("Error reading registration response from server");
    			ex.printStackTrace();
    			System.exit(1);
    		}
    	}
    	
    	private void sendSharedKey(Socket s, Key sharedKey) {
    		PublicKey hubPublic = Constants.getHubPublicKey();
    		
    		try {
    			NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), hubPublic, Constants.PUBLIC_ENCRYPT_ALG);
    		} catch (IOException e) {
    			e.printStackTrace();
    			System.exit(1);
    		}
    		System.out.println("Sending encrypted shared key");
    	}
    }
    
    private static void displaySuccessWindow(JFrame mainFrame) {
    	//create a success window
		JFrame frame = new JFrame("Create User Success");
		JLabel label = new JLabel("Username sucessfully registered.", SwingConstants.CENTER);
		
		//dispose of the create user window
		mainFrame.setVisible(false);
		mainFrame.dispose();
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    private static void displayFailWindow(String title, String labelString) {
    	JFrame frame = new JFrame(title);
		JLabel label = new JLabel(labelString, SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    class cancelButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		
    		JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor((JButton) e.getSource());
    		mainFrame.setVisible(false);
    		mainFrame.dispose();
    	}
    }
 	
}
