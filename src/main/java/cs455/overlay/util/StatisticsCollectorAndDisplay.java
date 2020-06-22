package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
  /**
   * represents the number of packets that were sent by a node
   */
  private int sendTracker = 0;

  /**
   * represents the number of packets that were received
   */
  private int receiveTracker = 0;

  /**
   * keeps track of the number of packets relayed by a node
   */
  private int relayTracker = 0;

  /**
   * keep track of the number of packets a node has sent.
   * This variable stores the sums of the random values sent.
   */
  private long sendSummation = 0;

  /**
   * keep track of the number of packets a node has received.
   * This variable stores the sums of received payloads.
   */
  private long receiveSummation = 0;

  /**
   * Constructor
   */
  public StatisticsCollectorAndDisplay() {}

  /**
   * Copy the contents of S into {@this}.
   * @param S
   */
  public synchronized void copy(StatisticsCollectorAndDisplay S) {
    sendTracker = S.getSendTracker();
    receiveTracker = S.getReceiveTracker();
    relayTracker = S.getRelayTracker();
    sendSummation = S.getSendSummation();
    receiveSummation = S.getReceiveSummation();
  }

  public synchronized void incrementSendTracker() {
    ++sendTracker;
  }

  public synchronized void incrementReceiveTracker() {
    ++receiveTracker;
  }

  public synchronized void incrementRelayTracker() {
    ++relayTracker;
  }

  public synchronized void incrementSendSummation(long value) {
    sendSummation += value;
  }

  public synchronized void incrementReceiveSummation(long value) {
    receiveSummation += value;
  }

  public synchronized void print() {
    System.out.println();
    System.out.printf("\u001B[47m \u001B[30m");
    System.out.printf("%-9s|%-15s|%-15s|%-15s|%-13s\u001B[0m\n",
                      "Sent", "Received", "Relayed", "Sum Sent", "Sum Received");
    System.out.printf("%10s|%15s|%15s|%15s|%13s\n",
                      sendTracker, receiveTracker, relayTracker, sendSummation, receiveSummation);
  }

  public int getSendTracker() {
    return sendTracker;
  }

  public int getReceiveTracker() {
    return receiveTracker;
  }

  public int getRelayTracker() {
    return relayTracker;
  }

  public long getSendSummation() {
    return sendSummation;
  }

  public long getReceiveSummation() {
    return receiveSummation;
  }

  /**
   * reset the counters
   */
  public synchronized void resetCounters() {
    this.sendTracker = 0;
    this.receiveTracker = 0;
    this.relayTracker = 0;
    this.sendSummation = 0;
    this.receiveSummation = 0;
  }

  public static void main(String[] args) {
    StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();
    stats.sendSummation = Util.randInt();
    stats.receiveSummation = Util.randInt();
    stats.sendTracker = Util.randInt(0, 10000);
    stats.receiveTracker = Util.randInt(0, 10000);
    stats.relayTracker = Util.randInt(0, 10000);
    stats.print();
  }
}
