package examples;

import pcap.Convert;
import pcap.Pcap;

import java.io.IOException;

public class E02_Send {

    public static void main(String[] args) throws IOException {
        System.out.println("Sending...");

        Pcap.send("en0", new byte[] {       // ----- Ethernet
                 1,  2,  3,  4,  5,  6,     // Destination: 01:02:03:04:05:06
                 7,  8, 10, 11, 12, 13,     // Source:      07:08:09:0a:0b:0c
                14, 15                      // Type:        0D 0E
                                            // (empty payload)
        });

        Pcap.send("en0", Convert.hex2bytes( // ----- Ethernet
                "01 02 03 04 05 06 " +      // Destination: 01:02:03:04:05:06
                "07 08 09 0a 0b 0c " +      // Source:      07:08:09:0a:0b:0c
                "0d 0e"                     // Type:        0D 0E
                                            // (empty payload)
        ));

        Pcap.send("en0", Convert.hex2bytes( // ----- Ethernet
                "01 02 03 04 05 06",        // Destination: 01:02:03:04:05:06
                "07 08 09 0a 0b 0c",        // Source:      07:08:09:0a:0b:0c
                "0d 0e"                     // Type:        0D 0E
                                            // (empty payload)
        ));

        // NOTE: notice how comma (,) replaces concatenation (+)
        //       in the last example (the result is the same)

        System.out.println("Done.");
    }
}
