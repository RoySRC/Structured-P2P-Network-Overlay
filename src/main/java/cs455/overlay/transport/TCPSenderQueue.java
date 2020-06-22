package cs455.overlay.transport;

import cs455.overlay.node.Peer;
import cs455.overlay.util.LOGGER;
import cs455.overlay.wireformats.Event;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * The purpose of this class is to queue all of the TCP packets to be sent one at a time
 * to prevent the TCP buffer from being overwhelmed and blocking indefinitely
 */

public class TCPSenderQueue implements Runnable{

  // Logging
  private static final LOGGER log = new LOGGER(TCPSenderQueue.class.getSimpleName());

  private BlockingQueue<Object[]> queue = null;

  public TCPSenderQueue() {
    this.queue = new LinkedBlockingQueue<>();
  }

  public synchronized void put(TCPConnection connection, Event e) throws InterruptedException {
    Object[] element = new Object[2];
    element[0] = connection;
    element[1] = e;
    this.getQueue().put(element);
  }

  public BlockingQueue<Object[]> getQueue() {
    return this.queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Object[] elt = getQueue().take(); // blocks if the operation cannot be performed immediately
        TCPConnection connection = (TCPConnection) elt[0];
        Event event = (Event) elt[1];
        connection.send(event.getBytes());

      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }
}
