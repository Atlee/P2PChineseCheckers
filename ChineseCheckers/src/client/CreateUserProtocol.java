package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CreateUserProtocol extends ClientProtocol {
	
	@Override
	public String processInput(String input) {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		String output = null;
		
		try {
			if (input.equals("Start")) {
				output = "create";
			} else if (input.equals("username:password")) {
				System.out.println("Please enter new username");
				output = "username\t" + stdin.readLine();
				
				System.out.println("Please enter new password");
				output += "\tpassword\t" + stdin.readLine();
			} else {
				output = "default";
			}
		} catch (IOException e) {
			System.out.println("Error reading user input");
		}
		
		return output;
	}

}
