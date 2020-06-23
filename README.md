# Structured-P2P-Network-Overlay

The objective of this project is to get familiar with coding in a 
distributed setting where we need to manage the underlying 
communications between nodes. 
As part of this project we will be implementing routing schemes 
for packets in a structured peer-to-peer (P2P) overlay system. 

This project requires: 
   * Constructing a logical overlay over a distributed set of
    nodes.
   * Using partial information about nodes within the overlay to 
    route packets. 

The project demonstrates how partial information about nodes 
comprising a distributed system can be used to route packets
while ensuring correctness and convergence.

Nodes within the system are imposed a logical structure. 
This logical structure is the overlay. The overlay encompasses 
the organization of the nodes, their location, and how
information is maintained at each node. The logical overlay helps 
with locating nodes and routing content efficiently.

The overlay can contain any number of messaging nodes. Each 
messaging node is connected to some other messaging node.

Once the overlay has been setup, messaging nodes in the system 
will select a node at random and send a message to that node. 
Rather than send this message directly to the sink node, the 
source node will use the overlay for communications. Each node 
consults its routing table, and either routes the packet to its 
final destination or forwards it to an intermediate node closest 
(in the node ID space) to the final destination. 

Depending on the overlay, there may be zero or more intermediate 
messaging nodes between a particular source and sink that packets 
must pass through. Such intermediate nodes relay the message to 
the sink. Verification of correctness of packet exchanges between 
the source and sink are done by ensuring that the number of 
messages that you send and receive within the system match, and 
these messages have not been corrupted in transit to the intended 
recipient. Message exchanges happen continually in the system. 
All communication is based on TCP.

This project is a simplified version of a structured P2P system 
based on distributed hash tables (DHTs); the routing here is a 
simplified implementation of the well-known Chord P2P system.
In most DHTs node identifiers are 128-bits (when they are based 
on UUIDs) or 160-bits (when they are generated using SHA-1). In 
such systems the identifier space ranges from 0 to 
![](./readme_files/pow_2_128.gif) or
![](./readme_files/pow_2_160.gif).
Structured P2P systems are important because they have 
demonstrably superior scaling properties.

## Components
There are two components as part of this project: the registry and
messaging node. There is exactly one instance of the registry and 
multiple instances of the messaging nodes.

#### Registry
There is exactly one registry in the system. The registry provides 
the following functions:
* Allows messaging nodes to register themselves. This is performed 
when a messaging node starts for the first time.
* Assign random identifiers (between 0-127) to nodes within the 
system; the registry also has to ensure that no two nodes are 
assigned the same ID.
* Allows messaging nodes to deregister themselves. This is 
performed when a messaging node leaves the overlay.
* Enables the construction of the overlay by populating the 
routing table at the messaging nodes. The routing table 
dictates the connections that a messaging node initiates with 
other messaging nodes in the system.

The registry maintains information about the registered messaging 
nodes. The registry does not play any role in the routing of data 
within the overlay. Interactions between the messaging nodes and 
the registry are via request-response messages. For each request 
that it receives from the messaging nodes, the registry will send
a response back to the messaging node (based on the IP address 
associated with the socket’s input stream) where the request 
originated. The contents of this response depend on the type of 
the request and the outcome of processing this request.

#### Messaging Node
There are multiple messaging nodes in the system. A messaging 
node provides two closely related functions: it initiates and 
accepts both communications and control messages within the 
system.

Each messaging node automatically configures the port over 
which it listens for communications. Once the initialization is 
complete, the node should send a registration request to the 
registry. Each node in the system has a routing table that is 
used to route content to the sink. This routing table contains 
information about a subset of nodes in the system. Messaging 
nodes use this routing table to forward packets to the sink 
specified in the message. Every messaging node makes local 
decisions based on its routing table to get the packets closer 
to the sink.

## Interaction between components
In this project there are several control message types. Each
of these message types have their own separate class. These
classes are responsible for reading and creating marshalled
byte arrays to be sent to nodes.

#### Registration message type
Upon starting up, each messaging node registers its IP address
, and port number with the registry. There are four fields
in this registration request:
```
byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
byte: length of following "IP address" field
byte[^^]: IP address; from InetAddress.getAddress()
int: Port number
```

When the registry receives this request, it checks to see if the
node had previously registered and ensures that the IP address
in the message matches the address where the request originated
. The registry issues an error message under two circumstances:
* If the node had previously registered and has a valid entry in
 its registry.
* If there is a mismatch in the address that is specified in
 the registration request and the IP address of the request

If there is no error, the registry generates a unique
identifier (between 0-127) for the node while ensuring that there
are no duplicate IDs being assigned. The contents of the
response message generated by the registry are depicted below
. The success or failure of the registration request is
indicated in the status field of the response message.
```
byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
int: Success status; Assigned ID if successful, -1 if failure
byte: Length of following "Information string" field
byte[^^]: Information string; ASCII charset
```

If the registration was successful, the registry includes a
message that indicates the number of entries currently present
in its registry. A sample information string is ***“Registration
request successful. The number of messaging nodes currently
constituting the overlay is (5)”***. If the registration was
unsuccessful, the message from the registry should indicate why
the request was unsuccessful. In the rare case that a messaging
node fails just after sending a registration request, the registry
will not be able to communicate with it. In this case, the
entry for the messaging node is removed from the data structure
maintained at the registry.

#### Deregistration message type
When a messaging node exits it it deregisters itself. This is
done by sending a deregistration message to the registry. This
deregistration request includes the following fields:
```
byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
byte: length of following "IP address" field
byte[^^]: IP address; from InetAddress.getAddress()
int: Port number
int: assigned Node ID
```

The registry checks if the request is valid by checking where
the message originated and if the node was previously
registered. Error messages are returned in case of a mismatch
in the addresses or if the messaging node is not registered
with the overlay. The registry will respond with a
`REGISTRY_REPORTS_DEREGISTRATION_STATUS` control message that is
similar to the `REGISTRY_REPORTS_REGISTRATION_STATUS` message.

#### Peer node manifest message type
Once the **setup-overlay** command is specified at the registry it
performs a series of actions that lead to the creation of the
overlay with a routing table being installed at every node.
Afterwards, messaging nodes initiate connections with each other
. Messaging nodes await instructions from the registry
regarding other messaging nodes to connect to – messaging
nodes only initiate connections to nodes that are part of its
routing table. 

The registry must ensure two properties. First, it must ensure
that the size of the routing table at every messaging node in
the overlay is identical; this is a configurable metric (with a
default value of 3) and is specified as part of the **setup
-overlay** command. 

If the routing table size requirement for the overlay is 
![](https://latex.codecogs.com/gif.latex?\inline&space;N_{R})
 , each messaging node will have links to 
![](https://latex.codecogs.com/gif.latex?\inline&space;N_{R})
other messaging nodes in the overlay. The registry selects these
![](https://latex.codecogs.com/gif.latex?\inline&space;N_{R})
messaging nodes that constitute the peer-messaging nodes list
for a messaging node such that the first entry is one hop away
in the ID space, the second entry is two hops away, and the
third entry is 4 hops away. Consider a network overlay
comprising nodes with the following identifiers: 10, 21, 32, 43
, 54, 61, 77, 87, 99, 101, 103. The routing table at 10
includes information about nodes <21, 32, and 54> while the
routing table at node 101 includes information about nodes <103
, 10, 32>; notice how the ID space wraps around after 103. A
messaging node initiates connections to all nodes that are part
of its routing table. A messaging node should never be connect
to itself. The registry also informs each node about the IDs
of all nodes in the system. This information is used in the
testing part of the overlay to randomly select sink nodes
that messages should be sent to. The registry includes all this
information in a `REGISTRY_SENDS_NODE_MANIFEST` message. The
contents of the manifest message are different for each
messaging node (since the routing table at every messaging node
is different). The wire format is shown when 
![](https://latex.codecogs.com/gif.latex?\inline&space;N_{R}=3)
, if 
![](https://latex.codecogs.com/gif.latex?\inline&space;N_{R}=4)
there will also be an entry for a node
![](https://latex.codecogs.com/gif.latex?\inline&space;2^{N_{R}-1})
hops away.
```
byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
byte: routing table size N R
int: Node ID of node 1 hop away
byte: length of following "IP address" field
byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress()
int: Port number of node 1 hop away
int: Node ID of node 2 hops away
byte: length of following "IP address" field
byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress()
int: Port number of node 2 hops away
int: Node ID of node 4 hops away
byte: length of following "IP address" field
byte[^^]: IP address of node 4 hops away; from InetAddress.getAddress()
int: Port number of node 4 hops away
byte: Number of node IDs in the system
int[^^]: List of all node IDs in the system [Note no IPs are included]
```
Note that the manifest message includes IP addresses only for
nodes within a particular node’s routing table. Upon receipt of
the manifest from the registry, each messaging node initiates
connections to the nodes that comprise its routing table.

#### Node overlay setup message
Upon receipt of the `REGISTRY_SENDS_NODE_MANIFEST` from the
registry, each messaging node should initiate connections to
the nodes in its routing table. Every messaging node must
report to the registry on the status of setting up connections
to nodes that are part of its routing table. The message schema
is outlined below
```
byte: Message type (NODE_REPORTS_OVERLAY_SETUP_STATUS)
int: Success status; Assigned ID if successful, -1 in case of a failure
byte: Length of following "Information string" field
byte[^^]: Information string; ASCII charset
```

#### Initiate sending messages message
The registry informs nodes in the overlay when to start sending
messages to each other. It does so via the
`REGISTRY_REQUESTS_TASK_INITIATE` control message. This message
also includes the number of packets that must be sent by each
messaging node.
```
byte: Message type; REGISTRY_REQUESTS_TASK_INITIATE
int: Number of data packets to send
```

#### Send data packets message
Data packets can be fed into the overlay from any messaging
node within the system. Packets are sent from a source to a
sink; it is possible that there might be zero or more
intermediate nodes in the system that relay packets en route to
the sink. Every node tracks the number of messages that it has
relayed during communications within the overlay.

When a packet is ready to be sent from a source to the sink
, the source node consults its routing table to identify the
best node that it should send the packet to. There are two
situations: there is an entry for the sink in the routing table
, or the sink does not exist in the routing table and the
messaging node must relay the packet to the closest node to the
sink. Routing decisions only target nodes that are clockwise
successors. 

A key requirement for the dissemination of packets within the
overlay is that no messaging node should receive the same
packet more than once.
```
byte: Message type; OVERLAY_NODE_SENDS_DATA
int: Destination ID
int: Source ID
int: Payload
int: Dissemination trace field length (number of hops)
int[^^]: Dissemination trace comprising nodeIDs that the packet traversed
through
```

The dissemination trace includes nodes (except the source and
sink) that were involved in routing the particular packet.

#### Inform registry of task completion
Once a node has completed its task of sending a certain number
of messages, it informs the registry of its task completion
using the `OVERLAY_NODE_REPORTS_TASK_FINISHED` message.
This message should have the following format:
```
byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED
byte: length of following "IP address" field
byte[^^]: Node IP address:
int: Node Port number:
int: nodeID
```

#### Retrieve traffic summaries from nodes
Once the registry has received
`OVERLAY_NODE_REPORTS_TASK_FINISHED` messages from all the
registered nodes it will issue a
`REGISTRY_REQUESTS_TRAFFIC_SUMMARY message`. This message is sent
to all the registered nodes in the overlay. This message will
have the following format.
```
byte: Message Type; REGISTRY_REQUESTS_TRAFFIC_SUMMARY
```

#### Sending traffic summaries from the nodes to the registry
Upon receipt of the `REGISTRY_REQUESTS_TRAFFIC_SUMMARY` message
from the registry, the messaging node creates a response
that includes summaries of the traffic that it has participated
in. The summary includes information about messages that
were sent, received, and relayed by the node. This message will
have the following format.
```
byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
int: Assigned node ID
int: Total number of packets sent
(only the ones that were started/initiated by the node)
int: Total number of packets relayed
(received from a different node and forwarded)
long: Sum of packet data sent
(only the ones that were started by the node)
int: Total number of packets received
(packets with this node as final destination)
long: Sum of packet data received
(only packets that had this node as final destination)
```
Once the `OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY` message is sent
to the registry, the node must reset the counters associated
with traffic relating to the messages it has sent, relayed, and
received so far: the number of messages sent, summation of sent
messages, etcetera.

#### Summary of Messages Exchanged between the registry and node
The figure below depicts the exchange of messages between the registry and a particular messaging
node in the system.
![](./readme_files/summary_message_exchanges.png)

#### Values for the control messages
The following values for control messages are used:
```
 OVERLAY_NODE_SENDS_REGISTRATION            2
 REGISTRY_REPORTS_REGISTRATION_STATUS       3
   
 OVERLAY_NODE_SENDS_DEREGISTRATION          4
 REGISTRY_REPORTS_DEREGISTRATION_STATUS     5
 
 REGISTRY_SENDS_NODE_MANIFEST               6
 NODE_REPORTS_OVERLAY_SETUP_STATUS          7
 
 REGISTRY_REQUESTS_TASK_INITIATE            8
 OVERLAY_NODE_SENDS_DATA                    9
 OVERLAY_NODE_REPORTS_TASK_FINISHED         10
 
 REGISTRY_REQUESTS_TRAFFIC_SUMMARY          11
 OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY       12 
```

## Supported Commands
#### Commands supported by the registry
#####list-messaging-nodes
This result in information about the messaging nodes (hostname
, port-number, and node ID) being listed. Information for each
 messaging node should be listed on a separate line.

#####setup-overlay <number-of-routing-table-entries>
This results in the registry setting up the overlay. It
does so by sending every messaging node the
`REGISTRY_SENDS_NODE_MANIFEST` message that contains
information about the routing table specific to that node and
also information about other nodes in the system. This does not
deal with the case where a messaging node is added or removed
after the overlay has been set up.

#####list-routing-tables
This lists information about the computed routing tables
for each node in the overlay. Each messaging node’s information
includes the node’s IP address, portnum, and logical-ID.

#####start number-of-messages (e.g. start 25000)
The start command results in the registry sending the 
`REGISTRY_REQUESTS_TASK_INITIATE` to all nodes within the
overlay. A command of start 25000 results in each messaging
node sending 25000 packets to nodes chosen at random. A detailed
description of the sequence of actions that this triggers 
is provided in [citation needed].

#### Commands supported by the messaging nodes
#####print-counters-and-diagnostics
This prints information (to the console using System.out) about
the number of messages that have been sent, received, and
relayed along with the sums for the messages that have been
sent from and received at the node.

#####exit-overlay
This allows a messaging node to exit the overlay. The messaging
node first sends a deregistration message to the registry and
await a response before exiting and terminating the process.

## 