package infopoller.dataconverter;

/**
 * A generic converter suitable for 4-byte properties, unsigned
 *
 * @author smarios <smarios@jaist.ac.jp>
 */
public class DataConverterInt extends DataConverterBase {

    public DataConverterInt(String eoj, byte epc, String infotype) {
        super(eoj, epc, infotype);
    }

    @Override
    protected String convertData(byte[] data) {
        assert data != null && data.length == 4;
        int number_int = (((int) data[0] & 0x000000ff) << 24
                | ((int) data[1] & 0x000000ff) << 16
                | ((int) data[2] & 0x000000ff) << 8
                | ((int) data[3] & 0x000000ff));
        return Integer.toString(number_int);
    }

}
