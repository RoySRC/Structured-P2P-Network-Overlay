package cs455.overlay.wireformats;

import java.io.*;
import java.util.*;

/**
 * singleton class
 * @author sajeeb
 */
public final class EventFactory {

	/**
	 * singleton instance
	 */
	private static final EventFactory instance = new EventFactory();

	/**
	 *
	 */
	public HashMap<Integer, Protocol> eventProtocolMap = new HashMap<>() {{
		for (Protocol protocol : Protocol.values()) {
			put(protocol.getValue(), protocol);
		}
	}};

	/**
	 * Private Constructor for singleton object
	 */
	private EventFactory() {}

	/**
	 * Function to get the singleton instance
	 * @return the singleton instance of the event factory
	 */
	public static EventFactory getInstance() {return instance;}

	/**
	 * Extract the integer representing the event type from the marshalled byte array
	 * @param marshalledBytes byte stream representing the event type for event object reconstruction
	 * @return an integer representing the event type
	 * @throws IOException Throws IOException if the event type cannot be read
	 */
	private int getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		int type = din.readByte();	// get the message type
		baInputStream.close();
		din.close();
		return type;
	}

	/**
	 * Extract the event type from the marshalled byte array, create the event and return that event object
	 * @param marshalledBytes byte stream representing the event type for event object reconstruction
	 * @return The reconstructed event object from the marshalled byte array
	 * @throws IOException Throws IOException if the event type cannot be read
	 */
	public Event createEvent(byte[] marshalledBytes) throws IOException {	
		int event = getType(marshalledBytes);
		return eventProtocolMap.get(event).getEventObject(marshalledBytes);
	}
	
}
