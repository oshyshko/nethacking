package examples;

import pcap.Bytes;
import pcap.Pcap;

import java.io.Closeable;
import java.io.IOException;

public class E02_Listening {

    public static void main(String[] args) throws IOException {
        // NOTE replace "en0" bellow with interface name that exists on your system
        //
        // See: output of "ifconfig" command on UNIX-like or "ipconfig" on Windows
        // See: method Pcap.interfaces()

        System.out.println("Listening...");

        Closeable c  = Pcap.listen("en0", new Pcap.Listener() {
            public void onPacket(byte[] bytes) {
                System.out.println("<<< " + Bytes.toHex(bytes));
            }
        });

        System.err.println("Press enter to close");
        System.in.read();

        c.close();

        System.out.println("Done.");

        // NOTE: the same code, but with new Java 8 lambda syntax
        //
        // Closeable c = Pcap.listen("en0", bytes -> {
        //     System.out.println("<<< " + Bytes.toHex(bytes));
        // });
        //
        // System.err.println("Press enter to shutdown");
        // System.in.read();
        //
        // c.close();


        // NOTE: the same code, but with new Java 8 try-with syntax
        //
        // try (Closeable c = Pcap.listen("en0", bytes -> {
        //     System.out.println("<<< " + Bytes.toHex(bytes));
        // })) {
        //     System.err.println("Press enter to shutdown");
        //     System.in.read();
        // }
    }
}
