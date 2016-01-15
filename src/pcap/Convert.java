package pcap;

import org.pcap4j.util.ByteArrays;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convert {
    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    public static final Pattern HEX = Pattern.compile("[0-9a-fA-F][0-9a-fA-F]?");
    public static final Pattern DEC = Pattern.compile("[0-9][0-9]?[0-9]?");

    public static String bytes2hex(byte[] bytes) {
        return ByteArrays.toHexString(bytes, " ");
    }
    public static String bytes2hex(byte[] bytes, int offset, int length) {
        return ByteArrays.toHexString(bytes, " ", offset, length);
    }

    public static byte[] hex2bytes(String... ss) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String s : ss) {
            Matcher m = HEX.matcher(s);
            while (m.find()) {
                baos.write((byte) Integer.parseInt(m.group(), 16));
            }
        }
        return baos.toByteArray();
    }

    public static byte[] fromDec(String... ss) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String s : ss) {
            Matcher m = DEC.matcher(s);
            while (m.find()) {
                baos.write((byte) Integer.parseInt(m.group(), 10));
            }
        }
        return baos.toByteArray();
    }

    public static String dec2hex(String... ss) {
        return bytes2hex(fromDec(ss));
    }

}
