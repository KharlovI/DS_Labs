import java.util.Collection;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
class Sector {
    private int index;
    private boolean isChecked = false;
    private boolean winniIsHear = false;

    public Sector(int index) {
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
    public void setWinni(){
        winniIsHear = true;
    }
    public boolean checkThisSector(){
        this.isChecked = true;
        return this.winniIsHear;
    }
}

class Forest{
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Sector[] sectors;
    private int size;
    private boolean winniIsLocated = false;
    public Forest(int countOfSectors){
        sectors = new Sector[countOfSectors];
        for(int i = 0; i < countOfSectors; i++){
            sectors[i] = new Sector(i);
        }
        this.size = countOfSectors;
    }
    public void setWinni(int index){
        assert (index < size);
        assert (index > -1);

        if(winniIsLocated){
            System.out.println("Winni is already located");
            return;
        }
        winniIsLocated = true;
        sectors[index].setWinni();
    }
    public boolean checkSector(int groupIndex){
        int index = getRandom();
        Sector temp;
        if(lock.writeLock().tryLock()){
            temp = sectors[index];
            sectors[index] = sectors[size - 1];
            sectors[size - 1] = temp;
            size -= 1;
        }
        else{
            int oldIndex = index;
            index = getRandom();
            assert(oldIndex != index);
            temp = sectors[index];
            sectors[index] = sectors[size - 1];
            sectors[size - 1] = temp;
            size -= 1;
        }

        final boolean winniIsHere = temp.checkThisSector();

        if(winniIsHere){
            System.out.println("Winni was found in sector " + temp.getIndex() + " by group: " + groupIndex);
        }
        else{
            System.out.println("Sector " + temp.getIndex() + " was empty." + "Group: " + groupIndex + " returned");
        }
        return winniIsHere;
    }

    public int getRandom(){
        Random r = new Random();
        return r.nextInt(size);
    }
    public Sector getRandomFreeSector(){
            lock.writeLock().lock();
            Random r = new Random();
            int index = r.nextInt(size);
            Sector temp = sectors[index];
            sectors[index] = sectors[size - 1];
            sectors[size - 1] = temp;
            size -= 1;
            lock.writeLock().unlock();
            return temp;
    }
}

class Portfolio{
    private Thread[] tasks;
    private Forest forest;
    public Portfolio(int countOfGroups, int countOfSectors){
        tasks = new Thread[countOfGroups];
        forest = new Forest(countOfSectors);
        Random r = new Random();
        int index = r.nextInt(countOfSectors);
        forest.setWinni(index);
    }

    Boolean winniWasFound = false;
    public void findWinni(){
        for(int i = 0; i < tasks.length; i++) {
            final int index = i;
            Runnable r = ()->{
                while (true) {
                    //final Sector s = forest.getRandomFreeSector();
                    if(forest.checkSector(index)){          //
                        break;
                    }
                    else if(winniWasFound){
                        break;
                    }

                }
                winniWasFound = true;
            };

            tasks[i] = new Thread(r);
            tasks[i].start();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        Portfolio p = new Portfolio(4, 100000);
        p.findWinni();
    }
}