package cs455.overlay.wireformats;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class RegistryRequestsTaskInitiateTest extends TestCase {
    static int num_packets_to_send = 5;

    public static byte[] getMarshalledByte() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeByte( Protocol.REGISTRY_REQUESTS_TASK_INITIATE.getValue() );
        dout.writeInt( num_packets_to_send );
        dout.flush();

        data = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return data;
    }

    @Test
    public void testCreation() throws IOException {
        RegistryRequestsTaskInitiate e = new RegistryRequestsTaskInitiate(getMarshalledByte());
        assertEquals(e.num_packets, num_packets_to_send);
    }

    @Test
    public void testMarshalling() throws IOException {
        RegistryRequestsTaskInitiate e = new RegistryRequestsTaskInitiate(getMarshalledByte());
        assertArrayEquals(e.getBytes(), getMarshalledByte());
    }
}