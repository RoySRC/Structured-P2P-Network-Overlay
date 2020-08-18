package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;

import java.io.IOException;

public enum RegistryArgParser {

    list_messaging_nodes {
        @Override
        public void action(RegistryNode registry, String[] LineToken) {
            System.out.printf("\u001B[47m\u001B[30m");
            System.out.printf("%-53s|%-11s|%-6s\u001B[0m\n", "Host Name", "Port", "Node ID");
            for (int i = 0; i < registry.connectionsCache.size(); ++i) {
                int ID = registry.connectionsCache.getNodeID(i);
                TCPConnection connection = registry.connectionsCache.getTCPConnection(ID);
                String hostName = connection.getRemoteHostName();
                int port = connection.getRemotePort();
                System.out.printf(" %-52s| %-10s| %-5s\u001B[0m\n", hostName, port, ID);
            }
        }
    },
    setup_overlay {
        @Override
        public void action(RegistryNode registry, String[] LineToken) throws IOException {
            // if the user tries to run setup-overlay command twice
            if (registry.flagOverlayNodesFinishedSetup) {
                System.out.println("Cannot run 'setup-overlay' command twice.");
                return;
            }
            if (registry.flagSettingUpOverlay) { // currently in the process of setting up overlay
                System.out.println("Cannot run 'setup-overlay' command, nodes currently in the " +
                        "process of setting up overlay");
                return;
            }
            if (registry.connectionsCache.size() <= 0) {
                System.out.print("Cannot setup overlay with no messaging nodes");
                return;
            }
            String token = (LineToken.length > 1) ? LineToken[1] : "";
            registry.num_routing_table_entries = (token == "") ? 3 : Integer.parseInt(token);
            if (registry.num_routing_table_entries <= 0) {
                System.out.print("Number of routing table entries has to be greater than zero.");
                return;
            }
            int minNodes = (1 << (registry.num_routing_table_entries - 1)) + 1;
            if (registry.connectionsCache.size() < minNodes) { // TODO: does the math look right
                System.out.print("Need " + minNodes + " messaging nodes for a routing table of size "
                        + registry.num_routing_table_entries);
                return;
            }
            registry.sendNodeManifest();
            registry.flagSettingUpOverlay = true;
        }
    },
    list_routing_tables {
        @Override
        public void action(RegistryNode registry, String[] LineToken) {
            // print the routing table for each node
            for (int ID : registry.connectionsCache.getNodeIDs()) {
                System.out.println("\n Routing Table at Node " + ID + ":");
                registry.node_routing_table.get(ID).print();
                System.out.println();
            }
        }
    },
    set_wait_time {
        @Override
        public void action(RegistryNode registry, String[] LineToken) {
            registry.waitTime = Integer.parseInt(LineToken[1]);
            System.out.println("Current waiting time set to " + registry.waitTime + " seconds.");
        }
    },
    start {
        @Override
        public void action(RegistryNode registry, String[] LineToken) throws IOException {
            if (!registry.flagOverlayNodesFinishedSetup) {
                System.out.println("Cannot start sending messages before all nodes have finished setting " +
                        "up overlay.");
                return;
            }
            if (registry.fOverlayNodesRoutingMessages) {
                System.out.println("Overlay nodes currently routing messages...");
                System.out.println("Wait for the current round to complete and then try again.");
                return;
            }
            registry.fOverlayNodesRoutingMessages = true;
            registry.num_messages = Integer.parseInt(LineToken[1]);
            registry.overlayNodeReportsTrafficSummaryMap.clear();
            registry.overlayNodeTaskFinishTracker = 0;
            registry.sendTaskInitiateMessage();
            System.out.println("Started messaging round with " + registry.num_messages + " messages.");
        }
    };

    public abstract void action(RegistryNode registry, String[] LineToken) throws IOException;
}
