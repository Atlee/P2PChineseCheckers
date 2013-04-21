package peer;

import game.Game;
import game.Interaction;
import game.NetworkLayer;
import game.Player;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Tuple;
 
/* ListDemo.java requires no other files. */
public class HubGui extends JPanel
                      implements ListSelectionListener {
    private JList<String> list;
    private DefaultListModel<String> listModel;
 
    private static final String HOST_STRING = "Host";
    private static final String JOIN_STRING = "Join";
    private static final String REFRESH_STRING = "Refresh";
    private static final String TEXT_FIELD_DEFAULT = "New Game Name";
    private JButton joinButton;
    private JButton refreshButton;
    private JTextField newGameName;
    private Key sharedKey;
 
    public HubGui(Key sharedKey) {
        super(new BorderLayout());
        this.sharedKey = sharedKey;
        
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
 
        newGameName = new JTextField(TEXT_FIELD_DEFAULT, 10);
        newGameName.addActionListener(hostListener);
        newGameName.getDocument().addDocumentListener(hostListener);
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(refreshButton);
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(joinButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        //buttonPane.add(newGameName);
        buttonPane.add(hostButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    private void updateHostList() {
    	List<String> hosts = null;
    	try {
    		hosts = HubGuiProtocols.getHostList(sharedKey);
    	} catch (IOException e) {
    		System.out.println("Error getting host list from server");
    		e.printStackTrace();
    		System.exit(1);
    	}
    	
    	listModel.clear();
        
        //add all the hosts to the listmodel display
        for (String hostname : hosts) {
        	listModel.addElement(hostname);
        }
    }
    
    class RefreshListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		updateHostList();
    	}
    }
 
    class JoinListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            String hostname = list.getSelectedValue();
            
            try {
				Tuple<InetAddress, Key> pair = HubGuiProtocols.joinGame(hostname, sharedKey);
				Socket s = new Socket(pair.first(), Constants.CLIENT_HOST_PORT);
				//Chat c = new Chat(s);
				//test
				try {
					Game g = createBasicGame(s, pair.second(), hostname, "local");
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(0);
				}
				//c.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("unable to get address from hub");
				e1.printStackTrace();
				System.exit(1);
			}
        }
        
        private Game createBasicGame(Socket s, Key gameKey, String hostname, String username) throws Exception {
        	Player host = new Player(hostname, 0);
        	Player me = new Player(username, 1);
        	ArrayList<Player> l = new ArrayList<Player>();
        	l.add(host); l.add(me);
        	Interaction i = new NetworkLayer(s, gameKey);
        	return new Game(l, me, i);
        }
    }
 
    //This listener is shared by the text field and the hire button.
    class HostListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;
        private ServerSocket hostSocket = null;
 
        public HostListener(JButton button) {
            this.button = button;
        }
 
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
            String name = newGameName.getText();
 
            //User didn't type in a unique name...
            /*if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                newGameName.requestFocusInWindow();
                newGameName.selectAll();
                return;
            }*/
            
            Key gameKey = HubGuiProtocols.hostNewGame(sharedKey);
            updateHostList();
            (new Thread(new GameStart(gameKey))).start();
            //test
            //Chat c = new Chat(peer);
            //c.start();
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

    public static void createAndShowGUI(Key sharedKey) {
        //Create and set up the window.
        JFrame frame = new JFrame("ListDemo");
        frame.addWindowListener(new CloseListener(sharedKey));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        System.out.println("Creating HubGUI");
        JComponent newContentPane = new HubGui(sharedKey);
        System.out.println("Created HubGUI");
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    } 
/*    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }*/
}

class GameStart implements Runnable {
	
	private Key gameKey;
	
	public GameStart(Key gameKey) {
		this.gameKey = gameKey;
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
    	
    	try {
        	Game g = createBasicGame(peer, "host", "peer");
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
			// TODO Auto-generated catch block
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

	
}

class CloseListener extends WindowAdapter {
	
	Key sharedKey;
	
	public CloseListener(Key sharedKey) {
		this.sharedKey = sharedKey;
	}
	
	public void windowClosing(WindowEvent e) {
		System.out.println("Sending logout");
		HubGuiProtocols.logout(sharedKey);
		Frame frame = (Frame) e.getSource();
		frame.dispose();
	}
}