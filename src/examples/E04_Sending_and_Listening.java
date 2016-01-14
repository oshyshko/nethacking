package examples;

import pcap.Bytes;
import pcap.Pcap;
import pcap.Threads;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class E04_Sending_and_Listening {

    public static void main(String[] args) throws IOException {
        byte[] packet = Bytes.fromHex( // Ethernet packet:
                "01 02 03 04 05 06" +  // - destination
                "07 08 09 0a 0b 0c" +  // - source
                "0d 0e"                // - type
        );

        // listening
        System.out.println("Listening...");

        Closeable c  = Pcap.listen("en0", new Pcap.Listener() {
            public void onPacket(byte[] bytes) {
                if (Arrays.equals(bytes, packet)) {
                    System.out.println("<<< GOT OUR PACKET!");
                } else {
                    System.out.println("<<< got someone else's packet");
                }
            }
        });

        // sleeping 1 second
        Threads.sleep(2000);

        // sending
        System.out.println(">>> Sending...");
        Pcap.send("en0", packet);

        // waiting another 1 second to ensure that our packet was intercepted by the listener above
        Threads.sleep(1000);

        // exiting
        System.out.println("Exiting...");
        c.close();

        System.out.println("Done.");
    }
}
