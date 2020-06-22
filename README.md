# Structured-P2P-Network-Overlay

The objective of this project is to get familiar with coding in a 
distributed setting where we need to manage the underlying 
communications between nodes. 
As part of this project we will be implementing routing schemes 
for packets in a structured peer-to-peer (P2P) overlay system. 

This project requires: 
   * Constructing a logical overlay over a distributed set of nodes.
   * Using partial information about nodes within the overlay to 
        route packets. 

The project demonstrates how partial information about nodes 
comprising a distributed system can be used to route packets while 
ensuring correctness and convergence.

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
