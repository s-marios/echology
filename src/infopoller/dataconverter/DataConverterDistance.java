/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller.dataconverter;

/**
 *
 * @author haha
 */
public class DataConverterDistance extends DataConverterBase {

    public DataConverterDistance() {
        super("000300", (byte) 0xF1, "DIST");
    }

    @Override
    protected String convertData(byte[] data) {
        int distance = (((int) data[0] & 0x0000ff00) << 8) | (int) data[1] & 0x000000ff;
        return Integer.toString(distance);
    }

}
