public class Pot {
    private final int capacity;
    private int currentCount;
    private final Locker locker;
    private volatile boolean isEmpty = true;
    public Pot(int capacity){
        this.capacity = capacity;
        this.currentCount = 0;
        this.locker = new Locker(this);
    }
    public synchronized void bringHoney(Bee bee){
        while (!isEmpty) {
            locker.lock();
        }
        isEmpty = false;
        try {
            wait(500);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        currentCount++;
        System.out.println("Bee " + bee.getIndex() + " brought honey. Current count: " + currentCount);
        isEmpty = !isFull();
        locker.unLock();
    }
    public void eatHoney(){
        //currentCount--;
        currentCount = 0;
        System.out.println("Winny aet honey. Current count: " + currentCount);
        isEmpty = true;
    }
    public boolean isEmpty(){
        return currentCount == 0;
    }
    public boolean isFull(){
        return currentCount == capacity;
    }
}
