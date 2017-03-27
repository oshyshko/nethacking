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
    private static final String VERSION = "6";

    public static void main(String[] args) throws PcapNativeException, IOException {
        AtomicBoolean gotOthers     = new AtomicBoolean();
        AtomicBoolean gotOwn        = new AtomicBoolean();
        AtomicBoolean gotOwnBroken  = new AtomicBoolean();
        AtomicBoolean rfmonSupported  = new AtomicBoolean();

        String pnifName = "";

        try {
            Pcap._muteSlf4j();
            System.out.println("# Pcap Tester " + VERSION);

            if (Pcaps.findAllDevs().isEmpty()) {
                System.out.println("No interfaces found. Try running with \"sudo\".");
                return;
            }

            PcapNetworkInterface pnif;

            if (args.length != 0) {
                pnif = Pcaps.findAllDevs().get((Integer.parseInt(args[0]) - 1));
            } else {
                System.out.println("Guessing default interface...");
                System.out.println();
                try {
                    pnif = Pcap.getDefault();
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.out.println();
                    System.out.println("Got an exception, selecting the first interface.");
                    System.out.println();

                    pnif = Pcaps.findAllDevs().get(0);
                }
            }


            pnifName = pnif.getName();

            List<PcapNetworkInterface> pnifs = Pcaps.findAllDevs();
            for (int i = 0; i < pnifs.size(); i++) {
                PcapNetworkInterface p = pnifs.get(i);
                System.out.println(
                        (pnif.equals(p) ? " --> " : "     ") +
                        (i + 1) + ". " + p.getName() +
                        (p.getDescription() == null ? "" : (" -- " + p.getDescription())));

            }

            System.out.println();
            System.out.println("NOTE: you can select another interface with: java -jar tester" + VERSION + ".jar <interface-number>");
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
                Pcap.send(pnif, packet);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            System.out.println("Sending (broken)...");
            try {
                Pcap.send(pnif, packetBroken);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            System.out.println("Probing RFMON...");
            try (Closeable ignored = Pcap.listen(pnif.getName(), "", true, (bytes -> { /* do nothing */}))) {
                rfmonSupported.set(true);
            };

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

            String result = gotOthers.get() && gotOwn.get() ? "OK" : "ERROR";

            System.out.println();
            System.out.println("# Test results");
            for (String k : new String[]{
                    "os.name",
                    "os.version",
                    "java.runtime.version",})
                System.out.println(String.format("%-33s = %s", k, System.getProperty(k)));
            System.out.println("Pcap library                      = " + Pcaps.libVersion());
            System.out.println("Intercepted other's packet        = " + gotOthers.get());
            System.out.println("Intercepted own packet            = " + gotOwn.get());
            System.out.println("Intercepted own packet (broken)   = " + gotOwnBroken.get());
            System.out.println("Radio Frequency MONitor supported = " + rfmonSupported.get());
            System.out.println(result);
            System.out.println();

            System.out.println();
            System.out.println("Copy and paste this line to Google Sheets:");
            System.out.println("============================================================================================================================");
            for (String k : new String[]{
                    "os.name",
                    "os.version",
                    "java.runtime.version",})
                System.out.print(System.getProperty(k) + "\t");
            System.out.print(Pcaps.libVersion());
            System.out.print("\t" + pnifName);
            System.out.print("\t" + gotOthers.get());
            System.out.print("\t" + gotOwn.get());
            System.out.print("\t" + gotOwnBroken.get());
            System.out.print("\t" + rfmonSupported.get());
            System.out.print("\t" + result);
            System.out.println();
            System.out.println("============================================================================================================================");
            System.out.println();

            System.exit(0);
        }
    }
}
