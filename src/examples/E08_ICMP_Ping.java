package examples;

import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;
import pcap.Convert;
import pcap.IO;
import pcap.Pcap;
import pcap.Threads;

import java.io.Closeable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class E08_ICMP_Ping {
    public static void main(String[] args) throws UnknownHostException {
        String iface = Pcap.getDefault().getName();

        byte[] sourceMac = Pcap.get(iface).getLinkLayerAddresses().get(0).getAddress();
        byte[] sourceIp = Convert.dec2bytes("1.2.3.4");
        byte[] targetMac = Convert.hex2bytes("01:02:03:04:05:06");
        byte[] targetIp = Convert.dec2bytes("8.8.8.8");

        byte[] payload = Convert.hex2bytes("08090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f3031323334353637");


        // taken from https://github.com/kaitoy/pcap4j/blob/master/pcap4j-sample/src/main/java/org/pcap4j/sample/SendFragmentedEcho.java
        IcmpV4EchoPacket.Builder echoBuilder = new IcmpV4EchoPacket.Builder()
                .identifier((short) 1)
                .payloadBuilder(new UnknownPacket.Builder().rawData(payload));

        IcmpV4CommonPacket.Builder icmpBuilder = new IcmpV4CommonPacket.Builder()
                .type(IcmpV4Type.ECHO)
                .code(IcmpV4Code.NO_CODE)
                .correctChecksumAtBuild(true)
                .payloadBuilder(echoBuilder);

        IpV4Packet.Builder ipBuilder = new IpV4Packet.Builder()
                .version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) 64)
                .protocol(IpNumber.ICMPV4)
                .srcAddr((Inet4Address) InetAddress.getByAddress(sourceIp))
                .dstAddr((Inet4Address) InetAddress.getByAddress(targetIp))
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true)
                .payloadBuilder(icmpBuilder);

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder()
                .dstAddr(MacAddress.getByAddress(targetMac))
                .srcAddr(MacAddress.getByAddress(sourceMac))
                .type(EtherType.IPV4)
                .paddingAtBuild(true)
                .payloadBuilder(ipBuilder);

        Closeable c = null;
        try {
            c = Pcap.listen(iface, "icmp", false, bytes -> {
                System.out.println("Intercepted: " + Convert.bytes2hex(bytes));

                // TODO parse packet + find matching request packet
            });

            for (int i = 0; i < 5; i++) {
                echoBuilder.sequenceNumber((short) i);
                ipBuilder.identification((short) i);

                System.out.println("Sending " + i + "...");

                Pcap.send(iface, etherBuilder.build().getRawData());

                Threads.sleep(500);
            }
        } finally {
            IO.close(c);
        }

    }
}
