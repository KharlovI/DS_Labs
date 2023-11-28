import java.util.Arrays;
import java.util.Random;

class Manager{
    private MyRunnable[] threads;
    private Recruit[] recruits;
    private Blocker blocker;
    private int recruitsCount = 200;
    private int threadsCount = 4;
    public Manager(){
        this.blocker = new Blocker(threadsCount);
        recruits = new Recruit[recruitsCount];
        this.threads = new MyRunnable[threadsCount];
        Random r = new Random();
        char mainDirection;

        if(r.nextInt(2) == 0)
            mainDirection = 'L';
        else
            mainDirection = 'R';

        for(int i = 0; i < recruitsCount; i++){
            recruits[i] = new Recruit(mainDirection, i);
        } // Initialize recruits
        for(int i = 0; i < recruitsCount; i++){
            if(i == 0){
                recruits[i].setLeft(null);
                recruits[i].setRight(recruits[i+1]);
            }
            else if(i == recruitsCount - 1){
                recruits[i].setLeft(recruits[i-1]);
                recruits[i].setRight(null);
            }
            else{
                recruits[i].setLeft(recruits[i-1]);
                recruits[i].setRight(recruits[i+1]);
            }
        } // set neighbour

        final int batchSize = recruitsCount / threadsCount;
        for(int i = 0; i < threadsCount; i++){
            this.threads[i] = new MyRunnable(blocker, Arrays.copyOfRange(recruits, i * batchSize, (i + 1) * batchSize ));
            Thread t = new Thread(this.threads[i]);
            t.start();
        }
        while(true){
            if(blocker.await(true)){
                for(int i = 0; i < recruitsCount; i++ ){
                    System.out.print(recruits[i].getDirection() + " ");
                }
                System.out.println();
                System.out.println();
                if(allIsCorrect()) {
                    System.out.println("All are correct!!");
                    for (int i = 0; i < threadsCount; i++) {
                        this.threads[i].flag = true;
                    }
                    return;
                }
            }

        }
    }
    private boolean allIsCorrect(){
       for(int i = 0; i < this.recruitsCount; i++){
           if(!recruits[i].isCorrect())
               return false;
       }
        return true;
    }
}
class Blocker{
    private volatile int freeSpaces;
    private final int capacity;
    private volatile boolean noSpace = false;
    public Blocker(int capacity){
        this.capacity = capacity;
        this.freeSpaces = capacity;
    }
    public synchronized boolean await(boolean main){
        if(main) {
            if(freeSpaces == 0) {
                noSpace = true;
                notifyAll();
                freeSpaces = capacity;
                return true;
            }
            noSpace = false;
            return false;
        }
        freeSpaces--;
        if(freeSpaces < 0){
            System.out.println("Oj, there are no space in blocker (");
            freeSpaces++;
            return false;
        }
        while(!noSpace){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}

class MyRunnable implements Runnable{
    private Recruit[] recruits;
    private final Blocker blocker;
    public boolean allAreCorrect = false;
    public volatile boolean flag = false;
    public MyRunnable(Blocker blocker, Recruit[] recruits){
        this.blocker = blocker;
        this.recruits = recruits;
    }
    @Override
    public void run() {
        while(!flag){
            for(int i = 0; i < recruits.length; i++){
                if(recruits[i].index == 0) {
                    if(!recruits[i].isCorrect()){
                        if(recruits[i].getDirection() == 'R') {
                            recruits[i].getRight().rotate();
                        }
                        recruits[i].rotate();
                        allAreCorrect = false;
                    }
                }
                else if(recruits[i].index == 199){
                    if(!recruits[i].isCorrect()){
                        if(recruits[i].getDirection() == 'L') {
                            recruits[i].getLeft().rotate();
                        }
                        recruits[i].rotate();
                        allAreCorrect = false;
                    }
                }
                else {
                    if (!recruits[i].isCorrect()) {
                        if (recruits[i].getDirection() == 'L') {
                            recruits[i].getLeft().rotate();
                        } else {
                            recruits[i].getRight().rotate();
                        }
                        recruits[i].rotate();
                        allAreCorrect = false;
                    }
                }
            }
            blocker.await(false);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
class Recruit{
    private Recruit left;
    private Recruit right;
    private char direction;
    public final int index;
    private final char commandDirection;
    public Recruit(char commandDirection, int index){
        this.commandDirection = commandDirection;
        this.index = index;
        randomRotate();
    }
    public void setLeft(Recruit recruit){
        left = recruit;
    }
    public void setRight(Recruit recruit){
        right = recruit;
    }
    public void rotate(){
        switch (direction) {
            case 'L' -> {
                direction = 'R';
            }
            case 'R' -> {
                direction = 'L';
            }
        }
    }
    public boolean isCorrect() {
        switch (direction){
            case 'L' ->{
                if(left == null){
                    return commandDirection == 'L';
                }
                return left.direction == 'L';
            }
            case 'R' ->{
                if(right == null){
                    return commandDirection == 'R';
                }
                return right.direction == 'R';
            }
        }
        System.out.println("WTF?");
        return false;
    }
    public char getDirection(){
        return direction;
    }
    public void randomRotate(){
        Random r = new Random();
        if(r.nextInt(2) == 0){
            direction = 'L';
        }
        else{
            direction = 'R';
        }
    }
    public Recruit getLeft(){
        return left;
    }
    public Recruit getRight(){
        return right;
    }
}
public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();
    }
}