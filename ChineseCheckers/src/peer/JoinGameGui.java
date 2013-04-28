package peer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import peer.HubGui.HostListener;
import peer.HubGui.JoinListener;
import peer.HubGui.LogoutListener;
import peer.HubGui.RefreshListener;
import peer.Peer.CloseListener;
import utils.SignUtils;

public class JoinGameGui extends JPanel implements ListSelectionListener {

	private JList<String> list;
    private DefaultListModel<String> listModel;
 
    private static final String READY_STRING = "Ready";
    private static final String REFRESH_STRING = "Refresh";
	private static final String LEAVE_STRING = "Leave";
    
    private final String username;
    private final int secret;
    private final Integer id;
    private final PublicKey signKey;
    private boolean ready = false;
    private JFrame frame;
    
    private JButton readyButton;
    private JButton refreshButton;
    private JButton leaveButton;
 
    public JoinGameGui(JFrame frame, Integer id, PublicKey signKey, String username, int secret) {
        super(new BorderLayout());
        this.frame = frame;
        this.username = username;
        this.secret   = secret;
        this.id = id;
        this.signKey = signKey;
        
        //get the list of hsots from the server and update
        //listModel to represent that list
        listModel = new DefaultListModel<String>();
        updatePlayerList();
        
        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        
        refreshButton = new JButton(REFRESH_STRING);
        refreshButton.setActionCommand(REFRESH_STRING);
        refreshButton.addActionListener(new RefreshListener());
        refreshButton.setSize(10, 10);
 
        readyButton = new JButton(READY_STRING);
        readyButton.setActionCommand(READY_STRING);
        readyButton.addActionListener(new ReadyListener());
        
        leaveButton = new JButton(LEAVE_STRING);
        leaveButton.setActionCommand(LEAVE_STRING);
        leaveButton.addActionListener(new LeaveListener());
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(refreshButton);
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(readyButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        //buttonPane.add(newGameName);
        buttonPane.add(leaveButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
	}
	
	private boolean updatePlayerList() {
		boolean output = false;
		try {
    		List<String> players = HubGuiProtocols.getPlayerList(id, username, secret);
    		if (players != null) {
	    		listModel.clear();
	    		for (String pname : players) {
	    			listModel.addElement(pname);
	    		}
	    		output = true;
    		}
    	} catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
    		Peer.displayWindow("Communication Error", "Error Getting Player List from Hub");
    	}
		return output;
	}
	
	synchronized void setReady(boolean t) {
		ready = t;
	}
	
	synchronized boolean stillReady() {
		return ready;
	}
	
    class RefreshListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		if (updatePlayerList()) {
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
    
    class ReadyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setReady(true);
			readyButton.setEnabled(false);
			new Thread(new ReadyThread((JFrame) SwingUtilities.getWindowAncestor(((JButton) arg0.getSource())))).start();
		}
    }
    
    class LeaveListener implements ActionListener {
    	@Override
		public void actionPerformed(ActionEvent arg0) {
			setReady(false);
			
			try {
				if (HubGuiProtocols.leaveGame(id, username, secret)) {
					frame.getContentPane().removeAll();
			    	
			    	JComponent newContentPane = new HubGui(frame, username, secret);
			    	newContentPane.setOpaque(true);
			    	frame.setContentPane(newContentPane);
			    	
			    	frame.pack();
			    	frame.setVisible(true);
				}  else {
	    			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((JButton) arg0.getSource()));
	    			frame.setVisible(false);
	    			frame.dispose();
	    			Peer.displayWindow("Session Expired", "Session Key no longer valid");
	    			new Peer();
	    		}
			} catch (ClassNotFoundException | GeneralSecurityException
					| IOException e) {
				;
			}
		}
    }
    
    class ReadyThread implements Runnable {
    	JFrame frame;
    	
    	ReadyThread(JFrame f) {
    		this.frame = f;
    	}
    	
    	@Override
    	public void run() {
    		try {
				GameInfo gi = HubGuiProtocols.ready(id, username, secret);
				if (gi != null) {
					System.out.println("Game Start!");					
				} else {
	    			frame.setVisible(false);
	    			frame.dispose();
	    			Peer.displayWindow("Session Expired", "Session Key no longer valid");
	    			new Peer();
	    		}
			} catch (ClassNotFoundException | GeneralSecurityException
					| IOException e) {
				Peer.displayWindow("Ready Error", "Error communicating with the hub");
			}
    		if (stillReady()) {
    			
    		}
    	}
    	
    }
}
