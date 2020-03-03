package infopoller.dataconverter;

import jaist.echonet.EOJ;

/**
 *
 * @author haha
 */
public abstract class DataConverterBase implements DataConverter {

    /**
     * A string representation of the type of information. Examples: "HMDT",
     * "TMPR", "PRSN" others.
     */
    protected String type;
    /**
     * The Property code associated with this converter
     */
    protected byte EPC;

    /**
     * The echonet object type associated with this converter, with the instance
     * code set to zero.
     */
    protected EOJ eoj;

    DataConverterBase(String eoj, byte epc, String infotype) {
        this.EPC = epc;
        this.eoj = new EOJ(eoj).getEOJWithInstanceCode((byte) 0);
        this.type = infotype;
    }

    @Override
    public String getInfoTypeString() {
        return this.type;
    }

    @Override
    public String doConversion(byte[] data) {
        if (data == null || data.length == 0) {
            return "BADDATA";
        } else {
            return convertData(data);
        }
    }

    /**
     * Convert data to a numerical string representation.
     *
     * @param data the data to encode as a string, non-null
     * @return a string representing the numerical data (no units), null for
     * failure
     */
    protected abstract String convertData(byte[] data);

    @Override
    public byte getEPC() {
        return EPC;
    }

    @Override
    public EOJ getEOJ() {
        return eoj;
    }

}
