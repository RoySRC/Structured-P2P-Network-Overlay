package cs455.overlay.node;

import java.io.*;
import java.net.*;
import java.util.*;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.LOGGER;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.util.Util;
import cs455.overlay.wireformats.*;

public class Peer implements Node {

	// for logging
	private static final LOGGER log = new LOGGER(Peer.class.getSimpleName());

	private String registry_host = null;
	private int registry_port;
	private TCPSenderQueue senderQueue = null;

	private StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();
	private StatisticsCollectorAndDisplay oldStats = new StatisticsCollectorAndDisplay();

	/**
	 * store the TCP connections of the node IDs in the routing table
	 */
	private TCPConnectionsCache connectionsCache;
	
	/**
	 * messaging node server for incoming messages
	 */
	private TCPServerThread server = null;
	
	/**
	 * ID of the current messaging node
	 */
	private int nodeID = -1;
	
	/**
	 * IDs of all nodes in the system
	 */
	private ArrayList<Integer> nodeIDs = null;
	
	/**
	 * This is the routing table received from the registry
	 */
	private RoutingTable routing_table = null;

	/**
	 * Connection to the Registry
	 */
	private TCPConnection tcp_connection_registry = null;

	/**
	 * Constructor
	 * @param host
	 * @param port
	 */
	public Peer(String host, int port) {
	  log.info("Created peer node.");

		registry_host = host;
		registry_port = port;
		connectionsCache = new TCPConnectionsCache();
		senderQueue = new TCPSenderQueue();
	}

	/**
	 *
	 */
	public void startSenderQueue() {
		// start the sender queue in a different thread
		Thread senderQueueThread = new Thread(this.senderQueue);
		senderQueueThread.start();
	}

	/**
	 * This is a client connecting to the registry
	 * @throws IOException
	 */
	public void connectToRegistry() throws IOException {
		Socket socket = new Socket(registry_host, registry_port);
		tcp_connection_registry = new TCPConnection(socket, this);
		tcp_connection_registry.startTCPSender();
		tcp_connection_registry.startTCPReceiver();
	}

	/**
	 * Once a messaging node is connected to the registry, calling the
	 * following function sends an overlay registration request to the
	 * registry.
	 */
	public void sendNodeRegistrationRequest() throws InterruptedException, IOException {
		// Generate registration request
		OverlayNodeSendsRegistration p = new OverlayNodeSendsRegistration();
		p.ip_address = tcp_connection_registry.getLocalIP();
		p.port = server.getLocalPort();

		log.info("Local Port: "+tcp_connection_registry.getLocalPort());
		log.info("Local IP: "+p.ip_address);
		log.info("Server PORT: "+p.port);

		// Queue the registration request to be sent
		tcp_connection_registry.send(p.getBytes());
//		this.senderQueue.put(tcp_connection_registry, p);
	}
	
	@Override
	public void startServerSocket(int port_num) throws IOException {
		server = new TCPServerThread( port_num, this );
		Thread server_thread = new Thread( server );
		server_thread.start();
	}

	/**
	 * When the registry receives the registration request, it sends back the
	 *
	 * @param e
	 */
	public void RegistryReportsRegistrationStatusHandler(Event e) {
		log.info("Received REGISTRY_REPORTS_REGISTRATION_STATUS message");
		RegistryReportsRegistrationStatus p = (RegistryReportsRegistrationStatus) e;
		nodeID = p.successStatus;
		System.out.println(p.informationString);
		log.info("Node ID: "+nodeID);
		log.info("Registration Message: "+log.PURPLE(p.informationString));
	}

	/**
	 * The following function generates and sends out messages to the nodes in
	 * the routing table.
	 * @param e
	 * @throws IOException
	 */
	public void generateAndSendMessages(Event e) throws IOException, InterruptedException {
		log.info("Node ID: "+this.nodeID);
		RegistryRequestsTaskInitiate event = (RegistryRequestsTaskInitiate) e;

		// Send messages
		for (int i=0; i<event.num_packets; ++i) {
			int message = Util.randInt();

			int destinationNode = Util.randChoice(this.nodeIDs);
			while (destinationNode == this.nodeID && !connectionsCache.contains(destinationNode))
				destinationNode = Util.randChoice(this.nodeIDs);

			OverlayNodeSendsData dataEvent = new OverlayNodeSendsData();
			dataEvent.destinationID = destinationNode;
			dataEvent.sourceID = this.nodeID;
			dataEvent.payload = message;
			//dataEvent.insertInPacketTrace(this.nodeID); // TODO: should I insert the source ID in the packet trace

			int nextNode = Util.getNextNode(routing_table, destinationNode);
			if (!connectionsCache.contains(nextNode)) {
				log.error("connectionsCache does not contain key: "+nextNode);
			}

			log.info(String.format("Generated message with payload: %d for node %d", message, destinationNode));
			log.info("Sending generated message for node "+destinationNode+" to node "+nextNode);
			this.senderQueue.put(connectionsCache.getTCPConnection(nextNode), dataEvent);

			// collect statistics
			stats.incrementSendTracker();
			stats.incrementSendSummation(message);
		}
	}

	public void informRegistryOfTaskCompletion() throws IOException {
		log.info("Informing registry of task completion");
		OverlayNodeReportsTaskFinished event = new OverlayNodeReportsTaskFinished();
		event.ip_address = tcp_connection_registry.getLocalIP();
		event.port = server.getLocalPort();
		event.nodeID = this.nodeID;

		tcp_connection_registry.send(event.getBytes());
		log.info("Informed registry of task completion.");
	}

	/**
	 * TODO: Should I enter the destination node ID into the packet trace
	 * @param e
	 * @throws IOException
	 */
	public void OverlayNodeSendsDataHandler(Event e) throws IOException, InterruptedException {
		OverlayNodeSendsData event = (OverlayNodeSendsData) e;
		if (event.destinationID == this.nodeID) {
			log.info("Received message with payload: "+event.payload+" from node "+event.sourceID);

			// collect statistics
			stats.incrementReceiveTracker();
			stats.incrementReceiveSummation(event.payload);

		} else {
			log.info("Relaying message for node "+event.destinationID+" from node "+event.sourceID);
			event.insertInPacketTrace(this.nodeID);
			int nextNode = Util.getNextNode(routing_table, event.destinationID);

			if (!connectionsCache.contains(nextNode)) {
				log.warning("connectionsCache does not contain key: "+nextNode);
			}

			log.info(
					String.format("Sending relayed message for %d from %d to %d", event.destinationID, event.sourceID, nextNode)
			);
			this.senderQueue.put(connectionsCache.getTCPConnection(nextNode), event);

			// collect statistics
			stats.incrementRelayTracker();
		}
	}

	/**
	 *
	 * @param e
	 */
	public void RegistryRequestsTrafficSummaryHandler(Event e) throws IOException {
		log.info("Registry has requested for traffic summary.");
		OverlayNodeReportsTrafficSummary event = new OverlayNodeReportsTrafficSummary();

		event.nodeID = this.nodeID;
		event.num_sent_packets = this.stats.getSendTracker();
		event.num_relayed_packets = this.stats.getRelayTracker();
		event.sent_packet_payload_sum = this.stats.getSendSummation();
		event.num_received_packets = this.stats.getReceiveTracker();
		event.received_packet_payload_sum = this.stats.getReceiveSummation();

		tcp_connection_registry.send(event.getBytes());
		log.info(log.ANSI_RED+"Sent traffic summary to the registry."+log.ANSI_RESET);

		log.info("Reset counters.");
		oldStats.copy(stats);
		stats.resetCounters();
	}


	public void RegistryReportsDeregistrationStatusHandler(Event e) throws IOException {
	  RegistryReportsDeregistrationStatus q = (RegistryReportsDeregistrationStatus) e;
	  log.info("Received REGISTRY_REPORTS_DEREGISTRATION_STATUS message.");

	  if (q.successStatus == nodeID) {
	    System.out.println(q.informationString);

			log.info("Killing Connection to registry");

	    // kill all the TCPConnection threads
			tcp_connection_registry.kill();
			System.exit(-1);
    }
  }


	/**
	 * The following function extracts the routing table, and the IDs of
	 * all nodes in the system.
	 * @param e
	 */
	public void RegistrySendsNodeManifestHandler(Event e) {
		RegistrySendsNodeManifest event = (RegistrySendsNodeManifest) e;

		if (event.routing_table == null)
			log.error("RegistrySendsNodeManifest received null routing table");

		if (event.node_IDs == null)
			log.error("RegistrySendsNodeManifest received null nodeIDs");

		this.routing_table = event.routing_table;
		this.nodeIDs = event.node_IDs;
	}

	/**
	 *
	 */
	public void connectToPeers() throws InterruptedException {
		String informationString = "Failed to connect to nodes: ";
		ArrayList<Integer> failedNodes = new ArrayList<>();
		boolean failed = false;
		int successStatus = this.nodeID;
		for (int i=0; i < routing_table.length(); ++i) {
			RoutingEntry entry = routing_table.entry[i];
			try {
				Socket socket = new Socket(entry.IP, entry.port);
				TCPConnection connection = new TCPConnection(socket, this);
				connection.startTCPSender();
				connectionsCache.assign(entry.nodeID, connection);

			} catch (IOException e) {
				failed = true;
				failedNodes.add(entry.nodeID); // add the current node to the list of failed nodes
				successStatus = -1;
			}
		}

		// turn the elements into comma separated array string
		for (int i=0; i<failedNodes.size(); ++i) {
			informationString += failedNodes.get(i);
			if (i+1 < failedNodes.size())
				informationString += ", ";
		}

		// once connecting to other peers are done, send registry node setup status
		reportOverlaySetupStatus(successStatus, (!failed) ? "Successfully connected to all nodes" : informationString);
	}

	/**
	 * 
	 */
	private void reportOverlaySetupStatus(int successStatus, String informationString) {
		NodeReportsOverlaySetupStatus e = new NodeReportsOverlaySetupStatus();
		e.successStatus = successStatus;
		e.informationString = informationString;
		
		try {
			tcp_connection_registry.send(e.getBytes());
		} catch (Exception e1) {
			log.info("Cannot send overlay setup status to registry.");
			log.printStackTrace(e1);
		}
	}
	
	@Override
	public void onEvent(Event e, String IpPort) throws Exception {
		switch (e.getType()) {
		
		case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
			RegistryReportsRegistrationStatusHandler(e);
			break;
		
		case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
			RegistrySendsNodeManifestHandler(e);
			connectToPeers();
			break;
			
		case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
			generateAndSendMessages(e);
			informRegistryOfTaskCompletion();
			break;

		case Protocol.OVERLAY_NODE_SENDS_DATA:
			OverlayNodeSendsDataHandler(e);
			break;

		case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
			RegistryRequestsTrafficSummaryHandler(e);
			break;

    case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
      RegistryReportsDeregistrationStatusHandler(e);
      break;

		default:
			log.warning("Unrecognized event: "+e.toString());
		}
	}

	@Override
	public void parseArguments() throws IOException {
		Scanner stdin = new Scanner(System.in);
		while (stdin.hasNext()) {
			switch (stdin.nextLine()) {
				case "print-counters-and-diagnostics":
					oldStats.print();
					break;

				case "exit-overlay":
					log.info("Exit Overlay");
					// if the node is connected to other messaging nodes
					if (connectionsCache.size() > 0) { // TODO: is this safe to do, should I be doing this?
						System.out.print("Cannot exit overlay ove setup.");
						break;
					}
					OverlayNodeSendsDeregistration deregistrationEvent = new OverlayNodeSendsDeregistration();
					deregistrationEvent.ip_address = tcp_connection_registry.getLocalIP();
					deregistrationEvent.port = this.server.getLocalPort();
					deregistrationEvent.nodeID = this.nodeID;
					tcp_connection_registry.send(deregistrationEvent.getBytes());
					log.info("Sent OVERLAY_NODE_SENDS_DEREGISTRATION message to registry.");
					break;

				case "print-routing-table":
					if (routing_table == null) {
						System.out.print("Node has not received routing table from registry.");
						break;
					}
					routing_table.print();
					break;

				case "register":
					try {
						sendNodeRegistrationRequest();
					} catch (Exception e) {}
					break;

				case "enable-logger":
					log.log_status = true;
					break;

				case "disable-logging":
					log.log_status = false;

				default:
			}
		}
		stdin.close();
	}

	@Override
	public void tcpReceiverErrorHandler(TCPConnection connection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tcpSenderErrorHandler(TCPConnection connection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTCPConnection(TCPConnection connection) throws IOException {
		connection.startTCPReceiver();
	}

}
