package echology.poller.dataconverter;

/**
 * A generic converter suitable for 2-byte properties, unsigned
 * *
 * @author smarios <smarios@jaist.ac.jp>
 */
public class DataConverterShort extends DataConverterBase {

    /**
     * Default constructor, dealing with unsigned shorts
     * 
     * @param eoj eoj
     * @param epc property code
     * @param infotype a string representing the type of information
     */
    public DataConverterShort(String eoj, byte epc, String infotype) {
        super(eoj, epc, infotype);
    }

    @Override
    protected String convertData(byte[] data) {
        assert data != null && data.length == 2;
        int number_short = (((int) data[0] & 0x000000ff) << 8
                | ((int) data[1] & 0x000000ff));
        return Integer.toString(number_short);
    }
}
