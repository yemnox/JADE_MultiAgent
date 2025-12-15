import jade.core.Agent;
import jade.core.behaviours.*;

public class MyAgent extends Agent {
    
    // 1. SETUP - Called when agent is born
    protected void setup() {
        System.out.println("Hello! I'm agent " + getLocalName());
        
        // Add behaviors (tasks the agent will do)
        addBehaviour(new MyBehaviour());
    }
    
    // 2. TAKEDOWN - Called when agent dies
    protected void takeDown() {
        System.out.println("Goodbye! Agent " + getLocalName() + " terminating.");
    }
}