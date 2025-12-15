# Install SQLite
sudo apt-get update
sudo apt-get install -y sqlite3

# Verify installation
sqlite3 --version

# Download SQLite JDBC driver for Java
cd ~/jade/lib
wget https://github.com/xerial/sqlite-jdbc/releases/download/3.44.1.0/sqlite-jdbc-3.44.1.0.jar
# Download SLF4J API
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar

# Download SLF4J Simple (logging implementation)
wget https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar

# Verify download
ls -lh sqlite-jdbc-3.44.1.0.jar