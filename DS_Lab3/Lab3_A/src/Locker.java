public class Locker {
    private final Object o;
    Locker(Object o){
        this.o = o;
    }
    public void lock(){
        try {
            o.wait();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
    public void unLock(){
        o.notify();
    }
}
