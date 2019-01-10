/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

/**
 *
 * @author haha
 */
public class HumidityDataConverter extends DataConverterBase{

    public HumidityDataConverter() {
        super((byte) 0xE0);
        
    }

    @Override
    public String convertData(byte[] data) {
        Integer humidity = (int) data[0];
        return humidity.toString();
    }
    
}
