package examples;

import pcap.Bytes;
import pcap.Pcap;

import java.io.IOException;

public class E03_Sending_ARP {

    public static void main(String[] args) throws IOException {
        System.out.println("Sending...");

        String iface = "en0";

        String sourceMac = Bytes.toHex(Pcap.get(iface).getLinkLayerAddresses().get(0).getAddress());
        String targetMac = args[0];

        byte[] packet = Bytes.fromHex(  // ----- Ethernet
                "ff ff ff ff ff ff" +   // Destination: ff:ff:ff:ff:ff:ff
                        sourceMac +            // Source: __:__:__:__:__:__
                        "08 06" +               // Type: ARP (0x0806)
                        // ----- ARP
                        "00 01" +               // Hardware type: Ethernet (1)
                        "08 00" +               // Protocol type: IPv4 (0x0800)
                        "06" +                  // Hardware size: 6
                        "04" +                  // Protocol size: 4
                        "00 01" +               // Opcode: request (1)
                        sourceMac +             // Sender MAC address: __:__:__:__:__:__
                        "01 02 03 04" +         // Sender IP address: 1.2.3.4
                        targetMac +             // Target MAC address: __:__:__:__:__:__
                        "05 06 07 08"           // Target IP address: 5.6.7.8
        );


        Pcap.send(iface, packet);

        System.out.println("Done.");
    }
}
