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

# Run CentralServerAgent on ASC server
cd ~/jade
java -cp lib/jade.jar:agents jade.Boot -gui -host 10.1.1.10 -port 1099 ASC:CentralServerAgent

#When DataBase.sql is ready, run the following command to create the database and tables
java -cp "lib/jade.jar:lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.36.jar:lib/slf4j-simple-1.7.36.jar:agents" \
     jade.Boot -gui -host 10.1.1.10 -port 1099 \
     ASC:CentralServerAgent