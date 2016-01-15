package pcap;

import org.pcap4j.util.ByteArrays;

public class Bytes {
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static String toHex(byte[] bytes) {
        return ByteArrays.toHexString(bytes, " ");
    }

    public static byte[] fromHex(String s) {
        s = s.replaceAll("[^0-9a-fA-F]", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
