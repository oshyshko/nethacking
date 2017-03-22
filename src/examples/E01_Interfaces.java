package examples;

import org.pcap4j.core.PcapNetworkInterface;
import pcap.Pcap;

public class E01_Interfaces {
    public static void main(String[] args) {
        // Use the command below to see available interfaces in your OS:
        // $ ifconfig -a
        //
        // On Windows, use:
        // $ ipconfig /all

        System.out.println("Found " + Pcap.interfaces().size() + " interfaces");
        System.out.println();

        for (PcapNetworkInterface dev : Pcap.interfaces()) {
            System.out.println(
                    "Name: " + dev.getName() + "\n" +
                    "IPs:  " + dev.getAddresses() + "\n" +
                    "MACs: " + dev.getLinkLayerAddresses() + "\n");
        }

        try {
            System.out.println("Default network interface: " + Pcap.getDefault().getName());
        } catch (Exception e) {
            System.out.println("Couldn't get default network interface");
            e.printStackTrace();
        }
    }
}
