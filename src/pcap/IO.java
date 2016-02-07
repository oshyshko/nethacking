package pcap;

import java.io.Closeable;
import java.io.IOException;

public class IO {
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
