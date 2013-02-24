package protocols;

public class HelloWorldProtocol extends Protocol {
	
	public String processInput(String s) {
		String message;
		if (s == null) {
			message = "";
		} else {
			message = "Hello World";
		}
		return message;
	}

	
}
