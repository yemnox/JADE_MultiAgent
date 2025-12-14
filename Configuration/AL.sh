# Inside each Docker container
ip addr add 10.1.1.1/24 dev eth0  # Change IP for each node 10.1.1.[1..5]
ip link set eth0 up
ip route add default via 10.1.1.50 

# Test connectivity
ping 10.1.1.10  # Ping host