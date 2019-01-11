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
public class DataConverterHumidity extends DataConverterBase{

    public DataConverterHumidity() {
        super((byte) 0xE0);
        
    }

    @Override
    public String convertData(byte[] data) {
        Integer humidity = (int) data[0];
        return humidity.toString();
    }
    
}
