package cs455.overlay.node;

import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;

import java.io.IOException;

public enum PeerArgParser {

    print_counters_and_diagnostics {
        @Override
        public void action(Peer peer) {
            peer.oldStats.print();
        }
    },
    exit_overlay {
        @Override
        public void action(Peer peer) throws IOException {
            // if the node is connected to other messaging nodes
            if (peer.connectionsCache.size() > 0) {
                System.out.print("Cannot exit overlay ove setup.");
                return;
            }
            OverlayNodeSendsDeregistration deregistrationEvent = new OverlayNodeSendsDeregistration();
            deregistrationEvent.ip_address = peer.tcp_connection_registry.getLocalIP();
            deregistrationEvent.port = peer.server.getLocalPort();
            deregistrationEvent.nodeID = peer.nodeID;
            peer.tcp_connection_registry.send(deregistrationEvent.getBytes());
        }
    },
    print_routing_table {
        @Override
        public void action(Peer peer) {
            if (peer.routing_table == null) {
                System.out.print("Node has not received routing table from registry.");
                return;
            }
            peer.routing_table.print();
        }
    };

    public abstract void action(Peer peer) throws IOException;

}
