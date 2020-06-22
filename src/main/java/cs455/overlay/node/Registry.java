package cs455.overlay.node;

public class Registry {
	
	public static void main(String args[]) throws Exception {		
		int port_num = Integer.parseInt(args[0]);
		RegistryNode registry = new RegistryNode();
		

		registry.startServerSocket(port_num);	// start the server on a separate thread		
		registry.parseArguments();
		
	}
	
}
