import java.io.*;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

enum State{
    good,
    withered,
    dead
}
class FileLogger implements Runnable{
    private final String filePath = "C:\\Users\\Иван\\Desktop\\OS\\OS_Lab4\\Lab4_B\\src\\Garden.txt";
    private final Thread thread;
    private final File file = new File(filePath);
    private final Garden garden;
    public FileLogger(Garden garden){
        this.garden = garden;
        this.thread = new Thread(this);
        thread.start();
    }
    private void log(){
        final int size = garden.getSize();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            for(int i = 0; i < size; i++){
                String temp = "";
                for(int j = 0; j < size; j++){
                    temp += garden.parse(garden.getState(i,j));
                    temp += " ";
                }
                writer.write(temp + "\n");
            }
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        while(true){
            log();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
class ConsoleLogger implements Runnable{
    private final Garden garden;
    private final Thread thread;
    public ConsoleLogger(Garden garden){
        this.garden = garden;
        this.thread = new Thread(this);
        thread.start();
    }
    private void log(){
        final int size = garden.getSize();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                System.out.print(garden.parse(garden.getState(i,j)));
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }
    @Override
    public void run() {
        while(true){
            log();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
class Gardener implements Runnable{
    private final Garden garden;
    private final Thread thread;
    public Gardener(Garden garden){
        this.garden = garden;
        this.thread = new Thread(this);
        this.thread.start();
    }
    @Override
    public void run() {
        final int size = garden.getSize();
        Random r = new Random();
        while(true){
            int i = r.nextInt(garden.getSize());
            int j = r.nextInt(garden.getSize());

            if(garden.flowerNeedWater(i,j)) {
                waterTheFlower(i, j);
            }
        }
    }

    private void waterTheFlower(int i, int j){
        garden.changeState(i,j, State.good);
    }

}
class Weather implements Runnable{
    private final Thread thread;
    private final Garden garden;

    public Weather(Garden garden){
        this.thread = new Thread(this);
        this.garden = garden;
        thread.start();
    }

    private void hitTheFlower(int i, int j){
        garden.changeState(i, j, State.withered);
    }
    private void killTheFlower(int i, int j){
        garden.changeState(i, j, State.dead);
    }
    private void waterTheFlower(int i, int j){
        garden.changeState(i, j, State.good);
    }
    @Override
    public void run() {
        Random r = new Random();
        while(true){
            int i = r.nextInt(garden.getSize());
            int j = r.nextInt(garden.getSize());

            int randomValue = r.nextInt(30);

            hitTheFlower(i, j);


        }
    }
}
class Garden{
    public int parse(State state){
        if(state == State.good)
            return 1;
        else if(state == State.withered)
            return 0;
        return -1;
    }
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile State[][] flowers;
    private final  int size;
    public Garden(int size){
        this.size = size;
        flowers = new State[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                flowers[i][j] = State.good;
            }
        }
    }
    public synchronized void changeState(int i, int j, State state){
        lock.writeLock().lock();
        if(flowers[i][j] != State.dead)
            flowers[i][j] = state;
        lock.writeLock().unlock();
    }
    public State getState(int i, int j){
        return flowers[i][j];
    }
    public boolean flowerNeedWater(int i, int j){
        return flowers[i][j] == State.withered;
    }
    public int getSize(){
        return size;
    }
}
public class Main {
    public static void main(String[] args) {
        Garden garden = new Garden(10);
        Gardener gardener = new Gardener(garden);
        Weather weather = new Weather(garden);
        ConsoleLogger consoleLogger = new ConsoleLogger(garden);
        FileLogger fileLogger = new FileLogger(garden);
    }
}