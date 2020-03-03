package infopoller.dataconverter;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class DataConverterOperationStatus extends DataConverterBase {

    public DataConverterOperationStatus(String eoj, String infotype) {
        super(eoj, (byte) 0x80, infotype);
    }

    @Override
    protected String convertData(byte[] data) {
        if (data[0] == (byte) 0x30) {
            return Integer.toString(1);
        } else {
            return Integer.toString(0);
        }
    }

}
