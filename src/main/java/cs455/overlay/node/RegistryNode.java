package cs455.overlay.node;

import java.io.*;
import java.util.*;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.LOGGER;
import cs455.overlay.util.Util;
import cs455.overlay.wireformats.*;

public class RegistryNode implements Node{

	// for logging
	private static final LOGGER log = new LOGGER(RegistryNode.class.getSimpleName());
	
	public static int num_routing_table_entries;
	public static int num_messages;

	public long waitTime = 50;

	public TCPConnectionsCache connectionsCache = null;

	/**
	 * Pair a node ID with its IP address and server port
	 */
	private Map<Integer, Map<String, Object>> NodeAddressPortPairMap;
	
	/**
	 * Maps a node ID to its routing table
	 */
	public Map<Integer, RoutingTable> node_routing_table;

	/**
	 * Counts the number of nodes in the overlay that finished setting up the overlay.
	 * This means the number of nodes that finished connecting to the nodes in its
	 * routing table.
	 */
	private int overlayNodeSetupStatusCounter = 0;

	/**
	 * becomes true when all the nodes in the overlay have finished setting up the overlay,
	 * i.e. when all the nodes have established connection to the nodes in their routing table.
	 */
	private boolean flagOverlayNodesFinishedSetup = false;

	/**
	 * becomes true when the messaging nodes are in the process of setting up the overlay.
	 * when the messaging nodes are done setting up the overlay, the following flag is set
	 * to false;
	 */
	private boolean flagSettingUpOverlay = false;

	/**
	 * becomes true if the overlay nodes are currently routing messages, false otherwise
	 */
	private boolean fOverlayNodesRoutingMessages = false;

	// this is only incremented when a messaging node finishes sending messages
	public int overlayNodeTaskFinishTracker = 0;

	// get the traffic summary from each node and store it in a map
	public Map<Integer, OverlayNodeReportsTrafficSummary> overlayNodeReportsTrafficSummaryMap = null;

	/**
	 * Constructor
	 */
	public RegistryNode() {
		connectionsCache = new TCPConnectionsCache();
		node_routing_table = new HashMap<>();
		NodeAddressPortPairMap = new HashMap<>();
		overlayNodeReportsTrafficSummaryMap = new HashMap<>();
	}

  /**
   * Send a task initiate message to all the registered messaging nodes
   * @throws IOException
   */
	public void sendTaskInitiateMessage() throws IOException {
		RegistryRequestsTaskInitiate e = new RegistryRequestsTaskInitiate();
		e.num_packets = num_messages;
		byte[] marshalledBytes = e.getBytes();
		for (int ID : connectionsCache.getNodeIDs()) {
			connectionsCache.getTCPConnection(ID).send(marshalledBytes);
		}
	}

	/**
	 * The following function will be called after all the messaging nodes have registered
	 * and have been assigned an ID. The following function will be called when the user
   * issues the start <num-messages> on the interactive command line interpreter.
	 * @throws IOException
	 */
	public void sendNodeManifest() throws IOException {
		RegistrySendsNodeManifest nodeManifest = new RegistrySendsNodeManifest();

		//  get primitive arrays of node IDs
		nodeManifest.node_IDs = connectionsCache.getNodeIDs();
		
		// Create an initial empty routing table, one for each node
		RoutingTable[] routing_table = new RoutingTable[nodeManifest.node_IDs.size()];
		for (int i=0; i < routing_table.length; ++i)
			routing_table[i] = new RoutingTable(num_routing_table_entries);
		
		// Fill the routing table for each node
		for (int i=0; i < nodeManifest.node_IDs.size(); ++i) {
			for (int j=0; j < routing_table[i].length(); ++j) {
				int index = ( i + (1 << j) ) % nodeManifest.node_IDs.size();
				int nodeID = nodeManifest.node_IDs.get(index);
				routing_table[i].entry[j].distance = (1 << j);
				routing_table[i].entry[j].nodeID = nodeID;
				routing_table[i].entry[j].IP = (String) NodeAddressPortPairMap.get(nodeID).get("IP");
				routing_table[i].entry[j].port = (int) NodeAddressPortPairMap.get(nodeID).get("PORT");
			}
		}
		
		// Send each node their routing table, and put the routing table in a map for future reference
		for (int i=0; i < nodeManifest.node_IDs.size(); ++i) {
			int nodeID = nodeManifest.node_IDs.get(i);
			nodeManifest.routing_table = routing_table[i];
			connectionsCache.getTCPConnection(nodeID).send(nodeManifest.getBytes());
			node_routing_table.put(nodeID, routing_table[i]);
		}

		System.out.println("Please wait for all nodes to finish setting up the overlay before issuing the next command...");
	}

  /**
   * Start the serve socket on a separate thread
   * @param port_num
   * @throws IOException
   */
	@Override
	public void startServerSocket(int port_num) throws IOException {
		TCPServerThread server = new TCPServerThread( port_num, this );
		Thread server_thread = new Thread( server );
		server_thread.start();
	}

  /**
   * Generate a node ID that has not been assigned before
   * @return
   */
	private int generateNodeID() {
		Integer nodeID = Util.randInt(0, Util.MAX_NODES-1);
		while (connectionsCache.contains(nodeID))
			nodeID = Util.randInt(0, Util.MAX_NODES-1);
		return nodeID;
	}

	/**
	 * TODO: what do I do if a node tries to register again, do i remove it, or do i...?
	 * This function is called when an overlay node sends registration message.
	 * @param e
	 * @param conn
	 */
	public synchronized void OverlayNodeSendsRegistrationHandler(Event e, TCPConnection conn) throws IOException {
		OverlayNodeSendsRegistration k = (OverlayNodeSendsRegistration) e;
		RegistryReportsRegistrationStatus ev = new RegistryReportsRegistrationStatus();

		// Check to make sure that the IP of the node matches the IP in the packet
		String remoteIP = conn.getRemoteIP();
		String receiveIP = k.ip_address;

		if (!remoteIP.equals(receiveIP)) { // if the node is not who they say they are
			log.error("Received invalid credentials.");
			log.error("\tremoteIP:  |" + remoteIP + "|");
			log.error("\treceiveIP: |" + receiveIP + "|");
			ev.successStatus = -1;
			ev.informationString = "Registry received invalid credentials.";
			conn.send(ev.getBytes());
			// close the connection
			conn.kill();
			// remove <String, TCPConnection> pair from cache
			connectionsCache.remove(conn.getRemoteAddressPortPair());
			return;
		}

		// check to make sure that the node has not registered before
		if (connectionsCache.contains(conn.getRemoteAddressPortPair())) {
			log.info("Messaging node has already registered before.");
			ev.successStatus = connectionsCache.getNodeID(conn.getRemoteAddressPortPair());
			ev.informationString = "Messaging node has already registered before";
			conn.send(ev.getBytes());
			return;
		}

		// if the node ID space is full
		if (connectionsCache.getNodeIDs().size() >= Util.MAX_NODES) {
			ev.successStatus = -1;
			ev.informationString = "Node ID space full, cannot register node, try again later.";
			conn.send(ev.getBytes());
			return;
		}

		log.info("All checks have passed.");

		// Generate a node ID
		int nodeID = generateNodeID();

		log.info("Received valid credentials.");
		log.info("\tremote:  |" + conn.getRemoteHostName()+":"+remoteIP + "|");

		// Once the node ID has been generated, assign it with this particular TCP connection
		connectionsCache.assign(nodeID, conn);

		// Once the ID has been generated, pair the node with its server IP and port
		// and put this in NodeAddressPortPairMap
		NodeAddressPortPairMap.put(nodeID, new HashMap<>());
		NodeAddressPortPairMap.get(nodeID).put("IP", k.ip_address);
		NodeAddressPortPairMap.get(nodeID).put("PORT", k.port);

		// send marshalled bytes to the messaging node
		try {
			ev.successStatus = nodeID;
			ev.informationString = "Registration request " +
					"successful. The number of messaging nodes currently constituting the overlay is ("+
					connectionsCache.NodeIDConnectionMapSize()+")";
			conn.send(ev.getBytes());

		} catch (IOException e1) { // if an error occurred during sending
			log.printStackTrace(e1);
			connectionsCache.unassign(conn);

		}

	}

	/**
	 *
	 * @param e
	 * @param connection
	 * @throws IOException
	 */
	public void OverlayNodeSendsDeregistrationHandler(Event e, TCPConnection connection) throws IOException {
		log.info("Received Overlay Node Sends Deregistration message:");

		if (connection == null) {
			System.out.print("Cannot deregister since node not registered.");
		}

		OverlayNodeSendsDeregistration q = (OverlayNodeSendsDeregistration) e;
		RegistryReportsDeregistrationStatus deregistrationEvent = new RegistryReportsDeregistrationStatus();
		deregistrationEvent.informationString = "Deregistration request unsuccessful. ";

		String remoteIP = connection.getRemoteIP();
		String receivedIP = q.ip_address;

		// check if the node is who they say they are
		if (remoteIP.equals(receivedIP)) {
			log.info("Received valid node credentials");
			int registeredID = -1;

			// check if the node registered before
			if (connectionsCache.contains(connection.getRemoteAddressPortPair())) {
				log.info("Node has registered before");
				registeredID = connectionsCache.getNodeID(connection.getRemoteAddressPortPair());

				// check if the registered node ID is the same as the node ID in the deregistration message
        if (registeredID == q.nodeID) {
        	log.info("Sending REGISTRY_REPORTS_DEREGISTRATION_STATUS message to node.");
          // Send a REGISTRY_REPORTS_DEREGISTRATION_STATUS message to the messaging node
          deregistrationEvent.successStatus = registeredID;
          deregistrationEvent.informationString = "Deregistration request successful.";
          connection.send(deregistrationEvent.getBytes());

          // remove the reference to the object, this will not cause garbage collection since the TCPConnection
          // has a TCPReceiver thread which has a reference to itself.
          connectionsCache.unassign(connection);

					// remove the routing table associated with node ID
          synchronized (node_routing_table) {
          	node_routing_table.remove((Integer)registeredID);
					}

					log.info("Sent REGISTRY_REPORTS_DEREGISTRATION_STATUS to node.");
          return;

        } else {
        	log.info("Received node ID is not the same as the registered node ID.");
          deregistrationEvent.successStatus = -1;
          deregistrationEvent.informationString += "Received node ID is not the same as the registered node ID.";
        }

			} else {
				log.info("Node has not registered before.");
				deregistrationEvent.successStatus = -1;
				deregistrationEvent.informationString += q.nodeID+" never registered.";
			}

		} else {
			log.info("Node sent invalid credentials");
			deregistrationEvent.successStatus = -1;
			deregistrationEvent.informationString += "Mismatch in the IP address of the node.";
		}

    connection.send(deregistrationEvent.getBytes());
	}
	
	/**
	 * NODE_REPORTS_OVERLAY_ SETUP_STATUS event handler
	 */
	public synchronized void OverlaySetupStatusHandler(Event e) {
		NodeReportsOverlaySetupStatus q = (NodeReportsOverlaySetupStatus) e;
		log.info("Received NODE_REPORTS_OVERLAY_SETUP_STATUS message with success status: "+q.successStatus);
		log.info("NODE_REPORTS_OVERLAY_SETUP_STATUS message: "+q.informationString);

		if (q.successStatus < 0) {
			log.info("Received success status "+q.successStatus);
			log.info("Information string: "+q.informationString);
			String[] failedNodes = q.informationString.substring(28).split(", ");
			System.out.println("The following nodes failed to setup the overlay:");
			System.out.println(Arrays.toString(failedNodes));
			log.error("Failed nodes: "+Arrays.toString(failedNodes));

		} else {
			overlayNodeSetupStatusCounter += 1;

			// when all the messaging nodes have completed setting up the overlay.
			if (overlayNodeSetupStatusCounter == connectionsCache.size()) {
				overlayNodeSetupStatusCounter = 0;
				flagOverlayNodesFinishedSetup = true;
				flagSettingUpOverlay = false;
				System.out.println("All nodes have finished setting up the overlay");
			}

		}
	}

	/**
	 *
	 */
	public synchronized void OverlayNodeReportsTaskFinishedHandler(Event e) throws IOException, InterruptedException {
		OverlayNodeReportsTaskFinished event = (OverlayNodeReportsTaskFinished) e;

		// update the number of nodes that finished their task
		overlayNodeTaskFinishTracker += 1;

		log.info("Received task completion from node: "+event.nodeID+", number of nodes remaining: "
				+(connectionsCache.size()-overlayNodeTaskFinishTracker));

		// check to see if all messaging nodes are done with their task
		boolean all_messaging_nodes_completed = (overlayNodeTaskFinishTracker == connectionsCache.size());

		// if all the messaging nodes are done with the task, ask for traffic summary
		if (all_messaging_nodes_completed == true) {
			RegistryRequestsTrafficSummary response = new RegistryRequestsTrafficSummary();
			log.info("All messaging nodes have completed their task.");
			log.info("Waiting "+waitTime+" seconds to collect traffic summary.");

			System.out.println("Waiting "+waitTime+" seconds to collect traffic summary");
			System.out.println("Run 'set-wait-time <seconds>' to change the wait time");

			long currentSleepTime = 0; // seconds
			while (currentSleepTime++ < waitTime) {
				Thread.sleep(1000);
				System.out.printf("Waiting for: %d seconds to collect traffic summary           \r", waitTime-currentSleepTime);
			}
			System.out.println("                                                                                           ");

			byte[] marshalledBytes = response.getBytes();
			for (int ID : connectionsCache.getNodeIDs()){
				connectionsCache.getTCPConnection(ID).send(marshalledBytes);
				log.info("Sent TrafficSummary request to node "+ID);
			}
			fOverlayNodesRoutingMessages = false;
		}
	}

	/**
	 * When all messaging nodes report their summary, create an instance of OverlayNodeReportsTrafficSummary
	 * and store it in overlayNodeReportsTrafficSummaryMap for later printing.
	 * @param e
	 */
	public synchronized void OverlayNodeReportsTrafficSummaryHandler(Event e) {
		log.info("Overlay node responded with traffic summary.");
		OverlayNodeReportsTrafficSummary event = (OverlayNodeReportsTrafficSummary) e;
		overlayNodeReportsTrafficSummaryMap.put(event.nodeID, event);
	}

	/**
	 * Print the reported traffic summary after all the nodes have reported traffic summary
	 */
	public synchronized void printOverlayTrafficSummary() {
		// Check to see if all the nodes reported traffic summary
		boolean all_nodes_reported_traffic_summary = (overlayNodeReportsTrafficSummaryMap.size() == connectionsCache.size());

		if (all_nodes_reported_traffic_summary) {
			System.out.printf("\u001B[47m \u001B[30m");
			System.out.printf("%22s|%19s|%19s|%19s|%22s\u001B[0m\n",
					"Packets Sent", "Packets Received", "Packets Relayed", "Sum Values Sent", "Sum Values Received");
			long c1 = 0;
			long c2 = 0;
			long c3 = 0;
			long c4 = 0;
			long c5 = 0;
			for (int ID : connectionsCache.getNodeIDs()) {
				OverlayNodeReportsTrafficSummary v = overlayNodeReportsTrafficSummaryMap.get(ID);
				System.out.printf("\u001B[47m \u001B[30mNode %-3s\u001B[0m", ID);
				System.out.printf("%14s|%19s|%19s|%19s|%22s\n",
						v.num_sent_packets, v.num_received_packets,
						v.num_relayed_packets, v.sent_packet_payload_sum, v.received_packet_payload_sum);
				c1 += v.num_sent_packets;
				c2 += v.num_received_packets;
				c3 += v.num_relayed_packets;
				c4 += v.sent_packet_payload_sum;
				c5 += v.received_packet_payload_sum;
			}
			System.out.printf("\u001B[47m\u001B[30m Sum: %17s|%19s|%19s|%19s|%22s\u001B[0m\n", c1, c2, c3, c4, c5);
		}
	}

	/**
	 * When a messaging node crashes, remove the entry from the registry. This can only happen before the setup-overlay
   * command is called
	 * @param connection
	 */
	@Override
	public void tcpReceiverErrorHandler(TCPConnection connection) {
		log.info("Cannot reach node at address "+connection.getRemoteHostName()+":"+connection.getRemoteAddressPortPair());

		// if the node was assigned an ID
		if (connectionsCache.contains(connection.getRemoteAddressPortPair())) {
			log.info("Disconnected node was assigned an ID");
			synchronized (node_routing_table) {
				node_routing_table.remove((Integer)connectionsCache.getNodeID(connection.getRemoteAddressPortPair()));
			}
			this.connectionsCache.unassign(connection);

		} else {
			log.info("Disconnected node was not assigned an ID");
			connectionsCache.remove(connection.getRemoteAddressPortPair());

		}

	}

	@Override
	public void tcpSenderErrorHandler(TCPConnection connection) {
		log.info("Error occurred while sending to node "+connection.getRemoteAddressPortPair());
		log.error("No routine implemented.");
	}

	@Override
	public void addTCPConnection(TCPConnection connection) throws IOException {
		if (connectionsCache.contains(connection.getRemoteAddressPortPair())) {
			log.info("Messaging node already connected.");
			return;
		}
		log.info("Adding "+connection.getRemoteAddressPortPair()+" to connectionsCache");
		connectionsCache.add(connection.getRemoteAddressPortPair(), connection);
		connection.startTCPSender();
		connection.startTCPReceiver();
	}

	@Override
	public void onEvent(Event e, String IpPort) throws Exception {
		switch (EventFactory.getInstance().eventProtocolMap.get(e.getType())) {

			case OVERLAY_NODE_SENDS_REGISTRATION:
				OverlayNodeSendsRegistrationHandler(e, connectionsCache.getTCPConnection(IpPort));
				break;

			case OVERLAY_NODE_SENDS_DEREGISTRATION:
				OverlayNodeSendsDeregistrationHandler(e, connectionsCache.getTCPConnection(IpPort));
				break;

			case NODE_REPORTS_OVERLAY_SETUP_STATUS:
				OverlaySetupStatusHandler(e);
				break;

			case OVERLAY_NODE_REPORTS_TASK_FINISHED:
				OverlayNodeReportsTaskFinishedHandler(e);
				break;

			case OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
				OverlayNodeReportsTrafficSummaryHandler(e);
				printOverlayTrafficSummary();
				break;

			default:
				System.err.println("Registry Error: Received an unknown event.");
		}
	}


	@Override
	public void parseArguments() throws Exception {
		Scanner stdin = new Scanner(System.in);
		while (stdin.hasNext()) {
			String[] LineToken = stdin.nextLine().split(" ");
			switch (LineToken[0]) {
				case "list-messaging-nodes":
					System.out.printf("\u001B[47m\u001B[30m");
					System.out.printf("%-53s|%-11s|%-6s\u001B[0m\n", "Host Name", "Port", "Node ID");
					for (int i=0; i<connectionsCache.size(); ++i) {
						int ID = connectionsCache.getNodeID(i);
						TCPConnection connection = connectionsCache.getTCPConnection(ID);
						String hostName = connection.getRemoteHostName();
						int port = connection.getRemotePort();
						System.out.printf(" %-52s| %-10s| %-5s\u001B[0m\n", hostName, port, ID);
					}
					break;

				case "setup-overlay":
					// if the user tries to run setup-overlay command twice
					if (flagOverlayNodesFinishedSetup) {
						System.out.println("Cannot run 'setup-overlay' command twice.");
						break;
					}
					if (flagSettingUpOverlay) { // currently in the process of setting up overlay
						System.out.println("Cannot run 'setup-overlay' command, nodes currently in the process of setting up overlay");
						break;
					}
					if (connectionsCache.size() <=0 ) {
						System.out.print("Cannot setup overlay with no messaging nodes");
						break;
					}
					String token = (LineToken.length > 1) ? LineToken[1] : "";
					num_routing_table_entries = (token == "") ? 3 : Integer.parseInt(token);
					if (num_routing_table_entries <= 0) {
						System.out.print("Number of routing table entries has to be greater than zero.");
						break;
					}
					int minNodes = (1 << (num_routing_table_entries-1))+1;
					if (connectionsCache.size() < minNodes) { // TODO: does the math look right
						System.out.print("Need "+minNodes+" messaging nodes for a routing table of size "+num_routing_table_entries);
						break;
					}
					sendNodeManifest();
					flagSettingUpOverlay = true;
					break;

				case "list-routing-tables":
					// print the routing table for each node
					for (int ID : connectionsCache.getNodeIDs()) {
						System.out.println("\n Routing Table at Node "+ID+":");
						node_routing_table.get(ID).print();
						System.out.println();
					}
					break;

				case "set-wait-time":
					this.waitTime = Integer.parseInt(LineToken[1]);
					System.out.println("Current waiting time set to "+this.waitTime+" seconds.");
					break;

				case "start":
					if (!flagOverlayNodesFinishedSetup) {
						System.out.println("Cannot start sending messages before all nodes have finished setting up overlay.");
						break;
					}
					if (fOverlayNodesRoutingMessages) {
						System.out.println("Overlay nodes currently routing messages...");
						System.out.println("Wait for the current round to complete and then try again.");
						break;
					}
					fOverlayNodesRoutingMessages = true;
					num_messages = Integer.parseInt(LineToken[1]);
					overlayNodeReportsTrafficSummaryMap.clear();
					overlayNodeTaskFinishTracker = 0;
					sendTaskInitiateMessage();
					System.out.println("Started messaging round with "+num_messages+" messages.");
					break;

				case "enable-logger":
					log.log_status = true;
					break;

				default:
			}
		}
		stdin.close();
	}

}
