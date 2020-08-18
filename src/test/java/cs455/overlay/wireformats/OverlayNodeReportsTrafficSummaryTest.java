package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class OverlayNodeReportsTrafficSummaryTest extends TestCase {

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY.getValue() ); // write the message type
        dout.writeInt( 55 );
        dout.writeInt( 1200 );
        dout.writeInt( 1300 );
        dout.writeLong( 1400 );
        dout.writeInt( 1500 );
        dout.writeLong( 1600 );
        dout.flush();	// flush the stream

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        OverlayNodeReportsTrafficSummary e = new OverlayNodeReportsTrafficSummary(getMarshalledByte());
        assertEquals(e.nodeID, 55);
        assertEquals(e.num_sent_packets, 1200);
        assertEquals(e.num_relayed_packets, 1300);
        assertEquals(e.sent_packet_payload_sum, 1400);
        assertEquals(e.num_received_packets, 1500);
        assertEquals(e.received_packet_payload_sum, 1600);
    }

    @Test
    public void testMarshalling() throws IOException {
        OverlayNodeReportsTrafficSummary e = new OverlayNodeReportsTrafficSummary(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}
