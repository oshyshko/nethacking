package examples;

import pcap.Packets;
import pcap.Pcap;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public class E07_Listen_rfmon {

    public static void main(String[] args) throws IOException {
        String iface = "en0";

        System.out.println("Listening...");

        // filter set to "", frmon set to "true"
        Closeable c = Pcap.listen(iface, "", true, bytes -> {
            try {
                Map<String, String> packet = Packets.parseRfmonBeacon(bytes);

                System.out.println("<<< " + packet);
            } catch (Exception e) {
                // do nothing -- got something we don't know how to parse yet
            }
        });

        System.err.println("Press Enter to close");
        System.in.read(); // blocks here until user presses Enter

        c.close();

        System.out.println("Done.");
    }
}
