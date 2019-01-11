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
public class DataConverterCO2VOC extends DataConverterBase {

    public DataConverterCO2VOC() {
        super((byte) 0xE0);
    }

    @Override
    protected String convertData(byte[] data) {
        Integer ppm = (((int) data[0] & 0x000000ff) << 8) | (int) data[1] & 0x000000ff;
        return ppm.toString();
    }

}
