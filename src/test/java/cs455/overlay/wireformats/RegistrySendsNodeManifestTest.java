package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.util.Util;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;

public class RegistrySendsNodeManifestTest extends TestCase {
    static int routing_size = 12;
    static int num_nodes = 10;	// number of nodes in the system
    static RoutingTable routing_table = null;
    static ArrayList<Integer> node_IDs = null;

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.REGISTRY_SENDS_NODE_MANIFEST.getValue() );
        dout.writeByte( routing_size );

        routing_table = new RoutingTable(routing_size);
        for (int i=0; i < routing_size; ++i) {
            int nodeID = Util.randInt(0, Util.MAX_NODES-1);
            String IP = Util.generateRandomIP();
            int port = Util.randInt(1025, 5000);
            int distance = (1 << i);

            routing_table.entry[i].fill(distance, nodeID, IP, port);
            dout.writeInt(nodeID);	// node ID
            dout.writeByte(IP.length());	// length of the IP address
            dout.writeBytes(IP);	// IP address
            dout.writeInt(port);	// port number
        }

        dout.writeInt(num_nodes);

        node_IDs = new ArrayList<>();
        for (int i=0; i<num_nodes; ++i) {
            int nid = Util.randInt(0, Util.MAX_NODES - 1);
            dout.writeInt(nid);
            node_IDs.add(nid);
        }
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        RegistrySendsNodeManifest e = new RegistrySendsNodeManifest(getMarshalledByte());
        for (int i=0; i < routing_size; ++i) {
            RoutingEntry routingEntry = routing_table.entry[i];
            RoutingEntry eRoutingEntry = e.routing_table.entry[i];

            assertEquals(routingEntry.distance, eRoutingEntry.distance);
            assertEquals(routingEntry.nodeID, eRoutingEntry.nodeID);
            assertEquals(routingEntry.IP, eRoutingEntry.IP);
            assertEquals(routingEntry.port, eRoutingEntry.port);
        }
        assertEquals(e.node_IDs, node_IDs);
    }

    @Test
    public void testMarshalling() throws IOException {
        byte[] data = getMarshalledByte();
        RegistrySendsNodeManifest e = new RegistrySendsNodeManifest(data);
        assertArrayEquals(e.getBytes(), data);
    }
}