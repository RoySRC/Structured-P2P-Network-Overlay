package cs455.overlay.node;

public class MessagingNode {

	public static void main(String args[]) throws Exception {
		Peer messaging_node = new Peer(args[0], Integer.parseInt(args[1]));		

		// start sender queue on a separate thread
		messaging_node.startSenderQueue();

		// start server on separate thread
		messaging_node.startServerSocket(0);	
		
		// connect to the registry
		messaging_node.connectToRegistry();
		
		// send node registration request to registry
		messaging_node.sendNodeRegistrationRequest();
		
		messaging_node.parseArguments();
	}
	
}
