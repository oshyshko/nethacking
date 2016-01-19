package pcap;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Packets {
    public static Map<String, String> parseRfmonBeacon(byte[] bs) {
        Map<String, String> kv = new HashMap<>();
        if (!Bytes.contains(bs, 0, new byte[] {0, 0}))
            throw new IllegalArgumentException("Unsupported radiotap header: " + Convert.bytes2hex(bs, 0, 2));

        int radiotap_length = bs[2] + bs[3] * 256; // little-endian

        // NOTE: the dump of example packet used in comments bellow
        //       can be found in pcap.Test_Packets/test()

        // ----- Radiotap Header v0, Length 25
        // 00       - Header revision: 0
        // 00       - Header pad: 0
        // 19 00    - Header length: 25
        // 6f 08 00 00 26 8a a9 53 00 00 00 00
        // 10 02 a8 09 80 04 ae a4 00
        //
        // ---- IEEE 802.11 Beacon frame, Flags: ........C (24 bytes)
        // 80 00    - Frame Control Field: 0x8000
        // 00 00 ff ff ff
        // ff ff ff 50 46 5d 86 ed a0 50 46 5d 86 ed a0 50
        // ba

        if (!Bytes.contains(bs, radiotap_length, Convert.hex2bytes("80 00")))
            throw new IllegalArgumentException("Unsupported Frame Control Field: " + Convert.bytes2hex(bs, radiotap_length, 2));

        // ---- IEEE 802.11 wireless LAN management frame
        // 73 81 eb 24 33 00 00 00 64 00 11 04 -- Fixed parameters (12 bytes)
        //

        int i = radiotap_length + 24 + 12;

        while (i < bs.length) {
            int type  = bs[i] & 0xFF;
            int length = bs[i + 1] & 0xFF; // make unsigned

            i += 2;

            switch (type) {
                case 0x00: kv.put("ssid",    new String(bs, i, length, Charset.forName("UTF-8"))); break;
                case 0x03: kv.put("channel", Integer.toString(bs[i], 10)); break;
            }

            i += length;
        }

        // 00 - Tag Number: SSID parameter set (0)
        // 08 - Tag length: 8
        // 6d 65 72 79 61 6c 65 78 - SSID: meryalex
        //
        // 01 - Tag Number: Supported Rates (1)
        // 08 - Tag length: 8
        // 82 84 8b 96 0c 12 18 24
        //
        // 03 - Tag Number: DS Parameter set (3)
        // 01 - Tag length: 1
        // 0d - Current Channel: 13
        //
        // 05 - Tag Number: Traffic Indication Map (TIM) (5)
        // 04 - Tag length: 4
        // 00 01 00 00
        //
        // 2a - Tag Number: ERP Information (42)
        // 01 - Tag length: 1
        // 04
        //
        // 32 - Tag Number: Extended Supported Rates (50)
        // 04 30
        // 48 60 6c 2d 1a 2c 18 1e ff 00 00 00 00 00 00 00
        // 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3d
        // 16 0d 00 03 00 00 00 00 00 00 00 00 00 00 00 00
        // 00 00 00 00 00 00 00 dd 16 00 50 f2 01 01 00 00
        // 50 f2 04 01 00 00 50 f2 04 01 00 00 50 f2 02 30
        // 14 01 00 00 0f ac 04 01 00 00 0f ac 04 01 00 00
        // 0f ac 02 00 00 dd 18 00 50 f2 02 01 01 00 00 03
        // a4 00 00 27 a4 00 00 42 43 5e 00 62 32 2f 00 dd
        // 1e 00 90 4c 33 2c 18 1e ff 00 00 00 00 00 00 00
        // 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 dd
        // 1a 00 90 4c 34 0d 00 03 00 00 00 00 00 00 00 00
        // 00 00 00 00 00 00 00 00 00 00 00 dd 06 00 e0 4c
        // 02 01 60 dd 18 00 50 f2 04 10 4a 00 01 10 10 44
        // 00 01 02 10 49 00 06 00 37 2a 00 01 20 20 25 20
        // db

        return kv;
    }
}
