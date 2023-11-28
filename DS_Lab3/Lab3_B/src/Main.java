import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Salon{
    private final Barber barber = new Barber();
    private final Semaphore semaphore = new Semaphore(1);
    private volatile ArrayList<Customer> queue = new ArrayList<>(10);
    class Barber implements Runnable{
        Customer currentCustomer = null;
        volatile boolean isBusy = false;
        volatile boolean isSleeping = true;
        void awakeCustomer(Customer customer){
            customer.wakeUp();
        }
        synchronized void wakeUp(){
            System.out.println("Barber waked up");
            isSleeping = false;
        }
        void goSleep(){
            System.out.println("Barber go sleep");
            isSleeping = true;
        }
        void makeHairCut(Customer customer){
            try {
                isBusy = true;
                System.out.println("Start making haircut for customer " + customer.index);
                customer.goSleep();
                Thread.sleep(customer.haircutDuration);
                System.out.println("Haircut is done");
                customer.haircutIsDone = true;
                customer.wakeUp();
                isBusy = false;
                semaphore.release();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        @Override
        public void run() {
            while (true){
                if(!isSleeping){
                    if (currentCustomer == null) {
                        if (queue.isEmpty()) {
                            goSleep();
                            continue;
                        }
                        else {
                            currentCustomer = queue.remove(0);
                            currentCustomer.wakeUp();
                        }
                    }
                    makeHairCut(currentCustomer);
                    currentCustomer = null;
                }
            }
        }
    }
    class Customer implements Runnable{
        int haircutDuration;
        final int index;
        volatile boolean haircutIsDone = false;
        Customer(int haircutDuration, int index){
            this.haircutDuration = haircutDuration;
            this.index = index;
        }
        void wakeBarberUp(){
            barber.isSleeping = false;
        }
        void wakeUp(){
            System.out.println("Customer " + index + " waked up");
        }
        void goSleep(){
            System.out.println("Customer " + index + " go sleep");
        }
        @Override
        public void run() {
            while(!haircutIsDone){
                if(barber.isSleeping){
                    if(semaphore.tryAcquire()) {
                        barber.currentCustomer = this;
                        queue.remove(0);
                        barber.wakeUp();
                    }
                }
            }
        }
    }
    public void start(){
        Random r = new Random();
        Thread t1 = new Thread(barber);
        t1.start();
        int iterator = 1;
        while(true){
            if(queue.size() < 10) {
                try {
                    Thread.sleep(r.nextInt(500) + 200);
                    Customer temp = new Customer(r.nextInt(300) + 200, iterator);
                    Thread t2 = new Thread(temp);
                    queue.add(temp);
                    t2.start();
                    System.out.println("Customer " + iterator + " added to the queue");
                    iterator++;
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }

        }
    }
}
public class Main {
    public static void main(String[] args) {
        Salon s = new Salon();
        s.start();
    }
}