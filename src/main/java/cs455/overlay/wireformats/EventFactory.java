package cs455.overlay.wireformats;

import java.io.*;

/**
 * singleton class
 * @author sajeeb
 *
 */
public class EventFactory {

	private static final EventFactory instance = new EventFactory();
	
	private EventFactory() {}
	
	public static EventFactory getInstance() {return instance;}
	
	private int getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		int type = (int)din.readByte();	// get the message type
		baInputStream.close();
		din.close();
		return type;
	}
	
	public Event createEvent(byte[] marshalledBytes) throws IOException {	
		
		switch (getType(marshalledBytes)) {
		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
			return new OverlayNodeSendsRegistration(marshalledBytes);
			
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			return new RegistryReportsRegistrationStatus(marshalledBytes);
		
		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
			return new OverlayNodeSendsDeregistration(marshalledBytes);
		
		case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
			return new RegistryReportsDeregistrationStatus(marshalledBytes);
		
		case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
			return new RegistrySendsNodeManifest(marshalledBytes);
		
		case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
			return new NodeReportsOverlaySetupStatus(marshalledBytes);
			
		case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
			return new RegistryRequestsTaskInitiate(marshalledBytes);
		
		case Protocol.OVERLAY_NODE_SENDS_DATA:
			return new OverlayNodeSendsData(marshalledBytes);
		
		case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
			return new OverlayNodeReportsTaskFinished(marshalledBytes);
		
		case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
			return new RegistryRequestsTrafficSummary(marshalledBytes);
		
		case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
			return new OverlayNodeReportsTrafficSummary(marshalledBytes);
			
		default:
			System.err.println("Event could not be created.");
			return null;	
		}
	
	}
	
}
