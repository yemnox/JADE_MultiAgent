import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.io.*;
import java.util.*;

public class MobileAuditAgent extends Agent {
    private String targetNode;
    private String auditReport = "";
    
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetNode = (String) args[0];
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  Mobile Audit Agent Started           ║");
            System.out.println("║  Target: " + targetNode + "                         ║");
            System.out.println("║  Mission: Investigate anomaly         ║");
            System.out.println("╚════════════════════════════════════════╝\n");
            
            // Start investigation
            addBehaviour(new InvestigationBehaviour());
        } else {
            System.err.println("[AMA] Error: No target node specified");
            doDelete();
        }
    }
    
    private class InvestigationBehaviour extends OneShotBehaviour {
        public void action() {
            System.out.println("[AMA] Starting investigation on " + targetNode + "...");
            
            // Collect evidence
            String processInfo = getTopProcesses();
            String cpuInfo = getCurrentCPU();
            String memInfo = getMemoryInfo();
            String networkInfo = getNetworkConnections();
            
            // Build audit report
            auditReport = buildReport(processInfo, cpuInfo, memInfo, networkInfo);
            
            // Display report
            System.out.println("\n" + auditReport);
            
            // Send report to ASC
            sendReportToASC();
            
            // Self-terminate after completing mission
            System.out.println("[AMA] Mission complete. Self-terminating...\n");
            doDelete();
        }
    }
    
    private String getTopProcesses() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "sh", "-c", 
                "ps aux --sort=-%cpu | head -8 | awk '{print $11, $3\"%\"}'"
            });
            
            return readProcessOutput(p);
        } catch (Exception e) {
            return "Unable to retrieve process info: " + e.getMessage();
        }
    }
    
    private String getCurrentCPU() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "python3", "-c",
                "import psutil; print(f'{psutil.cpu_percent(interval=1):.1f}%')"
            });
            
            return readProcessOutput(p).trim();
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    private String getMemoryInfo() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "python3", "-c",
                "import psutil; m=psutil.virtual_memory(); print(f'{m.percent:.1f}% ({m.used//1024//1024}MB used)')"
            });
            
            return readProcessOutput(p).trim();
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    private String getNetworkConnections() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "sh", "-c",
                "ss -tuln 2>/dev/null | grep LISTEN | wc -l"
            });
            
            String count = readProcessOutput(p).trim();
            return count + " listening ports";
        } catch (Exception e) {
            return "Unable to retrieve network info";
        }
    }
    
    private String readProcessOutput(Process p) {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            p.waitFor();
            
            return result.toString();
        } catch (Exception e) {
            return "Error reading output: " + e.getMessage();
        }
    }
    
    private String buildReport(String processes, String cpu, String mem, String network) {
        StringBuilder report = new StringBuilder();
        
        report.append("╔════════════════════════════════════════════════════╗\n");
        report.append("║           AUDIT REPORT - ").append(targetNode).append("              ║\n");
        report.append("╠════════════════════════════════════════════════════╣\n");
        report.append("║ Timestamp: ").append(new Date()).append("\n");
        report.append("║ \n");
        report.append("║ SYSTEM METRICS:\n");
        report.append("║   CPU Usage:    ").append(cpu).append("\n");
        report.append("║   Memory Usage: ").append(mem).append("\n");
        report.append("║   Network:      ").append(network).append("\n");
        report.append("║ \n");
        report.append("║ TOP CPU PROCESSES:\n");
        for (String line : processes.split("\n")) {
            if (!line.trim().isEmpty()) {
                report.append("║   ").append(line).append("\n");
            }
        }
        report.append("║ \n");
        report.append("║ ANALYSIS:\n");
        
        // Determine severity and recommendation
        if (processes.toLowerCase().contains("stress-ng")) {
            report.append("║   ⚠ MALICIOUS ACTIVITY DETECTED\n");
            report.append("║   stress-ng process consuming resources\n");
            report.append("║ \n");
            report.append("║ RECOMMENDATION:\n");
            report.append("║   → Terminate stress-ng process\n");
            report.append("║   → Monitor for recurrence\n");
            report.append("║   → Investigate user activity\n");
        } else if (cpu.contains("9") || cpu.contains("100")) {
            report.append("║   ⚠ HIGH CPU USAGE DETECTED\n");
            report.append("║   No obvious malicious process\n");
            report.append("║ \n");
            report.append("║ RECOMMENDATION:\n");
            report.append("║   → Review running processes\n");
            report.append("║   → Check for legitimate workload\n");
            report.append("║   → Consider resource allocation\n");
        } else {
            report.append("║   ℹ ANOMALY RESOLVED\n");
            report.append("║   CPU usage returned to normal\n");
            report.append("║ \n");
            report.append("║ RECOMMENDATION:\n");
            report.append("║   → Continue monitoring\n");
            report.append("║   → No immediate action required\n");
        }
        
        report.append("╚════════════════════════════════════════════════════╝");
        
        return report.toString();
    }
    
    private void sendReportToASC() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("ASC", AID.ISLOCALNAME));
        msg.setContent("AUDIT_REPORT\n" + auditReport);
        msg.setConversationId("audit-" + System.currentTimeMillis());
        
        send(msg);
        System.out.println("[AMA] ✓ Report sent to ASC");
    }
    
    protected void takeDown() {
        System.out.println("[AMA] Agent terminated.\n");
    }
}
