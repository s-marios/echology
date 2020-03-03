package infopoller.dataconverter;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class DataConverterTemperature extends DataConverterBase {

    public DataConverterTemperature() {
        super("001100", (byte) 0xE0, "TMPR");
    }

    @Override
    public String convertData(byte[] data) {
        //TODO echonet bytes are unsigned. sanitize them.
        float temperature = (float) (((int) data[0] & 0x000000ff) << 8 | (int) data[1] & 0x000000ff) / 10;
        return String.format("%.1f", temperature);
    }

}
