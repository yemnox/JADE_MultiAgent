// SENDING a message
ACLMessage msg = new ACLMessage(ACLMessage.INFORM);  // Message type
msg.addReceiver(new AID("ASC", AID.ISLOCALNAME));    // Who to send to
msg.setContent("CPU alert: 85%");                     // Message body
msg.setConversationId("monitoring");                  // Optional: thread ID
send(msg);

// RECEIVING a message
ACLMessage msg = receive();  // Non-blocking
if (msg != null) {
    String content = msg.getContent();
    String sender = msg.getSender().getLocalName();
    System.out.println("Received from " + sender + ": " + content);
} else {
    block();  // Wait for next message
}