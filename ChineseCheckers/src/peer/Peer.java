package peer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import test.MTHubTest;
import utils.Constants;


public class Peer  {  

	private JTextField usernameTxt;
	private JPasswordField passwordTxt;
	private String username;
	private HubGuiProtocols comm;
	
	public static void main(String[] args) throws IOException {		
		new Peer();
	}
    
    public Peer() {
    	displayLoginGui();
    }
    
    private void displayLoginGui() {
    	JFrame mainFrame = new JFrame("Chinese Checkers");
        mainFrame.setSize(350,200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        
        mainFrame.getContentPane().add(BorderLayout.CENTER,loginPanel);
  
        mainFrame.setVisible(true);
    }
    
    class loginButtonListener implements ActionListener {  
        public void actionPerformed(ActionEvent e) {
            username = usernameTxt.getText();            
            char[] password = passwordTxt.getPassword();
            try {
            	comm = new HubGuiProtocols(username, password);
            	if (comm.login(username, new String(password)) != -1) {
            		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
	            	
            		displayHub(frame);
            	} else {
            		displayWindow("Login Unsuccessful", "Authentication Failed");
            	}
            } catch (IOException | GeneralSecurityException | ClassNotFoundException ex) {
            	System.out.println("Exception during login");
            	displayWindow("Login Unsuccessful", "Exception during login");
			} finally {
	            //eliminate the password from memory as fast as possible
	            Arrays.fill(password, '_');
            }
        }
    }
    
    private void displayHub(JFrame frame) {
    	frame.getContentPane().removeAll();
    	
    	frame.addWindowListener(new CloseListener());
    	
    	JComponent newContentPane = new HubGui(username, comm);
    	newContentPane.setOpaque(true);
    	frame.setContentPane(newContentPane);
    	
    	frame.pack();
    	frame.setVisible(true);
    }
    
    public static void displayWindow(String title, String labelString) {
    	JFrame frame = new JFrame(title);
		JLabel label = new JLabel(labelString, SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
    }
    
    class newUserButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		String username = usernameTxt.getText();
    		
    		//if the username is invalid
    		if (!Constants.verifyUsername(username)) {
    			displayWindow("User Registration Failed", "Invalid Username");
    			return;
    		}
    		char[] password = passwordTxt.getPassword();
    		if (!Constants.verifyPassword(password)) {
    			displayWindow("User Registration Failed", "Invalid Password");
    			return;
    		}
    		
			try {
				int response = HubGuiProtocols.register(username, new String(passwordTxt.getPassword()));
				
	    		switch(response) {
	    		case 0: //success
	    			displayWindow("User Created", "User successfully registered");
	    			break;
	    		case 1: //in use
	    			displayWindow("User Registration Failed", "Username already in use");
	    			break;
	    		case 2: //generic fail
	    		default:
	    			displayWindow("User Registration Failed", "Unknown response from server");
	    		}
			} catch (IOException | GeneralSecurityException ex) {
				displayWindow("Registration Unsuccessful", "Exception during registration");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    }
    
    class CloseListener extends WindowAdapter {
    	
    	public void windowClosing(WindowEvent e) {
    		try {
				comm.logout();
			} catch (IOException ex) {
				//try to send a logout, but if it doesn't work don't do anything
				;
			}
    		Frame frame = (Frame) e.getSource();
    		frame.dispose();
    	}
    }
} 
