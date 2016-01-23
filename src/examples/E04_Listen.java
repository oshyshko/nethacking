package examples;

import pcap.Convert;
import pcap.Pcap;

import java.io.Closeable;
import java.io.IOException;

public class E04_Listen {

    public static void main(String[] args) throws IOException {
        String iface = Pcap.getDefault().getName();

        System.out.println("Listening...");

        Closeable c  = Pcap.listen(iface, new Pcap.Listener() {
            public void onPacket(byte[] bytes) {
                System.out.println("<<< " + Convert.bytes2hex(bytes));
            }
        });

        System.err.println("Press Enter to close");
        System.in.read(); // blocks here until user presses Enter

        c.close();

        System.out.println("Done.");

        // NOTE: the same code, but with new Java 8 lambda syntax.
        //       We'll be using this style for the rest of examples.
        //
        // Closeable c = Pcap.listen("en0", bytes -> {
        //     System.out.println("<<< " + Convert.bytes2hex(bytes));
        // });
        //
        // System.err.println("Press enter to shutdown");
        // System.in.read();
        //
        // c.close();


        // NOTE: the same code, but with new Java 8 try-with syntax
        //
        // try (Closeable c = Pcap.listen("en0", bytes -> {
        //     System.out.println("<<< " + Convert.bytes2hex(bytes));
        // })) {
        //     System.err.println("Press enter to shutdown");
        //     System.in.read();
        // }
    }
}
