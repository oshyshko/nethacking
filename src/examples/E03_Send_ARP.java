package examples;

import pcap.Convert;
import pcap.Pcap;

import java.io.IOException;

public class E03_Send_ARP {

    public static void main(String[] args) throws IOException {
        String iface = "en0";

        // Use the command bellow to see ARP traffic (works like Wireshark):
        // $ sudo tcpdump -ennqti en0 \( arp \)

        String sourceMac = Convert.bytes2hex(Pcap.get(iface).getLinkLayerAddresses().get(0).getAddress());
        String sourceIp  = Convert.dec2hex("1.2.3.4");
        String targetMac = "ff:ff:ff ff:ff:ff";
        String targetIp  = Convert.dec2hex("5.6.7.8");

        byte[] packet = Convert.hex2bytes( // ----- Ethernet
                targetMac,                 // Destination: ff:ff:ff:ff:ff:ff
                sourceMac,                 // Source: __:__:__:__:__:__
                "08 06",                   // Type: ARP (0x0806)
                                           // ----- ARP
                "00 01",                   // Hardware type: Ethernet (1)
                "08 00",                   // Protocol type: IPv4 (0x0800)
                "06",                      // Hardware size: 6
                "04",                      // Protocol size: 4
                "00 01",                   // Opcode: request (1)
                sourceMac,                 // Sender MAC address: 6 bytes
                sourceIp,                  // Sender IP address:  4 bytes
                targetMac,                 // Target MAC address: 6 bytes
                targetIp                   // Target IP address:  4 bytes
        );

        System.out.println("Sending [" + Convert.bytes2hex(packet) + "]...");

        Pcap.send(iface, packet);

        System.out.println("Done.");
    }
}
