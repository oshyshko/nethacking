package pcap;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Pcap {
    static {
        // muting slf4j. Override by setting your value for "org.slf4j.simpleLogger.defaultLogLevel"
        if (!System.getProperties().contains(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY)) {
            System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        }
    }

    private static final int SNAPLEN        = 65536;    // bytes
    private static final int READ_TIMEOUT   = 10;       // ms
    private static final int COUNT          = 1;

    //private static final Map<String, NIF> iface2impl = new HashMap<String, NIF>();

    public static Closeable listen(String iface, Listener l) {
        return listen(iface, null, l);
    }
    public static Closeable listen(String iface, String filter, Listener l) {
        try {
            PcapNetworkInterface pnif = findPnif(iface);

            PcapHandle recv = pnif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

            if (null != filter)
                recv.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

            ExecutorService pool  = Executors.newSingleThreadExecutor();

            // pump events
            pool.execute(() -> {
                try {
                    while (!pool.isShutdown() && recv.isOpen()) {
                        recv.loop(COUNT, (Packet raw) -> l.onPacket(raw.getRawData()));
                    }
                } catch (PcapNativeException | InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (NotOpenException e) {
                    // do nothing
                }
            });
            return () -> {
                if (!pool.isShutdown()) pool.shutdown();
                if (recv.isOpen()) recv.close();
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //private static NIF getOrCreate(String iface) throws PcapNativeException {
    //    if (!iface2impl.containsKey(iface))
    //        iface2impl.put(iface, new NIF(iface));
    //
    //    return iface2impl.get(iface);
    //}

    private static PcapNetworkInterface findPnif(String iface)  {
        try {
            for (PcapNetworkInterface dev : Pcaps.findAllDevs())
                if (iface.equals(dev.getName()))
                    return dev;

            throw new IllegalArgumentException("Can't find interface with name: " + iface +
                    ". Available interface are: " +
                    interfaces().stream().map(PcapNetworkInterface::getName).collect(Collectors.toList()));
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void send(String iface, byte[] bytes) {
        try {
            PcapNetworkInterface pnif = findPnif(iface);
            PcapHandle send = pnif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            send.sendPacket(UnknownPacket.newPacket(bytes, 0, bytes.length));
        } catch (PcapNativeException | NotOpenException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<PcapNetworkInterface> interfaces() {
        try {
            return Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Listener {
        void onPacket(byte[] bytes);
    }
}
