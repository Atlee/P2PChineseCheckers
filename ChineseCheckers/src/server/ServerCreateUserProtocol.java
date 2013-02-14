package server;

import java.util.Scanner;
import server.FakeDatabase;

public class ServerCreateUserProtocol extends ServerProtocol {

	@Override
	public String processInput(String s, FakeDatabase db) {
		String message;
		if (s == null) {
			message = "";
		} else if (s.equals("create")) {
			message = "username:password";
		} else {
			Scanner scn = new Scanner(s);
			String op;
			String username = null;
			String password = null;
			message = "Retry";
			scn.useDelimiter("\t");
			op = scn.next();
			if (op.equals("username")) {
				username = scn.next();
				op = scn.next();
				if (op.equals("password")) {
					password = scn.next();
					db.setValue(username, password);
				}
				message = "End";
			}
			System.out.println(username + ":" + password);
			scn.close();
		}
		return message;
	}
}
