public class Winny implements Runnable {
    private volatile boolean isSleeping = true;
    private final Pot pot;
    private final Locker locker;

    public Winny(Pot pot){
        this.pot = pot;
        this.locker = new Locker(this);
    }
    public void Eat(){
        pot.eatHoney();
    }
    public synchronized void wakeUp(int beeIndex){
        System.out.println("Bee " + beeIndex + " wake Winny Up!");
        this.isSleeping = false;
    }
    private void sleep(){
        this.isSleeping = true;
    }
    public boolean isSleeping(){
        return isSleeping;
    }
    @Override
    public void run() {
        while(true){
            if(isSleeping)
                continue;
            while(!pot.isEmpty()){
                Eat();
            }
            sleep();
        }
    }
}
