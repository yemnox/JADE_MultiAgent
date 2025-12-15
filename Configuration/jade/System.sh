#Compile CentralServerAgenta
cd ~/jade/agents

# Method 1: Using absolute path
javac -cp /home/yemnox/jade/lib/jade.jar CentralServerAgent.java

# Method 2: Using relative path (if you're in ~/jade/agents)
javac -cp ../lib/jade.jar CentralServerAgent.java

# Compile LocalAgent
javac -cp ~/jade/lib/jade.jar LocalAgent.java

# Check for .class files
ls *.class

# 1. Copy the CentralServerAgent code
nano agents/CentralServerAgent.java
# Paste the code from above, save (Ctrl+X, Y, Enter)

# 2. Compile
cd agents
javac -cp ~/jade/lib/jade.jar CentralServerAgent.java

# 3. Run the CentralServerAgent
cd ~/jade/src/examples
java -cp ~/jade/lib/jade.jar jade.Boot -gui -host 10.1.1.10 -port 1099 ASC:CentralServerAgent
