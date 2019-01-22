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
public class DataConverterLux extends DataConverterBase {

    public DataConverterLux() {
        super("000D00", (byte) 0xE0, "LUX");
    }
    
    

    @Override
    protected String convertData(byte[] data) {
        int lux = ((int) data[0] & 0x000000ff) << 8 | (int) data[1] & 0x000000ff;
        return String.format("%d", lux);
    }
    
}
