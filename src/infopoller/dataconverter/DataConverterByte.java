package infopoller.dataconverter;

/**
 * A generic converter suitable for 1-byte properties, unsigned
 *
 * @author smarios <smarios@jaist.ac.jp>
 */
public class DataConverterByte extends DataConverterBase {

    public DataConverterByte(String eoj, byte epc, String infotype) {
        super(eoj, epc, infotype);
    }

    @Override
    protected String convertData(byte[] data) {
        assert data != null;
        assert data.length == 1;
        return Integer.toString(data[0] & 0x000000ff);
    }

}
