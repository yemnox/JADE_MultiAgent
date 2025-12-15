// 1. ONE-SHOT: Runs once and finishes
class OneShotBehavior extends OneShotBehaviour {
    public void action() {
        System.out.println("I run once!");
    }
}

// 2. CYCLIC: Runs forever in a loop
class CyclicBehavior extends CyclicBehaviour {
    public void action() {
        System.out.println("I run forever!");
        // Don't forget to block() if no work to do!
    }
}

// 3. TICKER: Runs every X milliseconds
class TickerBehavior extends TickerBehaviour {
    public TickerBehavior(Agent a, long period) {
        super(a, period);
    }
    
    protected void onTick() {
        System.out.println("I run every " + getPeriod() + "ms");
    }
}