import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Car{
    private Majno m;
    private boolean timeToBring = true;

    public void bringMajno(Majno m) throws InterruptedException {
        assert(m != null);
        while(!timeToBring){
            wait();
        }
        this.m = m;
        timeToBring = false;
        notify();
    }

    public void evaluateMajno() throws InterruptedException {
        assert(m != null);
        while(timeToBring){
            wait();
        }
        System.out.println("The value of Majno is: " + m.evaluate());
        timeToBring = true;
        notify();
    }
}
class Outside{
    private Majno m;
    private boolean timeToPut = true;
    public synchronized void putMajno(Majno m) throws InterruptedException {
        assert (m != null);
        while (!timeToPut){
            wait();
        }
        this.m = m;
        timeToPut = false;
        notify();
    }
    public synchronized Majno getMajno() throws InterruptedException {
        assert(m != null);
        while (timeToPut){
            wait();
        }
        Majno temp = m;
        m = null;
        notify();
        return temp;
    }
}
class Storage{
    private List<Majno> majnos;

    public Storage(int count){
        majnos = new ArrayList<>(count);
    }

    public Majno getMajno(){
        assert(!majnos.isEmpty());
        return majnos.remove(0);
    }

    public boolean storageIsEmpty(){
        System.out.println("The storage is empty :(");
        return majnos.isEmpty();
    }
}
class Majno {
    private float value;
    public Majno(){
        Random r = new Random();
        value = r.nextFloat(1000);
    }

    float evaluate(){
        return value;
    }
}
class Ivanov implements Runnable{
    private Storage storage;
    private Outside outside;
    public Ivanov(Storage s, Outside o){
        storage = s;
        outside = o;
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        while(!storage.storageIsEmpty()){
            try {
                Majno majno = storage.getMajno();
                outside.putMajno(majno);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Petrov implements Runnable{

    Outside outside;
    Car car;

    public Petrov(Outside o, Car c){
        outside = o;
        car = c;
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        while(true){
            try {
                Majno majno = outside.getMajno();
                car.bringMajno(majno);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Nechuporchuk implements Runnable{
    Car car;
    public Nechuporchuk(Car c){
        car = c;
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        while(true){
            try {
                car.evaluateMajno();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
public class Main {
    Storage s = new Storage(1000);
    Outside o = new Outside();
    Car c = new Car();

    Ivanov ivanov = new Ivanov(s,o);
    Petrov petrov = new Petrov(o,c);
    Nechuporchuk nechuporchuk = new Nechuporchuk(c);
}
