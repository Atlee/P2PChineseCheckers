package client;

import javax.swing.*;

public abstract class UI extends JFrame{
	
	public abstract void promptUser(); 
	
	public abstract String getUserInput();
}
