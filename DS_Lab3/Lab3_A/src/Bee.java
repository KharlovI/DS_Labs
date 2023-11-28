public class Bee implements Runnable {
    private final int index;
    private final Pot pot;
    private final Winny winny;
    public Bee(int index, Pot pot, Winny winny){
        this.index = index;
        this.pot = pot;
        this.winny = winny;
    }
    public void bringHoney(){
        pot.bringHoney(this);
    }
    public int getIndex(){
        return index;
    }
    @Override
    public void run() {
        while(true){
            while(!winny.isSleeping()){

            }
            bringHoney();
            if (pot.isFull()) {
                winny.wakeUp(index);
            }
        }
    }
}
