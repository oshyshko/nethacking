package pcap;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class Test_Packets {
    @Test
    public void test() {
        byte[] bs = Convert.hex2bytes(
                "00 00 19 00 6f 08 00 00 26 8a a9 53 00 00 00 00",
                "10 02 a8 09 80 04 ae a4 00 80 00 00 00 ff ff ff",
                "ff ff ff 50 46 5d 86 ed a0 50 46 5d 86 ed a0 50",
                "ba 73 81 eb 24 33 00 00 00 64 00 11 04 00 08 6d",
                "65 72 79 61 6c 65 78 01 08 82 84 8b 96 0c 12 18",
                "24 03 01 0d 05 04 00 01 00 00 2a 01 04 32 04 30",
                "48 60 6c 2d 1a 2c 18 1e ff 00 00 00 00 00 00 00",
                "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3d",
                "16 0d 00 03 00 00 00 00 00 00 00 00 00 00 00 00",
                "00 00 00 00 00 00 00 dd 16 00 50 f2 01 01 00 00",
                "50 f2 04 01 00 00 50 f2 04 01 00 00 50 f2 02 30",
                "14 01 00 00 0f ac 04 01 00 00 0f ac 04 01 00 00",
                "0f ac 02 00 00 dd 18 00 50 f2 02 01 01 00 00 03",
                "a4 00 00 27 a4 00 00 42 43 5e 00 62 32 2f 00 dd",
                "1e 00 90 4c 33 2c 18 1e ff 00 00 00 00 00 00 00",
                "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 dd",
                "1a 00 90 4c 34 0d 00 03 00 00 00 00 00 00 00 00",
                "00 00 00 00 00 00 00 00 00 00 00 dd 06 00 e0 4c",
                "02 01 60 dd 18 00 50 f2 04 10 4a 00 01 10 10 44",
                "00 01 02 10 49 00 06 00 37 2a 00 01 20 20 25 20",
                "db");

        HashMap<String, String> kv = new HashMap<>();
        kv.put("ssid", "meryalex");
        kv.put("channel", "13");

        Assert.assertEquals(kv, Packets.parseRfmonBeacon(bs));
    }
}
