package pcap;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class Test_Convert {
    @Test
    public void test() {
        Assert.assertThat(new byte[]{  1,  2,  3}, IsEqual.equalTo(Convert.hex2bytes("01 02 03")));
        Assert.assertThat(new byte[]{ -1, -2, -3}, IsEqual.equalTo(Convert.hex2bytes("ff fe fd")));

        Assert.assertThat(new byte[]{  1,  2,  3}, IsEqual.equalTo(Convert.hex2bytes("010203")));
        Assert.assertThat(new byte[]{ -1, -2, -3}, IsEqual.equalTo(Convert.hex2bytes("fffefd")));

        Assert.assertThat(new byte[]{  4,  5,  6}, IsEqual.equalTo(Convert.hex2bytes("04:05  06")));

        Assert.assertThat("ff fe fd", IsEqual.equalTo(Convert.bytes2hex(new byte[]{-1, -2, -3})));
        Assert.assertThat("01 02 03", IsEqual.equalTo(Convert.bytes2hex(new byte[]{1, 2, 3})));

        Assert.assertThat(new byte[]{  1,  2,  3}, IsEqual.equalTo(Convert.fromDec("1 2 3")));
        Assert.assertThat(new byte[]{ -1, -2, -3}, IsEqual.equalTo(Convert.fromDec("255 254 253")));
    }
}
