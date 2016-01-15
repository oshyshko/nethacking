package pcap;

import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Tester {
    public static void main(String[] args) throws PcapNativeException, IOException {
        AtomicBoolean gotOwn    = new AtomicBoolean();
        AtomicBoolean gotOthers = new AtomicBoolean();
        try {
            Pcap._muteSlf4j();


            for (String k : new String[]{
                    "os.name",
                    "os.version",
                    "java.runtime.version",
                    "java.vendor",
                    "java.vm.name",
                    "sun.arch.data.model",
                    "sun.java.command",
            }) {
                System.out.println(k + " = " + System.getProperty(k));
            }

            System.out.println();

            System.out.println("Pcap library: " + Pcaps.libVersion());
            System.out.println("Found interfaces: " + Pcaps.findAllDevs().stream().map(PcapNetworkInterface::getName).collect(Collectors.toList()));

            System.out.println();

            if (Pcaps.findAllDevs().isEmpty()) {
                System.out.println("No interfaces? Try running with \"sudo\".");
                System.exit(1);
            }

            PcapNetworkInterface pnif = args.length == 0
                    ? Pcaps.findAllDevs().get(0)
                    : Pcaps.getDevByName(args[0]);

            System.out.println("Testing for interface \"" + pnif.getName() + "\"");

            byte[] packet = Bytes.fromHex(         // Ethernet packet:
                            "01 02 03 04 05 06" +  // - destination
                            "07 08 09 0a 0b 0c" +  // - source
                            "0d 0e"                // - type
            );

            System.out.println("Listening...");
            Closeable c = Pcap.listen(pnif.getName(), bytes -> {
                if (Arrays.equals(bytes, packet)) {
                    gotOwn.set(true);
                } else {
                    gotOthers.set(true);
                }
            });

            System.out.println("Sending...");
            Pcap.send(pnif.getName(), packet);

            for (int i = 0; i < 50; i++) {
                if (gotOwn.get() && gotOthers.get())
                    break;
                System.out.print(".");
                System.out.flush();

                Threads.sleep(200);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            boolean ok = gotOwn.get() && gotOthers.get();

            System.out.println();
            System.out.println("Intercepted own packet:     " + gotOwn.get());
            System.out.println("Intercepted other's packet: " + gotOthers.get());
            System.out.println();
            System.out.println(ok ? "OK" : "ERROR");

            System.exit(ok ? 0 : 1);
        }
    }
}
