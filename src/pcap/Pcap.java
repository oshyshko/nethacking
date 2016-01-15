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
        _muteSlf4j();
    }

    private static final int SNAPLEN        = 65536;    // bytes
    private static final int READ_TIMEOUT   = 10;       // ms
    private static final int COUNT          = 1;

    public static Closeable listen(String iface, Listener l) {
        return listen(iface, null, l);
    }
    public static Closeable listen(String iface, String filter, Listener l) {
        PcapHandle _recv = null;
        ExecutorService _pool = null;

        try {
            PcapNetworkInterface pnif = get(iface);

            PcapHandle recv = pnif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            _recv = recv;

            if (null != filter)
                recv.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

            ExecutorService pool = Executors.newSingleThreadExecutor();
            _pool = pool;

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
            if (_pool != null) _pool.shutdown();
            if (_recv != null && _recv.isOpen()) _recv.close();
            throw new RuntimeException(e);
        }
    }

    public static void send(String iface, byte[] bytes) {
        send(get(iface), bytes);
    }
    public static void send(PcapNetworkInterface pnif, byte[] bytes) {
        PcapHandle send = null;
        try {
            send = pnif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            send.sendPacket(UnknownPacket.newPacket(bytes, 0, bytes.length));
        } catch (PcapNativeException | NotOpenException e) {
            throw new RuntimeException(e);
        } finally {
            if (send != null && send.isOpen()) send.close();
        }

    }

    public static List<PcapNetworkInterface> interfaces() {
        try {
            return Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }
    public static PcapNetworkInterface get(String iface)  {
        try {
            for (PcapNetworkInterface dev : Pcaps.findAllDevs())
                if (iface.equals(dev.getName()))
                    return dev;

            throw new IllegalArgumentException("Can't find interface with name: " + iface +
                    ". Available interfaces are: " +
                    interfaces().stream().map(PcapNetworkInterface::getName).collect(Collectors.toList()));
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Listener {
        void onPacket(byte[] bytes);
    }

    public static void _muteSlf4j() {
        // muting slf4j. Override by setting your value for "org.slf4j.simpleLogger.defaultLogLevel"
        if (!System.getProperties().contains(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY)) {
            System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        }
    }

}
