package pcap;

public class Threads {
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static Thread run(Runnable r) {
        return _run(false, r);
    }
    public static Thread daemon(Runnable r) {
        return _run(true, r);
    }

    public static Thread _run(boolean daemon, Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(daemon);
        t.start();
        return t;
    }
}
