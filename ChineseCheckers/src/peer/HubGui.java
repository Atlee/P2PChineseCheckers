package peer;

/*import game.Game;
import game.Interaction;
import game.NetworkLayer;
import game.Player;*/

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utils.SignUtils;
 

/* ListDemo.java requires no other files. */
@SuppressWarnings("serial")
public class HubGui extends JPanel
                      implements ListSelectionListener {
    private JList<String> list;
    private DefaultListModel<String> listModel;
 
    private static final String HOST_STRING = "Host";
    private static final String JOIN_STRING = "Join";
	private static final String LOGOUT_STRING = "Logout";
    private static final String REFRESH_STRING = "Refresh";
    
    private final String username;
    private final int secret;
    private KeyPair signKeys;
    private JFrame frame;
    
    private HashMap<String, Integer> gameNameMap = new HashMap<String, Integer>();
    
    private JButton joinButton;
    private JButton refreshButton;
    private JButton logoutButton;
 
    public HubGui(JFrame window, String username, int secret) {
        super(new BorderLayout());
        this.username = username;
        this.secret   = secret;
        this.signKeys  = SignUtils.newSignKeyPair();
        this.frame = window;
        
        //get the list of hsots from the server and update
        //listModel to represent that list
        listModel = new DefaultListModel<String>();
        updateHostList();
        
        int size = listModel.getSize();
        
        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        JButton hostButton = new JButton(HOST_STRING);
        HostListener hostListener = new HostListener(hostButton);
        hostButton.setActionCommand(HOST_STRING);
        hostButton.addActionListener(hostListener);
        
        refreshButton = new JButton(REFRESH_STRING);
        refreshButton.setActionCommand(REFRESH_STRING);
        refreshButton.addActionListener(new RefreshListener());
        refreshButton.setSize(10, 10);
 
        joinButton = new JButton(JOIN_STRING);
        joinButton.setActionCommand(JOIN_STRING);
        joinButton.addActionListener(new JoinListener());
        if (size == 0) {
        	joinButton.setEnabled(false);
        }
        
        logoutButton = new JButton(LOGOUT_STRING);
        logoutButton.setActionCommand(LOGOUT_STRING);
        logoutButton.addActionListener(new LogoutListener());
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(refreshButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(joinButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(hostButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(logoutButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    private boolean updateHostList() {
    	boolean output = false;
    	try {
    		gameNameMap.clear();
    		Map<Integer, String> games = HubGuiProtocols.getGameList(username, secret);
    		
    		if (games != null) {
	    		listModel.clear(); //clear the current list
	    		for (Integer id : games.keySet()) {
	    			String gameName = games.get(id);
	    			listModel.addElement(gameName);
	    			gameNameMap.put(gameName, id);
	    		}
	    		output = true;
    		} else {
    			output = false;
    		}
    	} catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
    		e.printStackTrace();
    		Peer.displayWindow("Communication Error", "Error Getting Game List from Hub");
    	}
    	return output;
    }
    
    private void showJoinGameGui(Integer gameID) {
    	frame.getContentPane().removeAll();
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	
    	JComponent newContentPane = new JoinGameGui(frame, gameID, signKeys, username, secret);
    	newContentPane.setOpaque(true);
    	frame.setContentPane(newContentPane);
    	
    	frame.pack();
    	frame.setVisible(true);
    }
    
    class RefreshListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		if (updateHostList()) {
    			;
    		} else {
    			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
    			frame.setVisible(false);
    			frame.dispose();
    			Peer.displayWindow("Session Expired", "Session Key no longer valid");
    			new Peer();
    		}
    	}
    }
 
    class JoinListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            String gameName = list.getSelectedValue();
            
            try {
            	System.out.println("entering join");
				boolean success = HubGuiProtocols.joinGame(gameNameMap.get(gameName), signKeys.getPublic(), username, secret);
				
				if (success) {
					showJoinGameGui(gameNameMap.get(gameName));
				} else {
	    			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
	    			frame.setVisible(false);
	    			frame.dispose();
	    			Peer.displayWindow("Session Expired", "Session Key no longer valid");
	    			new Peer();
				}
			} catch (IOException | GeneralSecurityException | ClassNotFoundException e1) {
				Peer.displayWindow("Join Failed", "Error Connecting to Hub");
			}           
        }
        
       /* private Game createBasicGame(Socket peer, Key gameKey, String hostname, String username) throws Exception {
        	Player host = new Player(hostname, 0);
        	Player me = new Player(username, 1);
        	ArrayList<Player> l = new ArrayList<Player>();
        	l.add(host); l.add(me);
        	Interaction i = new NetworkLayer(peer, gameKey);
        	return new Game(l, me, i);
        } */
    }
    
    class LogoutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	try {
        		HubGuiProtocols.logout(username, secret);
        	} catch(IOException | GeneralSecurityException ex) {
        		;
        	}
        	//get the window
        	JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
        	frame.dispose();
        	new Peer();
        }
    }
    
    class CreateListener implements ActionListener {
    	JTextField newGameName;
    	ButtonGroup group;
    	
    	CreateListener(JTextField text, ButtonGroup bg) {
    		newGameName = text;
    		group = bg;
    	}
    	
        public void actionPerformed(ActionEvent e) {
        	String gameName = newGameName.getText();
        	ButtonModel b  = group.getSelection();
        	JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
        	frame.setVisible(false);
            try {
            	signKeys = SignUtils.newSignKeyPair();
	            Integer gameID = HubGuiProtocols.hostNewGame(gameName,Integer.parseInt(b.getActionCommand()), 
	            		signKeys.getPublic(), username, secret);
	            if (gameID != null) {
	            	showJoinGameGui(gameID);
	            } else {
         			JFrame frame2 = (JFrame) SwingUtilities.getWindowAncestor(((JButton) e.getSource()));
         			frame2.setVisible(false);
         			frame2.dispose();
         			Peer.displayWindow("Session Expired", "Session Key no longer valid");
         			new Peer();
         		}
            } catch (IOException | NumberFormatException | ClassNotFoundException | GeneralSecurityException ex) {
            	Peer.displayWindow("Host Failed", "Error Connecting to Hub");
            }
            frame.dispose();
        }
    }
 
    //This listener is shared by the text field and the hire button.
    class HostListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;
 
        public HostListener(JButton button) {
            this.button = button;
        }
 
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
        	//create new window for selecting game options
        	JFrame options = new JFrame("Options");
        	JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        	
        	JTextField newGameName = new JTextField("Game Name");
        	
        	JRadioButton b2 = new JRadioButton("2", true); b2.setActionCommand("2");
        	JRadioButton b3 = new JRadioButton("3", false);b3.setActionCommand("3");
        	JRadioButton b4 = new JRadioButton("4", false);b4.setActionCommand("4");
        	JRadioButton b5 = new JRadioButton("5", false);b5.setActionCommand("5");
        	JRadioButton b6 = new JRadioButton("6", false);b6.setActionCommand("6");
        	ButtonGroup bg = new ButtonGroup();
        	bg.add(b2);bg.add(b3);bg.add(b4);bg.add(b5);bg.add(b6);
        	
        	JButton create = new JButton("Create");
        	create.setActionCommand("Create");
        	create.addActionListener(new CreateListener(newGameName, bg));
        	
        	panel.add(newGameName);
        	panel.add(create);
        	panel.add(b2);panel.add(b3);
        	panel.add(b5);panel.add(b4);
        	panel.add(b6);
        	
        	options.getContentPane().add(panel);
        	
        	options.pack();
        	panel.setVisible(true);        
        	options.getContentPane().setVisible(true);
        	options.setVisible(true);
        }
        
        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }
 
        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }
 
        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }
 
        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }
 
        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }
 
        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }
 
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
 
            if (list.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                joinButton.setEnabled(false);
 
            } else {
            //Selection, enable the fire button.
                joinButton.setEnabled(true);
            }
        }
    }
}

/*class GameStart implements Runnable {
	
	private Key gameKey;
	private String hostname;
	
	public GameStart(Key gameKey, String hostname) {
		this.gameKey = gameKey;
		this.hostname = hostname;
	}

	@Override
	public void run() {
		ServerSocket host = null;
    	try {
    		host = new ServerSocket(Constants.CLIENT_HOST_PORT);
    	} catch (IOException e) {
    		System.out.println("Could not listen on port " + Constants.CLIENT_HOST_PORT);
    		e.printStackTrace();
    		System.exit(1);
    	}
    	
    	Socket peer = displayWaitingWindow(host);
    	
    	String peername = null;
    	try {
    		peername = new String(NetworkUtils.readEncryptedMessage(peer, gameKey, Constants.SHARED_ENCRYPT_ALG));
    	} catch (IOException ex) {
    		ex.printStackTrace();
    		System.exit(1);
    	}
    	
    	try {
        	Game g = createBasicGame(peer, hostname, peername);
        } catch (Exception ex) {
        	ex.printStackTrace();
        	System.exit(1);
        }
	}
	
    private Socket displayWaitingWindow(ServerSocket hostSocket) {
    	JFrame frame = new JFrame("Waiting for opponent");
		JLabel label = new JLabel("Waiting for opponent to join", SwingConstants.CENTER);
		
		//show success/failure window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(label, BorderLayout.CENTER);			
		frame.setSize(300, 100);
		frame.setVisible(true);
		
		Socket peer = null;
		try {
			peer = hostSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		frame.dispose();
		return peer;
    }
    
    private Game createBasicGame(Socket s, String hostname, String peerName) throws Exception {
    	Player host = new Player(hostname, 0);
    	Player peer = new Player(peerName, 1);
    	ArrayList<Player> l = new ArrayList<Player>();
    	l.add(host); l.add(peer);
    	Interaction i = new NetworkLayer(s, gameKey);
    	return new Game(l, host, i);
    }	
}*/