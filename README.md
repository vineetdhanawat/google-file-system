# Google File System
Demo of file replication. Coursework CS 6378

## Introduction
1. There are n server nodes and m client nodes in the system, numbered from zero to n-1, zero to m-1. Each node executes on a different machine.
2. Establish reliable socket connections (TCP) between each pair of server-server nodes and client-server pair of nodes.
3. For each object, Hashfunction H(O) returns a server node to perform write or read.
4. Client Ci wants to update an object
 - Write is performed at three servers numbered: H(O), H(O)+1 modulo n, and H(O)+2 modulo n
 - Read is performed at any of the three servers numbered: H(O), H(O)+1 modulo n, and H(O)+2 modulo n
5. Conditions
 - Client should be able to randomly choose any of the three replicas of an object when it wishes to read the value of the object
 - Client can do update/insert only if two or more servers are available out of the chosen three.
 - Client should abort update/insert in case just one node is available.
 - In case of two or more clients trying to update same object. Updates must be performed in the same order in all servers.
5. Few of the nodes in the system can be down or out of reach at times. Such a scenario can be simulated by logical partitioning of nodes into two set of nodes. Nodes in 'up' set of nodes can respond while nodes in the 'down' set of nodes will not respond to requests.

## Sample Config File
	# Any text following '#' should be ignored
	3 # Total number of nodes
	#NodeID - HostName - Port
	0 127.0.0.1 50000 # Location of node 0
	1 127.0.0.1 51000 # Location of node 1
	2 127.0.0.1 52000 # Location of node 2

## Usage
	$ java -jar servernode 0
	$ java -jar servernode 1
	$ java -jar servernode <n>
	$ java -jar clientnode 0
	$ java -jar clientnode 1
	$ java -jar clientnode <n>

- Export a runnable jar file using eclipse.
- Place config.txt and ricartagrawala.jar in same folder on all the nodes.
- Execute commands on all the nodes in sequence, <n> = node number
- Thread has a time delay at multiple places seconds to start all the nodes.

## License

MIT: http://vineetdhanawat.mit-license.org/