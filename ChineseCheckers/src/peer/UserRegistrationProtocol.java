package peer;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;

import hub.HubCertificate;

import utils.KeyStoreUtils;
import utils.Constants;
import utils.Protocol;
import utils.SignUtils;


public class UserRegistrationProtocol extends Protocol {
	
	private KeyStore keyStore;
	
	private String username;
	private String password;
	
	public void execute(Socket s, KeyStore ks) {
		try {
			sendProtocolID(s);
			
			byte[] message;
			byte[] response;
			
			boolean usernameAvailable = false;
			
			while(!usernameAvailable) {
				getDesiredCredentials();
				message = (username).getBytes();
				sendMessage(s, message);
				response = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
				String responseStr = new String(response);
				if(responseStr.equals("AVAILABLE,"+username)){
					usernameAvailable = true;
				} else if(responseStr.equals("IN USE,"+username)) {
					System.out.println("The username you have selected is already in use. Please try again.");
				} else {
					System.out.println("lolwat");
					System.exit(1);
				}
			}
			
			KeyPair keys = SignUtils.newSignKeyPair();
			
			sendKey(s, keys.getPublic());
			message = username.getBytes();
			sendSignedMessage(s, message, keys.getPrivate());
			
			response = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
			HubCertificate cert = readCertificate(s);
			
			if(cert.hashCode() == ByteBuffer.wrap(response).getInt()){
				KeyStoreUtils.addPrivateKey(ks, keys.getPrivate(), cert, username, password);
				System.out.println("Registration successful! Welcome, "+username+".");
			} else {
				System.out.println("Registration failed. Please try again.");
			}
		} catch (IOException e) {
			System.out.println("Error reading user input");
		}
	}
	
//	public void execute(Socket s, KeyStore ks) {
//		keyStore = ks;
//		CreateUserGui gui = new CreateUserGui(this, s);
//	}
	
	void sendNewCredentials(String username, String password, Socket s) {
		setCredentials(username, password);
		
		byte[] message = username.getBytes();
		try {
			sendProtocolID(s);
			sendMessage(s, message);
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	void handleNameResponse(Socket s) throws IOException {
		byte[] fromServerBytes = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
		String fromServer = new String(fromServerBytes);
		
		if (fromServer.equals("AVAILABLE,"+username)) {
			//TODO: add key generation/recovery code here
			//for now assume keys in public/private.key files
			//NOTE: I think the above happens automatically in SignUtils.init()
			sendKey(s, KeyStoreUtils.getPublicKey(keyStore, this.username));
			sendMessage(s, username.getBytes());
			
			fromServerBytes = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
			fromServer = new String(fromServerBytes);
			if (fromServer.equals("OK,"+username)) {
				//TODO:Save password and username locally
				System.out.println("username " + username + " successfully created");
			} else {
				System.out.println("Unknown message from server");
			}			
		} else if (fromServer.equals("IN_USE,"+username)) {
			System.out.println("username " + username + " is not " +
					"available, please pick another");
		} else {
			System.out.println("Unknown message from server");
		}
	}

	private void sendProtocolID(Socket s) throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeInt(Constants.REGISTER);
	}
	
	private void sendKey(Socket s, PublicKey key) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(key);
	}
	
	private HubCertificate readCertificate(Socket s) throws IOException {
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		HubCertificate cert = null;
		try {
			cert = (HubCertificate) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Error reading certificate received from Hub");
			e.printStackTrace();
			System.exit(1);
		}
		return cert;
	}
	
 	private void getDesiredCredentials() {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			System.out.println("Desired username...");
			username = stdin.readLine();
			System.out.println("Password...");
			password = stdin.readLine();
		} catch (IOException e) {
			System.out.println("Error reading username and password");
			e.printStackTrace();
			System.exit(1);
		}
	}
 	
 	
}

class CreateUserGui extends JFrame implements ActionListener {
	
	private JTextField username;
	private JPasswordField password;
	private Button createUsrBtn;
	private static final String createUsrString = "Create User";
	private UserRegistrationProtocol p = null;
	private Socket s;

	public CreateUserGui(UserRegistrationProtocol p, Socket s) {
		this.p = p;
		this.s = s;
		
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
		
		username = new JTextField("username");
		username.setSize(new Dimension(100, 50));
		password = new JPasswordField("Password");
		createUsrBtn = new Button(createUsrString);
	
		getContentPane().add(username);
		getContentPane().add(password);
		getContentPane().add(createUsrBtn);
		createUsrBtn.addActionListener(this);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals(createUsrString)) {
			//Create new window for login
			String usernameString = username.getText();
			String passwordString = new String(password.getPassword());
			p.sendNewCredentials(usernameString, passwordString, this.s);
			try {
				p.handleNameResponse(this.s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
