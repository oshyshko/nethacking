package examples;

import pcap.Convert;
import pcap.Pcap;

import java.io.Closeable;
import java.io.IOException;

public class E06_Listen_filter {

    public static void main(String[] args) throws IOException {
        String iface = "en0";

        // See: filter syntax at http://linux.die.net/man/7/pcap-filter
        //      or run $ man pcap-filter

        System.out.println("Listening...");

        // intercept packets on port 80 only (http)
        Closeable c = Pcap.listen(iface, "port 80", bytes -> {
            System.out.println("<<< " + Convert.bytes2hex(bytes));
        });

        System.err.println("Press Enter to close");
        System.in.read(); // blocks here until user presses Enter

        c.close();
    }
}
