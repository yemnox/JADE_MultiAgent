import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;
import java.text.SimpleDateFormat;

public class CentralServerAgent extends Agent {
    private List<String> alertLog = new ArrayList<>();
    
    protected void setup() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Central Server Agent (ASC) Started   ║");
        System.out.println("║  Ready to receive alerts from nodes   ║");
        System.out.println("╚════════════════════════════════════════╝");
        
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
    
    // Behavior to receive alerts
    private class ReceiveAlertsBehaviour extends CyclicBehaviour {
        public void action() {
            // Create message filter for INFORM messages
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);
            
            if (msg != null) {
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String sender = msg.getSender().getLocalName();
                String content = msg.getContent();
                
                // Log alert
                String alertEntry = "[" + timestamp + "] " + sender + " -> " + content;
                alertLog.add(alertEntry);
                
                // Display alert
                System.out.println("\n╔═══════════════ ALERT RECEIVED ═══════════════╗");
                System.out.println("║ FROM: " + sender);
                System.out.println("║ TIME: " + timestamp);
                System.out.println("║ DATA: " + content);
                System.out.println("╚══════════════════════════════════════════════╝");
                
                // Decide on action
                if (content.contains("CRITICAL") || content.contains("95")) {
                    System.out.println("[ASC ACTION] Severity HIGH - Dispatching audit agent to " + sender);
                    // TODO: Send Mobile Audit Agent
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
    }
}