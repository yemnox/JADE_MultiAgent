#1. Link Container to NAT Network
apt update
apt install -y iproute2 iputils-ping net-tools nano
ifconfig

#2. Link the container to The Rest of the Network

# Inside each Docker container
ip addr add 10.1.1.1/24 dev eth1  # Change IP for each node 10.1.1.[1..5]
ip link set eth1 up

# Test connectivity
ping 10.1.1.10  # Ping host

#3. To make the changes persistent:
nano /etc/network/interfaces
# Add the following lines at the end of the file
# The loopback network interface
auto lo
iface lo inet loopback

# eth0 for DHCP (NAT/Internet access via GNS3 NAT cloud)
auto eth0
iface eth0 inet dhcp

# eth1 for Static IP (Internal Network connection to your router)
auto eth1
iface eth1 inet static
    address 10.1.1.1 # Change IP for each node 10.1.1.[1..5]
    netmask 255.255.255.0