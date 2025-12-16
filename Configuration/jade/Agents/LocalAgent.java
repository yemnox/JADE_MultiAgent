import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.MessageTemplate; 
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.io.*;

public class LocalAgent extends Agent {
    private double cpuThreshold = 80.0;
    private double memThreshold = 80.0;
    private String nodeID;
    private int alertCount = 0;
    
    protected void setup() {
        nodeID = getLocalName();
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Local Agent: " + nodeID + "                  ║");
        System.out.println("║  CPU Threshold: " + cpuThreshold + "%                ║");
        System.out.println("║  MEM Threshold: " + memThreshold + "%                ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        addBehaviour(new MonitoringBehaviour(this, 5000));
        addBehaviour(new ReceiveResponseBehaviour());
    }
    
    private class MonitoringBehaviour extends TickerBehaviour {
        public MonitoringBehaviour(Agent a, long period) {
            super(a, period);
        }
        
        protected void onTick() {
            double cpuUsage = getCPUUsage();
            double memUsage = getMemoryUsage();
            
            System.out.println("[" + nodeID + "] CPU: " + 
                             String.format("%.1f", cpuUsage) + "% | MEM: " + 
                             String.format("%.1f", memUsage) + "%");
            
            if (cpuUsage > cpuThreshold) {
                String severity = cpuUsage > 95 ? "CRITICAL" : "WARNING";
                sendAlert(severity + " - CPU Overload", cpuUsage);
            }
            
            if (memUsage > memThreshold) {
                sendAlert("WARNING - Memory High", memUsage);
            }
        }
    }
    
    private double getCPUUsage() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "python3", "-c", 
                "import psutil; print(psutil.cpu_percent(interval=1))"
            });
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = reader.readLine();
            p.waitFor();
            
            return line != null ? Double.parseDouble(line.trim()) : 0.0;
        } catch (Exception e) {
            System.err.println("[ERROR] CPU read failed: " + e.getMessage());
            return 0.0;
        }
    }
    
    private double getMemoryUsage() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                "python3", "-c",
                "import psutil; print(psutil.virtual_memory().percent)"
            });
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            String line = reader.readLine();
            p.waitFor();
            
            return line != null ? Double.parseDouble(line.trim()) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private void sendAlert(String type, double value) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("ASC", AID.ISLOCALNAME));
        
        String content = type + ": " + String.format("%.1f", value) + 
                        "% (Threshold: " + cpuThreshold + "%)";
        msg.setContent(content);
        msg.setConversationId("alert-" + System.currentTimeMillis());
        
        send(msg);
        alertCount++;
        
        System.out.println(">>> [" + nodeID + "] ALERT #" + alertCount + 
                         " SENT: " + type);
    }
    
    private class ReceiveResponseBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage msg = receive(mt);
            
            if (msg != null) {
                System.out.println("<<< [" + nodeID + "] ACK: " + msg.getContent());
            } else {
                block();
            }
        }
    }
    
    protected void takeDown() {
        System.out.println("\n[" + nodeID + "] Shutdown - Sent " + 
                         alertCount + " alerts");
    }
}
