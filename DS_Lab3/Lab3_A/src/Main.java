
public class Main {
    public static void main(String[] args) {
        Pot pot = new Pot(10);
        Winny winny = new Winny(pot);
        Bee bee1 = new Bee(1, pot, winny);
        Bee bee2 = new Bee(2, pot, winny);
        Bee bee3 = new Bee(3, pot, winny);
        Bee bee4 = new Bee(4, pot, winny);

        Thread w = new Thread(winny);
        Thread b1 = new Thread(bee1);
        Thread b2 = new Thread(bee2);
        Thread b3 = new Thread(bee3);
        Thread b4 = new Thread(bee4);

        w.start();
        b1.start();
        b2.start();
        b3.start();
        b4.start();

        try {
            b2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}