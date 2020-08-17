package cs455.overlay.wireformats;

import java.io.*;
import java.util.*;

/**
 * singleton class
 * @author sajeeb
 */
public class EventFactory {

	/**
	 * singleton instance
	 */
	private static final EventFactory instance = new EventFactory();

	private HashMap<Integer, Protocol> eventProtocolMap = new HashMap<>() {{
		for (Protocol protocol : Protocol.values()) {
			put(protocol.getType(), protocol);
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
		if (event < Integer.MAX_VALUE) {

		}
//		switch (getType(marshalledBytes)) {
//		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
//			return new OverlayNodeSendsRegistration(marshalledBytes);
//
//		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
//			return new RegistryReportsRegistrationStatus(marshalledBytes);
//
//		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
//			return new OverlayNodeSendsDeregistration(marshalledBytes);
//
//		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
//			return new RegistryReportsDeregistrationStatus(marshalledBytes);
//
//		case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
//			return new RegistrySendsNodeManifest(marshalledBytes);
//
//		case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
//			return new NodeReportsOverlaySetupStatus(marshalledBytes);
//
//		case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
//			return new RegistryRequestsTaskInitiate(marshalledBytes);
//
//		case Protocol.OVERLAY_NODE_SENDS_DATA:
//			return new OverlayNodeSendsData(marshalledBytes);
//
//		case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
//			return new OverlayNodeReportsTaskFinished(marshalledBytes);
//
//		case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
//			return new RegistryRequestsTrafficSummary(marshalledBytes);
//
//		case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
//			return new OverlayNodeReportsTrafficSummary(marshalledBytes);
//
//		default:
//			throw new IllegalStateException("Event could not be created, unknown event type.");
//		}
	
	}
	
}
