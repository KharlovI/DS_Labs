import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

enum Type{
    adderRemover,
    telephone,
    name
}
class CustomReadWriteLock {
    private int readers = 0;
    private int writers = 0;

    public synchronized void acquireReadLock(){
        while (writers > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        readers++;
    }

    public synchronized void releaseReadLock() {
        readers--;
        notifyAll();
    }

    public synchronized void acquireWriteLock(){
        writers++;
        while (readers > 0 || writers > 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void releaseWriteLock() {
        writers--;
        notifyAll();
    }
}
class Manager{
    Random r = new Random();
    private volatile ArrayList<String> names = new ArrayList<>();
    private volatile ArrayList<String> telephoneNumbers = new ArrayList<>();
    private int writerCount = 0;
    private String fileUrl = "C:\\Users\\Иван\\Desktop\\OS\\OS_Lab4\\Lab4_A\\src\\DB.txt";
    private File file = new File(fileUrl);
    private String findName(String number){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp = reader.readLine();
            while(temp != null){
                String name = temp.substring(0, temp.indexOf(" "));
                temp = temp.substring(temp.indexOf(" ")+1);
                for(int i = 0; i < 2; i++){
                    name += " ";
                    name += temp.substring(0, temp.indexOf(" "));
                    temp = temp.substring(temp.indexOf(" ")+1);
                }
                if(number.equals(temp))
                    return name;
                temp = reader.readLine();
            }
            reader.close();
            return "There are no writer with this TN: " + number;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String findTelephone(String name){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp = reader.readLine();
            while(temp != null){
                String currentName = temp.substring(0, temp.indexOf(" "));
                temp = temp.substring(temp.indexOf(" ")+1);
                for(int i = 0; i < 2; i++){
                    currentName += " ";
                    currentName += temp.substring(0, temp.indexOf(" "));
                    temp = temp.substring(temp.indexOf(" ")+1);
                }
                if(name.equals(currentName))
                    return temp;
                temp = reader.readLine();
            }
            reader.close();
            return "There are no writer with this name: " + name;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void removeString(int index){
        names.remove(index);
        telephoneNumbers.remove(index);
    }
    private void addString(String name, String number){
        names.add(name);
        telephoneNumbers.add(number);
    }
    public Manager(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp = reader.readLine();
            while(temp != null){
                String name = temp.substring(0, temp.indexOf(" "));
                temp = temp.substring(temp.indexOf(" ")+1);
                for(int i = 0; i < 2; i++){
                    name += " ";
                    name += temp.substring(0, temp.indexOf(" "));
                    temp = temp.substring(temp.indexOf(" ")+1);
                }
                names.add(name);
                telephoneNumbers.add(temp);
                temp = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Reader r1 = new Reader(Type.adderRemover);
        Reader r2 = new Reader(Type.name);
        Reader r3 = new Reader(Type.telephone);

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);

        t1.start();
        t2.start();
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    class Reader implements Runnable{
        private Type readerType;
        private CustomReadWriteLock lock = new CustomReadWriteLock();

        public Reader(Type type){
            readerType = type;
        }
        @Override
        public void run() {
             int iterator1 = 0;
             int iterator2 = 0;
            while(true) {
                switch (readerType) {
                    case name -> {
                        if(!names.isEmpty()) {
                            lock.acquireReadLock();
                            String name = randomName();
                            System.out.println("TN is equal: " + findTelephone(name));
                            lock.releaseReadLock();
                            try {
                                Thread.sleep(150);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    case telephone -> {
                        if(!telephoneNumbers.isEmpty()) {
                            lock.acquireReadLock();
                            String telephone = randomTN();
                            System.out.println("Name is : " + findName(telephone));
                            lock.releaseReadLock();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    case adderRemover -> {
                        lock.acquireWriteLock();
                        System.out.println(names.size());
                        if(iterator2 > 0 && iterator2 %  5 == 0){
                            removeString(r.nextInt(names.size()));
                            writeToFile();

                        }
                        else{
                            String name = "Прізвище"+iterator1 + " Ім'я"+iterator1 + " Батькові" + iterator1;
                            String num = String.valueOf(iterator1);
                            addString(name, num);
                            writeToFile();
                            iterator1++;
                        }
                        iterator2++;
                        lock.releaseWriteLock();
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
    String randomName(){
        int nameCount = names.size();
        return names.get(r.nextInt(nameCount));
    }
    String randomTN(){
        int tnCount = telephoneNumbers.size();
        return telephoneNumbers.get(r.nextInt(tnCount));
    }
    private void writeToFile(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for(int i = 0; i < names.size(); i++){
                writer.write(names.get(i) + " " + telephoneNumbers.get(i) +"\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
    }
}