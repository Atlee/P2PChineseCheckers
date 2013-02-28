package test;

import javax.swing.*;  

import java.awt.*;  
import java.awt.event.*;  
import java.util.ArrayList;
import java.util.UUID;
  
public class TestLoginGui implements Runnable {  

	Thread t;
    JFrame mainFrame;  
    JTextField usernameTxt;  
    JPasswordField passwordTxt;  
    JInternalFrame loginFrame;
      
    public static void main(String[] args){
    	TestLoginGui g = new TestLoginGui();
    }
    
    public TestLoginGui() {
    	t = new Thread(this, "TestLoginGui");
    	t.start();
    }
    
    public void run(){    	
    	ArrayList<String> buttonNames = new ArrayList<String>();
    	buttonNames.add("Login");
    	buttonNames.add("New User");
    	
    	displayGuiWithLogin(buttonNames);
    }  
    
    private ArrayList<JButton> createButtonswithListeners(ArrayList<String> buttonNames) {    	
    	ArrayList<JButton> buttons = new ArrayList<JButton>();
    	
    	for (int i = 0; i < buttonNames.size(); i++) {
    		JButton newButton = new JButton(buttonNames.get(i));
    		newButton.addActionListener(getButtonListener(buttonNames.get(i)));
    		buttons.add(newButton);
    	}
    	
    	return buttons;
    }
    
    private ActionListener getButtonListener(String name) {
    	ActionListener output = null;
    	switch(name) {
    	case "Login":
    		output = new loginButtonListener();
    		break;
    	case "New User":
    		output = new newUserButtonListener();
    		break;
    	case "Create":
    		output = new createButtonListener();
    		break;
    	case "Cancel":
    		output = new cancelButtonListener();
    		break;
    	default:
    		System.out.println("Unknown Button Name");
    		System.exit(1);
    	}
    	return output;
    }
    
    private void displayGuiWithLogin(ArrayList<String> buttonNames) {
    	JFrame mainFrame = new JFrame("Main");  
        mainFrame.setSize(400,200);  
        loginFrame = new JInternalFrame("Login");  
        loginFrame.setSize(400,200);  
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        JPanel loginPanel = new JPanel();  
        
        usernameTxt = new JTextField(25);     
        passwordTxt = new JPasswordField(25);  
        JPanel buttonPanel = new JPanel();
        JLabel usernameLbl = new JLabel("Username: ");  
        JLabel passwordLbl = new JLabel("Password: ");  
        
        ArrayList<JButton> buttons = createButtonswithListeners(buttonNames);         
        
        for (JButton b : buttons) {
        	buttonPanel.add(b);
        }
  
        loginPanel.add(usernameLbl);  
        loginPanel.add(usernameTxt);  
        loginPanel.add(passwordLbl);  
        loginPanel.add(passwordTxt);  
        loginPanel.add(buttonPanel);  
          
        loginFrame.getContentPane().add(BorderLayout.CENTER,loginPanel);  
        mainFrame.getContentPane().add(BorderLayout.CENTER,loginFrame);  
  
        loginFrame.setVisible(true);                      
        mainFrame.setVisible(true);
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
    		
    		ArrayList<String> buttonNames = new ArrayList<String>();
    		buttonNames.add("Create");
    		buttonNames.add("Cancel");
    		
    		displayGuiWithLogin(buttonNames);
    	}
    }
    
    class createButtonListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		//open a new gui for account creation
    		System.out.println("YOU CLICKED CREATE");
    		
    		//send away un/pw UserRegProtocol.execute();
    	}
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