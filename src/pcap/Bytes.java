package pcap;

public class Bytes {
    public static boolean contains(byte[] where, int offset, byte[] what) {
        return equal(where, offset, what, 0, what.length);
    }

    public static boolean equal(byte[] a, int offsetA, byte[] b, int offsetB, int length) {
        // TODO return false if out of bounds

        for (int i = 0; i < length; i++)
            if (a[i + offsetA] != b[i + offsetB])
                return false;

        return true;
    }

    public static boolean equal(byte[] a, byte[] b) {
        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++)
            if (a[i] != b[i])
                return false;

        return true;
    }
}
