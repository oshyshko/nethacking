package pcap;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class Test_Bytes {
    @Test
    public void test() {
        Assert.assertThat(new byte[]{ 1,  2,  3}, IsEqual.equalTo(Bytes.fromHex("01 02 03")));
        Assert.assertThat(new byte[]{-1, -2, -3}, IsEqual.equalTo(Bytes.fromHex("ff fe fd")));
        Assert.assertThat(new byte[]{ 4,  5,  6}, IsEqual.equalTo(Bytes.fromHex("04:05  06")));

        Assert.assertThat("ff fe fd", IsEqual.equalTo(Bytes.toHex(new byte[]{-1, -2, -3})));
        Assert.assertThat("01 02 03", IsEqual.equalTo(Bytes.toHex(new byte[]{ 1,  2,  3})));
    }
}
