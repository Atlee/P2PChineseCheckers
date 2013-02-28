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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import hub.HubCertificate;

import utils.Constants;
import utils.MyKeyStore;
import utils.Protocol;
import utils.SignUtils;
import utils.NetworkUtils;


public class UserRegistrationProtocol extends Protocol {
	
	private MyKeyStore ks;
	
	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	
	public UserRegistrationProtocol(MyKeyStore ks) {
		this.ks = ks;
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
 	
 	/*------------GUI CODE-------------------*/
 	
    class createButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		Socket s = NetworkUtils.handleCreateSocket();
    		NetworkUtils.sendProtocolID(s, Constants.REGISTER);
    		
    		String newUsername = usernameTxt.getText(), response;
    		NetworkUtils.sendMessage(s, newUsername.getBytes());
    		
    		response = new String(NetworkUtils.readSignedMessage(s, MyKeyStore.getHubPublicKey()));
    		System.out.println(response);
    		
    		if (response.equals("AVAILABLE,"+ newUsername)) {
    			System.out.println("received correct response");
    			KeyPair keys = SignUtils.newSignKeyPair();
    			
    			//send public key to server with authentication message
    			NetworkUtils.sendKey(s, keys.getPublic());
    			NetworkUtils.sendSignedMessage(s, newUsername.getBytes(), keys.getPrivate());
    			
    			//HubCertificate cert = readCertificate(s);
    			if(true){ //FIXME
    				ks.addPrivateKey(keys.getPrivate(), newUsername, passwordTxt.getPassword());
    				JFrame createUserFrame = (JFrame) SwingUtilities.getWindowAncestor((JButton) e.getSource());
    				displaySuccessWindow(createUserFrame);
    			} else {
    				displayFailWindow();
    			}
    		} else if (response.equals("IN_USE,"+ newUsername)) {
    			displayFailWindow();
    		}
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
    
    private static void displayFailWindow() {
    	JFrame frame = new JFrame("Create User Failed");
		JLabel label = new JLabel("Username already in use", SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    class cancelButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		System.out.println("YOU CLICKED CANCEL");
    		
    		JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor((JButton) e.getSource());
    		mainFrame.setVisible(false);
    		mainFrame.dispose();
    	}
    }
 	
}
