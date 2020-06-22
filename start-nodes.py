import os, sys, argparse
import numpy as np

parser = argparse.ArgumentParser(description='A python program to start messaging nodes on '
											 'remote machines')

parser.add_argument('--jar',
                    type=str,
                    help='Path to jar file')

parser.add_argument('--eid',
                    type=str,
                    help='The User EID')

parser.add_argument('--domain',
                    type=str,
					default='cs.colostate.edu',
                    help='The domain name of the machines')

parser.add_argument('--registry-host',
                    type=str,
                    help='The host machine of the registry')

parser.add_argument('--registry-port',
                    type=int,
                    help='The port on the host machine that the registry is running on')

parser.add_argument('--messaging_nodes',
                    type=int,
                    help='Number of messaging nodes to start')

parser.add_argument('--machine-list',
                    type=str,
                    help='The list of machines that the user wants to use')

parser.add_argument("--display-node-distribution",
					default=False,
					action='store_true'
					help="Displays a plot of the number of messaging nodes started on each machine")

args = parser.parse_args()

def startMessagingNode(mac):
	"""
	Start a messaging node on the given machine as mac
	:param mac: The machine on which to start a messaging node
	:return: None
	"""
	ssh_cmd = "\" echo 'Connecting to: "+mac+"' && ssh -t "+ \
				args.eid+"@"+mac+"."+args.domain+" 'hostnamectl && java -cp "+args.jar+ \
				" cs455.overlay.node.MessagingNode "+args.registry_host+" "+args.registry_port+"'\""
	command = "gnome-terminal -x bash -c "+ssh_cmd
	os.system(command)

def get_machines():
	"""
	:return: a numpy array of all the machines in the machines list file
	"""
	machines = []
	with open(args.machine_list) as f:
		for line in f:
			machines.append(line.split('\n')[0])
	return np.array(machines)

if __name__ == "__main__":
	machines = get_machines()

	# Create an empty node distributions array. This is to keep track of how many nodes are
	# started on each machine in the event that multiple messaging nodes are allowed to be
	# started on a single machine
	node_distribution = [0]*len(machines)
	node_distribution = np.array(node_distribution)

	# Start all the messaging nodes
	for i in range(args.messaging_nodes):
		machine = np.random.choice(machines)
		startMessagingNode(machine)
		node_distribution[np.argwhere(machines == machine)[0][0]] += 1

	if (args.display_node_distribution):
		import matplotlib.pyplot as plt
		plt.title("Distribution of messaging nodes across machines")
		plt.xticks(rotation=80)
		plt.plot(machines, node_distribution, 'o--')
		plt.show();
