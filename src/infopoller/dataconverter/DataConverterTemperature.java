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
public class DataConverterTemperature extends DataConverterBase{

    public DataConverterTemperature() {
        super((byte) 0xE0);
    }

    @Override
    public String convertData(byte[] data) {
        //echonet bytes are unsigned. sanitize them.
        float temperature = (float) ( ((int) data[0] & 0x000000ff) << 8 | (int) data[1] & 0x000000ff) / 10;
        return String.format("%.1f", temperature);
    }

    
}
