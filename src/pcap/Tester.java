package pcap;

import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Tester {
    public static void main(String[] args) throws PcapNativeException, IOException {
        AtomicBoolean gotOthers     = new AtomicBoolean();
        AtomicBoolean gotOwn        = new AtomicBoolean();
        AtomicBoolean gotOwnBroken  = new AtomicBoolean();

        try {
            Pcap._muteSlf4j();
            System.out.println("# Pcap Tester 2");

            for (String k : new String[]{
                    "os.name",
                    "os.version",
                    "java.runtime.version",
                    "java.vendor",
                    "java.vm.name",
                    "sun.arch.data.model",
                    "sun.java.command",})
                System.out.println(k + " = " + System.getProperty(k));

            System.out.println();
            System.out.println("Pcap library: " + Pcaps.libVersion());
            System.out.println();

            if (Pcaps.findAllDevs().isEmpty()) {
                System.out.println("No interfaces found. Try running with \"sudo\".");
                return;
            }

            PcapNetworkInterface pnif = args.length == 0
                    ? Pcaps.findAllDevs().get(0)
                    : Pcaps.findAllDevs().get((Integer.parseInt(args[0]) - 1));

            System.out.println("Found interfaces:");
            List<PcapNetworkInterface> pnifs = Pcaps.findAllDevs();
            for (int i = 0; i < pnifs.size(); i++) {
                PcapNetworkInterface p = pnifs.get(i);
                System.out.println(
                        (pnif.equals(p) ? " --> " : "     ") +
                        (i + 1) + ". " + p.getName() +
                        (p.getDescription() == null ? "" : (" -- " + p.getDescription())));

            }

            System.out.println();
            System.out.println("NOTE: you may select another interface with: java -jar tester2.jar <interface-number>");
            System.out.println();

            byte[] packetBroken = Convert.hex2bytes( // Ethernet packet:
                            "01 02 03 04 05 06",     // - destination
                            "07 08 09 0a 0b 0c",     // - source
                            "0d 0e"                  // - type
            );

            String sourceMac = Convert.bytes2hex(pnif.getLinkLayerAddresses().get(0).getAddress());
            String sourceIp  = Convert.dec2hex("1.2.3.4");
            String targetMac = "ff:ff:ff ff:ff:ff";
            String targetIp  = Convert.dec2hex("5.6.7.8");

            byte[] packet = Convert.hex2bytes( // ----- Ethernet
                    targetMac,                 // Destination: 6 bytes
                    sourceMac,                 // Source: 6 bytes
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



            System.out.println("Listening...");
            Closeable c = Pcap.listen(pnif.getName(), bytes -> {
                if      (Arrays.equals(bytes, packet))       gotOwn.set(true);
                else if (Arrays.equals(bytes, packetBroken)) gotOwnBroken.set(true);
                else                                         gotOthers.set(true);
            });

            System.out.println("Sending...");
            try {
                Pcap.send(pnif.getName(), packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Sending (broken)...");
            try {
                Pcap.send(pnif.getName(), packetBroken);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 50; i++) {
                if (gotOthers.get() && gotOwn.get() && gotOwnBroken.get())
                    break;
                System.out.print(".");
                System.out.flush();

                Threads.sleep(200);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            System.out.println();
            System.out.println("Intercepted other's packet:      " + gotOthers.get());
            System.out.println("Intercepted own packet:          " + gotOwn.get());
            System.out.println("Intercepted own packet (broken): " + gotOwnBroken.get());
            System.out.println();

            if (gotOthers.get() && gotOwn.get() && gotOwnBroken.get()) {
                System.out.println("OK");
            } else if (gotOthers.get() && gotOwn.get() && !gotOwnBroken.get()) {
                System.out.println("OK (your OS doesn't allow sending broken packets, but that's still ok)");
            } else {
                System.out.println("ERROR");
            }

            System.exit(0);
        }
    }
}
