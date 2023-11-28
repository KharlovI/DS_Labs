import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Field {
    private MyRunnable[] threads = new MyRunnable[4];
    private final CyclicBarrier barrier = new CyclicBarrier(4);
    public Field(int stringLength){
        for(int i = 0; i < 4; i++){
            threads[i] = new MyRunnable(barrier, stringLength, i);
            Thread t = new Thread(threads[i]);
            t.setDaemon(true);
            t.start();
        }
        while(true){
            if(barrier.getNumberWaiting() == 0){
                if(isEqual()){
                    for(int i = 0; i < 4; i++){
                        threads[i].flag = true;
                    }
                    return;
                }
            }
        }
    }
    private boolean isEqual(){
        int[] t1 = threads[0].getCount();
        int[] t2 = threads[1].getCount();
        int[] t3 = threads[2].getCount();
        int[] t4 = threads[3].getCount();
        return isEqual(t1,t2,t3) || isEqual(t1,t2,t4) || isEqual(t1,t3,t4) || isEqual(t2,t3,t4);
    }
    private boolean isEqual(int[] values1, int[] values2, int[] values3){
        if(values1[0] == values2[0] && values1[0] == values3[0]){
            return (values1[1] == values2[1] && values1[1] == values3[1]);
        }
        return false;
    }
}
class MyRunnable implements Runnable{
    private final int stringLength;
    private final int index;
    private final CyclicBarrier barrier;
    private String string = new String();
    public volatile boolean flag = false;
    public MyRunnable(CyclicBarrier barrier, int stringLength, int index){
        this.index = index;
        this.stringLength = stringLength;
        this.barrier = barrier;
        generateRandomString();
    }
    @Override
    public void run() {
        while(!flag){
            changeLetter();
            System.out.println("Thread " + index + " " + string);
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(10 * index);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void generateRandomString(){
        Random random = new Random();
        for(int i = 0; i < stringLength; i++){
            switch (random.nextInt(4)){
                case 0->{
                    string += "A";
                }
                case 1->{
                    string += "B";
                }
                case 2->{
                    string += "C";
                }
                case 3->{
                    string += "D";
                }
            }
        }
    }
    private int findFirstIndex(char letter){
        char[] chars = string.toCharArray();
        for(int i = 0; i < this.stringLength; i++){
            if(chars[i] == letter)
                return i;
        }
        return -1;
    }
    private void changeLetter(){
        Random random = new Random();
        switch (random.nextInt(4)){
            case 0->{
                int index = findFirstIndex('A');
                if(index != -1){
                    char[] chars = string.toCharArray();
                    chars[index] = 'C';
                    string = String.valueOf(chars);
                }
                else {
                    index = findFirstIndex('C');
                    if(index != -1) {
                        char[] chars = string.toCharArray();
                        chars[index] = 'A';
                        string = String.valueOf(chars);
                    }
                }
            }
            case 1->{
                int index = findFirstIndex('B');
                if(index != -1){
                    char[] chars = string.toCharArray();
                    chars[index] = 'D';
                    string = String.valueOf(chars);
                }
                else {
                    index = findFirstIndex('D');
                    if(index != -1) {
                        char[] chars = string.toCharArray();
                        chars[index] = 'B';
                        string = String.valueOf(chars);
                    }
                }
            }
            case 2->{
                int index = findFirstIndex('C');
                if(index != -1){
                    char[] chars = string.toCharArray();
                    chars[index] = 'A';
                    string = String.valueOf(chars);
                }
                else {
                    index = findFirstIndex('A');
                    if(index != -1) {
                        char[] chars = string.toCharArray();
                        chars[index] = 'C';
                        string = String.valueOf(chars);
                    }
                }
            }
            case 3->{
                int index = findFirstIndex('D');
                if(index != -1){
                    char[] chars = string.toCharArray();
                    chars[index] = 'B';
                    string = String.valueOf(chars);
                }
                else {
                    index = findFirstIndex('B');
                    if(index != -1) {
                        char[] chars = string.toCharArray();
                        chars[index] = 'D';
                        string = String.valueOf(chars);
                    }
                }
            }
        }
    }
    public int[] getCount(){
        int[] answer = new int[2];
        for(int i = 0; i < 2; i++){
            answer[i] = 0;
        }
        char[] str = string.toCharArray();
        for(int i = 0; i < stringLength; i++){
            switch (str[i]){
                case 'A'->{
                    answer[0]++;
                }
                case 'B'->{
                    answer[1]++;
                }
            }
        }
        return answer;
    }
}
public class Main {
    public static void main(String[] args) {
       Field field = new Field(6);
    }
}