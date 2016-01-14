package examples;

import pcap.Bytes;
import pcap.Pcap;

import java.io.IOException;

public class E03_Sending {

    public static void main(String[] args) throws IOException {
        System.out.println("Sending...");

        Pcap.send("en0", Bytes.fromHex( // Ethernet packet:
                "01 02 03 04 05 06" +   // - destination
                "07 08 09 0a 0b 0c" +   // - source
                "0d 0e"                 // - type
        ));

        System.out.println("Done.");
    }
}
