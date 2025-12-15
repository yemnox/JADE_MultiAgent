# On AL nodes (10.1.1.[1..5])
# Install Java (OpenJDK)
apt-get install -y openjdk-17-jre-headless openjdk-17-jdk

# Verify installation
java -version


#///////////////////PYTHON & DEPENDENCIES INSTALLATION ON AL NODES
# 1. Configure APT (if not done)
cat > /etc/apt/sources.list << 'EOF'
deb http://archive.ubuntu.com/ubuntu noble main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu noble-updates main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu noble-security main restricted universe multiverse
EOF

# 2. Update and install
apt-get clean
apt-get update
apt-get install -y python3 python3-psutil

# 3. Verify Python
python3 --version
python3 -c "import psutil; print('psutil OK')"




#///////////////////DOWNLOAD JADE AGENT FILES FROM ASC SERVER
# Download files from ASC HTTP server
apt install wget -y
mkdir -p /opt/jade/agents
cd /opt/jade/agents
wget http://10.1.1.10:8080/jade.jar
wget http://10.1.1.10:8080/CentralServerAgent.class
wget http://10.1.1.10:8080/'CentralServerAgent$ReceiveAlertsBehaviour.class'
wget http://10.1.1.10:8080/'CentralServerAgent$1.class'
wget http://10.1.1.10:8080/LocalAgent.class
wget http://10.1.1.10:8080/'LocalAgent$ReceiveResponseBehaviour.class'
wget http://10.1.1.10:8080/'LocalAgent$MonitoringBehaviour.class'
wget http://10.1.1.10:8080/'MobileAuditAgent$InvestigationBehaviour.class'
wget http://10.1.1.10:8080/MobileAuditAgent.class


# Run LocalAgent on each AL node
cd /opt/jade/agents
java -cp jade.jar:. jade.Boot -container \
     -host 10.1.1.10 -port 1099 \
     -local-host 10.1.1.1 \
     -container-name LA1-Container \
     AL1:LocalAgent

# Update and install stress-ng for CPU stress testing (In Host After SSH)
apt-get update
apt-get install -y stress-ng

# Run CPU stress test
stress-ng --cpu 1 --timeout 5s
