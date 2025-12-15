# On ASC (10.1.1.10)
#Make everything in one single folder for easy HTTP serving
mkdir -p ~/jade-deploy
cp ~/jade/lib/jade.jar ~/jade-deploy/
cp ~/jade/agents/LocalAgent.class ~/jade-deploy/
cp ~/jade/agents/CentralServerAgent.class ~/jade-deploy/

# Start HTTP Server
cd ~/jade-deploy
python3 -m http.server 8080
# Server now running on http://10.1.1.10:8080


# On AL nodes (10.1.1.[1..5])
# Install Java (OpenJDK)
apt-get install -y openjdk-11-jre-headless

# Verify installation
java -version

# Download files from ASC HTTP server
wget http://10.1.1.10:8080/jade.jar
wget http://10.1.1.10:8080/CentralServerAgent.class
wget http://10.1.1.10:8080/LocalAgent.class
