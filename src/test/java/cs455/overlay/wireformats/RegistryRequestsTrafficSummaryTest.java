package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class RegistryRequestsTrafficSummaryTest extends TestCase {

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY.getValue() ); // write the message type
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
        RegistryRequestsTrafficSummary e = new RegistryRequestsTrafficSummary();
        assert e instanceof RegistryRequestsTrafficSummary;
    }

    @Test
    public void testMarshalling() throws IOException {
        RegistryRequestsTrafficSummary e = new RegistryRequestsTrafficSummary();
        byte[] expected = new byte[] {(byte) Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY.getValue()};
        assertArrayEquals(e.getBytes(), expected);
    }
}