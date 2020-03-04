package echology.poller.dataconverter;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class DataConverterHumanPresence extends DataConverterBase {

    public DataConverterHumanPresence() {
        super("000700", (byte) 0xB1, "PRSN");
    }

    @Override
    protected String convertData(byte[] data) {
        assert data != null && data.length == 1;
        return data[0] == (byte) 0x41 ? "1" : "0";
    }

}
