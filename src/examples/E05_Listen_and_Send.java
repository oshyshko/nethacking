package examples;

import pcap.Convert;
import pcap.Pcap;
import pcap.Threads;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class E05_Listen_and_Send {

    public static void main(String[] args) throws IOException {
        String iface = "en0";

        System.out.println("Listening...");

        byte[] packet = Convert.hex2bytes( // ----- Ethernet
                "01 02 03 04 05 06",       // Destination: 01:02:03:04:05:06
                "07 08 09 0a 0b 0c",       // Source:      07:08:09:0a:0b:0c
                "0d 0e"                    // Type:        0D 0E
                                           // (empty payload)
        );

        Closeable c  = Pcap.listen(iface, bytes -> {
            if (Arrays.equals(bytes, packet)) {
                System.out.println("Got our own packet!");
            } else {
                System.out.println("Got someone else's packet");
            }
        });

        Threads.sleep(1000);

        System.out.println("Sending...");
        Pcap.send(iface, packet); // this line may throw exceptions if something is wrong

        // waiting another second to ensure that our packet was intercepted by the listener above
        Threads.sleep(1000);

        System.out.println("Exiting...");
        c.close();

        System.out.println("Done.");
    }
}
