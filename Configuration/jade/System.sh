cd ~/jade-project/agents

# Compile CentralServerAgent
javac -cp ../jade/lib/jade.jar CentralServerAgent.java

# Compile LocalAgent
javac -cp ../jade/lib/jade.jar LocalAgent.java

# Check for .class files
ls *.class

# 1. Copy the CentralServerAgent code
nano agents/CentralServerAgent.java
# Paste the code from above, save (Ctrl+X, Y, Enter)

# 2. Compile
cd agents
javac -cp ~/jade/jade/lib/jade.jar CentralServerAgent.java

# 3. Run the CentralServerAgent
cd ~/jade/jade/src/examples
java -cp ~/jade/jade/lib/jade.jar jade.Boot -gui -host 10.1.1.10 -port 1099 ASC:CentralServerAgent
