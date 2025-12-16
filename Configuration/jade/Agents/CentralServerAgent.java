import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;

public class CentralServerAgent extends Agent {
    private List<String> alertLog = new ArrayList<>();
    private Connection dbConnection;
    private String dbPath = "anomalies.db";
    
    private void initDatabase() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Connect to database
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            System.out.println("[ASC] ✓ Database connected: " + dbPath);
            
            // Test database
            testDatabaseConnection();
            
        } catch (ClassNotFoundException e) {
            System.err.println("[ASC] ✗ SQLite JDBC driver not found");
            System.err.println("[ASC] Make sure sqlite-jdbc.jar is in classpath");
        } catch (SQLException e) {
            System.err.println("[ASC] ✗ Database connection failed: " + e.getMessage());
        }
    }
    
    private void testDatabaseConnection() {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            
            System.out.print("[ASC] Available tables: ");
            while (rs.next()) {
                System.out.print(rs.getString("name") + " ");
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.err.println("[ASC] Database test failed: " + e.getMessage());
        }
    }
    
    
    protected void setup() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Central Server Agent (ASC) Started   ║");
        System.out.println("║  Ready to receive alerts from nodes   ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        //Initialize database
        initDatabase();
        
        // Behavior 1: Receive alerts from Local Agents
        addBehaviour(new ReceiveAlertsBehaviour());
        
        // Behavior 2: Periodic status report
        addBehaviour(new TickerBehaviour(this, 30000) {  // Every 30 seconds
            protected void onTick() {
                System.out.println("\n[ASC STATUS] Total alerts received: " + alertLog.size());
                System.out.println("[ASC STATUS] Monitoring " + getContainerController().getName());
            }
        });
    }
    
    private void logAnomalyToDatabase(String nodeId, String content) {
        try {
            // Parse content to extract values
            String anomalyType = "CPU";
            double value = 0.0;
            double threshold = 80.0;
            String status = "DETECTED";
            
            if (content.contains("CRITICAL")) {
                status = "CRITICAL";
            } else if (content.contains("WARNING")) {
                status = "WARNING";
            }
            
            // Extract value from content (e.g., "95.3%")
            String[] parts = content.split(":");
            if (parts.length > 1) {
                String valuePart = parts[1].trim().split("%")[0];
                try {
                    value = Double.parseDouble(valuePart);
                } catch (NumberFormatException e) {
                    // Use default if parsing fails
                }
            }
            
            // Insert into database
            String sql = "INSERT INTO anomalies (node_id, anomaly_type, value, threshold, status, details) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, nodeId);
            pstmt.setString(2, anomalyType);
            pstmt.setDouble(3, value);
            pstmt.setDouble(4, threshold);
            pstmt.setString(5, status);
            pstmt.setString(6, content);
            
            pstmt.executeUpdate();
            
            System.out.println("[ASC] 💾 Logged to database (ID: " + getLastInsertId() + ")");
            
        } catch (SQLException e) {
            System.err.println("[ASC] ✗ Database logging failed: " + e.getMessage());
        }
    }
    
    private long getLastInsertId() {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            // Ignore
        }
        return -1;
    }
    
    
    private void logAuditReportToDatabase(String targetNode, String fullReport) {
        try {
            // Parse report for key information
            String cpuUsage = extractValue(fullReport, "CPU Usage:");
            String memUsage = extractValue(fullReport, "Memory Usage:");
            String topProcess = extractValue(fullReport, "TOP CPU PROCESSES:");
            String recommendation = extractValue(fullReport, "RECOMMENDATION:");
            
            // Insert into database
            String sql = "INSERT INTO audit_reports " +
                        "(target_node, cpu_usage, memory_usage, top_process, recommendation, full_report) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, targetNode);
            pstmt.setString(2, cpuUsage);
            pstmt.setString(3, memUsage);
            pstmt.setString(4, topProcess);
            pstmt.setString(5, recommendation);
            pstmt.setString(6, fullReport);
            
            pstmt.executeUpdate();
            
            System.out.println("[ASC] 💾 Audit report logged to database (ID: " + getLastInsertId() + ")");
            
        } catch (SQLException e) {
            System.err.println("[ASC] ✗ Audit report logging failed: " + e.getMessage());
        }
    }
    
    private String extractValue(String report, String key) {
        try {
            int startIdx = report.indexOf(key);
            if (startIdx == -1) return "N/A";
            
            startIdx += key.length();
            int endIdx = report.indexOf("\n", startIdx);
            if (endIdx == -1) endIdx = report.length();
            
            return report.substring(startIdx, endIdx).trim();
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    // Behavior to receive alerts
    private class ReceiveAlertsBehaviour extends CyclicBehaviour {
        public void action() {
            // Create message filter for INFORM messages
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);
            
            if (msg != null) {
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
                String 
                sender = msg.getSender().getLocalName();
                String content = msg.getContent();
                
                // Log alert
                String alertEntry = "[" + timestamp + "] " + sender + " -> " + content;
                alertLog.add(alertEntry);
                
                // Log to database
                logAnomalyToDatabase(sender, content);
                
                // Display alert
                System.out.println("\n╔═══════════════ ALERT RECEIVED ═══════════════╗");
                System.out.println("║ FROM: " + sender);
                System.out.println("║ TIME: " + timestamp);
                System.out.println("║ DATA: " + content);
                System.out.println("╚══════════════════════════════════════════════╝");
                
                // Check if this is an audit report
                if (content.startsWith("AUDIT_REPORT")) {
                    String reportContent = content.substring("AUDIT_REPORT\n".length());
                    logAuditReportToDatabase(sender, reportContent);  // ← ADD THIS LINE
                } else {
                    // Regular anomaly alert
                    logAnomalyToDatabase(sender, content);
                }
                
                // Decide on action
                if (content.contains("CRITICAL") || content.contains("95")) {
                    System.out.println("[ASC ACTION] Severity HIGH - Dispatching audit agent to " + sender);
                    // TODO: Send Mobile Audit Agent => Done
                    deployAuditAgent(sender);
                } else {
                    System.out.println("[ASC ACTION] Severity MEDIUM - Logged for monitoring");
                }
                
                // Send acknowledgment back to Local Agent
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.setContent("Alert received and logged");
                send(reply);
                
            } else {
                block();  // Wait for next message
            }
        }
    }
    
    protected void takeDown() {
        System.out.println("\n[ASC] Shutting down. Total alerts processed: " + alertLog.size());
        
        // Print all alerts before shutdown
        System.out.println("\n═══════════ ALERT SUMMARY ═══════════");
        for (String alert : alertLog) {
            System.out.println(alert);
        }
        System.out.println("═══════════════════════════════════════\n");
        
        // Close database connection
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
                System.out.println("[ASC] ✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("[ASC] Error closing database: " + e.getMessage());
        }
        
    }
    private void deployAuditAgent(String targetNode) {
        try {
            System.out.println("\n[ASC] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("[ASC] 🚨 CRITICAL alert threshold exceeded");
            System.out.println("[ASC] 🔍 Deploying Mobile Audit Agent...");
            System.out.println("[ASC] 🎯 Target: " + targetNode);
            System.out.println("[ASC] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // Create Mobile Audit Agent
            jade.wrapper.AgentController amaController = 
                getContainerController().createNewAgent(
                    "AMA-" + targetNode + "-" + System.currentTimeMillis(),
                    "MobileAuditAgent",
                    new Object[]{targetNode}
                );
            
            amaController.start();
            
            System.out.println("[ASC] ✓ Mobile Audit Agent deployed successfully\n");
            
        } catch (Exception e) {
            System.err.println("[ASC] ✗ Failed to deploy Mobile Audit Agent");
            System.err.println("[ASC] Error: " + e.getMessage());
        }
    }
}
