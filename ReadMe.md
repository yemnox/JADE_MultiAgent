# JADE Multi-Agent Network Monitoring System

## Overview

This project demonstrates a multi-agent network monitoring system using JADE (Java Agent DEvelopment Framework). It showcases how mobile agents can be used to monitor and control local agents across a distributed network environment simulated with GNS3.

## Architecture

The system consists of three types of agents working in a coordinated manner:

### Central Server Agent (ASC)
- **Location**: Main host (Ubuntu 24.04)
- **Responsibilities**:
  - Receives and processes alerts from local agents
  - Maintains SQLite database for logging anomalies and audit reports
  - Deploys mobile audit agents in response to critical alerts
  - Provides centralized monitoring dashboard and control interface
- **Key Behaviors**:
  - Cyclic alert reception with message filtering
  - Periodic status reporting
  - Database initialization and connection management
  - Dynamic mobile agent deployment

### Local Agents (AL)
- **Location**: Docker containers (Ubuntu 20.04)
- **Responsibilities**:
  - Continuous monitoring of local system resources
  - Threshold-based alerting for CPU and memory usage
  - Communication with central agent via ACL messages
  - Acknowledgment handling for received commands
- **Key Behaviors**:
  - Ticker-based monitoring every 5 seconds
  - Alert generation with severity levels (WARNING/CRITICAL)
  - Message sending and response handling

### Mobile Audit Agents (AMA)
- **Deployment**: Dynamically created by ASC on demand
- **Responsibilities**:
  - On-site investigation of reported anomalies
  - Collection of detailed system metrics and process information
  - Generation of comprehensive audit reports
  - Self-termination after mission completion
- **Key Behaviors**:
  - One-shot investigation execution
  - System data collection (CPU, memory, processes, network)
  - Report building with analysis and recommendations

## How It Works

1. **Initialization**: Central agent starts and initializes database connection
2. **Deployment**: Local agents are deployed on network nodes via containers
3. **Monitoring**: Local agents continuously monitor system resources using Python psutil
4. **Alerting**: When thresholds are exceeded, alerts are sent to central agent
5. **Response**: Central agent logs alerts and deploys mobile agents for critical issues
6. **Investigation**: Mobile agents collect detailed information and generate reports
7. **Reporting**: All data is stored in SQLite database for analysis and visualization

## Installation

### Prerequisites
- Ubuntu 24.04 LTS (host machine for ASC)
- Ubuntu 20.04 LTS (Docker containers for AL agents)
- GNS3 network simulator
- Java Development Kit 17
- Python 3 with psutil library
- Docker for containerization

### Installing Core Dependencies

1. **GNS3 Network Simulator**
   - Refer to `Installation/GNS3.sh` for installation commands

2. **Grafana (Optional - for Visualization)**
   - Refer to `Installation/Grafana.sh` for installation commands

3. **SQLite Database**
   - Refer to `Installation/SQLite.sh` for installation and JDBC driver setup

4. **JADE Framework**
   - Refer to `Installation/Jade.md` for comprehensive installation guide
   - Includes Java version alignment, GUI configuration, and troubleshooting

5. **Additional Tools**
   - VMware: `Installation/VMware.sh`
   - VS Code: `Installation/VScode.sh`

## Network Setup

### GNS3 Configuration
- Create a new project in GNS3
- Add Docker containers running Ubuntu 20.04 for local agents (AL nodes)
- Add a host machine running Ubuntu 24.04 for the central agent (ASC)
- Configure network topology with IP addresses:
  - ASC (Central): 10.1.1.10
  - AL1-AL5 (Local Agents): 10.1.1.1 to 10.1.1.5

### Agent Deployment Scripts

1. **Setup Local Agents (AL nodes)**
   - Use `Configuration/Agent.sh` to install dependencies on AL containers
   - Install Java 17, Python 3, psutil
   - Download agent files from ASC HTTP server
   - Run LocalAgent on each container

2. **Setup Central Agent (ASC)**
   - Use `Configuration/ASC.sh` to deploy agent files
   - Start HTTP server for file distribution
   - Run CentralServerAgent with database support

3. **Compile Agents**
   - Use `Configuration/AMA.sh` to compile MobileAuditAgent

## Database Setup

1. **Create Database Tables**
   ```bash
   sqlite3 anomalies.db < Configuration/DataBase.sql
   ```

2. **Tables Created**
   - `anomalies`: Logs detected anomalies
   - `audit_reports`: Stores detailed audit reports
   - `agent_actions`: Tracks agent deployments and actions

## Usage

### Starting the System
1. **Launch Central Agent**:
   ```bash
   ./scripts/start_asc.sh
   ```
   This starts the ASC with database connectivity and GUI interface.

2. **Deploy Local Agents**:
   ```bash
   # On each AL container
   java -cp jade.jar:. jade.Boot -container \
        -host 10.1.1.10 -port 1099 \
        AL1:LocalAgent
   ```

### Testing and Monitoring
1. **Simulate System Load**:
   ```bash
   # Generate CPU stress to trigger alerts
   apt-get install stress-ng
   stress-ng --cpu 2 --timeout 60s
   ```

2. **Monitor Agent Activity**:
   - JADE GUI shows active agents and message flows
   - Database queries show logged anomalies:
     ```sql
     SELECT * FROM anomalies ORDER BY timestamp DESC LIMIT 10;
     ```

3. **View Audit Reports**:
   ```sql
   SELECT * FROM audit_reports WHERE target_node = 'AL1';
   ```

### Advanced Operations
- **Agent Communication**: Study `Configuration/jade/jade101/Agent Communication.java` for message patterns
- **Behavior Patterns**: Refer to `Configuration/jade/jade101/Behaviour.java` for agent behavior examples
- **Basic Agent Structure**: See `Configuration/jade/jade101/MyAgent.java` for fundamental agent implementation

## Project Structure

- `Configuration/`: Agent Java classes, setup scripts, database schema
  - `jade/Agents/`: CentralServerAgent.java, LocalAgent.java, MobileAuditAgent.java
  - `jade/jade101/`: Basic JADE examples (Agent Communication, Behaviour, MyAgent)
  - Setup scripts: Agent.sh, AMA.sh, ASC.sh, DataBase.sql
- `Installation/`: Installation guides and scripts
  - Jade.md: Detailed JADE installation guide
  - GNS3.sh, Grafana.sh, SQLite.sh: Dependency installation
- `scripts/`: Startup scripts for agents (start_asc.sh)
- `Network/`: Network configuration scripts (AL_SSH.sh, AL.sh, ASC.sh)
- `src/`: Additional modules concluding the work
  - `Connectivity/`: Network connectivity utilities
  - `Database/`: Database interaction components
  - `GRAFANA/`: Grafana integration for visualization
  - `Mobility/`: Mobile agent mobility features
  - `Test/`: Testing frameworks and utilities

## Technologies Used

- **JADE**: Multi-agent framework
- **Java**: Agent implementation
- **SQLite**: Data storage
- **Python/psutil**: System monitoring
- **GNS3**: Network simulation
- **Docker**: Containerization
- **Grafana**: Visualization (optional)

## Key Features

- **Hierarchical Agent Architecture**: Centralized control with distributed monitoring
- **Mobile Agent Technology**: Dynamic deployment for on-site investigations
- **Real-time Resource Monitoring**: CPU and memory tracking with configurable thresholds
- **Automated Alert System**: Severity-based response with mobile agent dispatch
- **Comprehensive Logging**: SQLite database with anomaly and audit report storage
- **Network Simulation**: GNS3-based testing environment with container integration
- **Modular Design**: Separated concerns for agents, database, and visualization

## Troubleshooting

### Common Issues
- **JADE GUI not starting**: Check DISPLAY and GDK_BACKEND environment variables
- **Database connection failed**: Verify SQLite JDBC driver in classpath
- **Agent communication errors**: Check network connectivity and IP configurations
- **Container access denied**: Ensure proper SSH key setup between containers

### Logs and Debugging
- JADE console output shows agent lifecycle events
- Database queries reveal system state and anomalies
- Network packet capture in GNS3 for communication analysis

## Future Enhancements

- Integration with additional monitoring metrics (disk, network I/O)
- Web-based dashboard for real-time visualization
- Machine learning for anomaly prediction
- Multi-platform agent deployment (beyond Linux containers)
- REST API for external system integration