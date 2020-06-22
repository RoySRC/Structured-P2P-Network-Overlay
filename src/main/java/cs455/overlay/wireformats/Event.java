package cs455.overlay.wireformats;

import java.io.IOException;

public interface Event {

	/**
	 * Gets the type of the protocol
	 * @return integer representing the protocol
	 */
	public int getType();
	
	/**
	 * Gets the marshalled byte array associated with the protocol
	 * @return Marshalled byte array of the protocol
	 * @throws IOException 
	 */
	public byte[] getBytes() throws IOException;
	
	/**
	 * Print event
	 */
	public void print();
	
}
