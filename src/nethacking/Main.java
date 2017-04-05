package nethacking;

import pcap.Convert;
import pcap.Pcap;

import java.io.Closeable;

public class Main {
    public static void main(String[] args) throws Exception {
        // sending
        Pcap.send("en0", "Hello, World!".getBytes());

        // receiving
        Closeable c = Pcap.listen("en0", bytes -> {
            System.out.println(Convert.bytes2hex(bytes));
        });

        System.out.println("Press <ENTER> to exit");
        System.in.read();
        c.close();
    }
}
