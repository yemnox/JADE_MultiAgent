sudo apt update
# Installe les utilitaires nécessaires (inclut tunctl)
sudo apt install uml-utilities

# Create tap0 interface
sudo ip tuntap add tap0 mode tap

# Assign IP to the interface
sudo ip addr add 10.1.1.10/24 dev tap0  # Replace tap0 with your interface
sudo ip link set tap0 up

# Test from host
ping 10.1.1.1  # Ping node1