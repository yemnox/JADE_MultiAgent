#!/bin/bash
#=======TO Verify:
#cd ~/jade
#chmod +x ~/jade/start_asc.sh
# Start ASC with all dependencies

JADE_HOME="/home/yemnox/jade"
JADE_JAR="$JADE_HOME/lib/jade.jar"
SQLITE_JAR="$JADE_HOME/lib/sqlite-jdbc-3.44.1.0.jar"
SLF4J_API="$JADE_HOME/lib/slf4j-api-1.7.36.jar"
SLF4J_SIMPLE="$JADE_HOME/lib/slf4j-simple-1.7.36.jar"
AGENTS_DIR="$JADE_HOME/agents"

echo "╔════════════════════════════════════════╗"
echo "║  Starting ASC with Database Support    ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Check if all JARs exist
echo "Checking dependencies..."

if [ ! -f "$JADE_JAR" ]; then
    echo "✗ JADE not found: $JADE_JAR"
    exit 1
fi
echo "✓ JADE found"

if [ ! -f "$SQLITE_JAR" ]; then
    echo "✗ SQLite JDBC not found: $SQLITE_JAR"
    echo "  Downloading..."
    wget -P $JADE_HOME/lib https://github.com/xerial/sqlite-jdbc/releases/download/3.44.1.0/sqlite-jdbc-3.44.1.0.jar
fi
echo "✓ SQLite JDBC found"

if [ ! -f "$SLF4J_API" ]; then
    echo "✗ SLF4J API not found: $SLF4J_API"
    echo "  Downloading..."
    wget -P $JADE_HOME/lib https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
fi
echo "✓ SLF4J API found"

if [ ! -f "$SLF4J_SIMPLE" ]; then
    echo "✗ SLF4J Simple not found: $SLF4J_SIMPLE"
    echo "  Downloading..."
    wget -P $JADE_HOME/lib https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar
fi
echo "✓ SLF4J Simple found"

echo ""
echo "Starting ASC Agent..."
echo ""

cd $JADE_HOME

java -cp "$JADE_JAR:$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE:$AGENTS_DIR" \
     jade.Boot -gui -host 10.1.1.10 -port 1099 \
     ASC:CentralServerAgent