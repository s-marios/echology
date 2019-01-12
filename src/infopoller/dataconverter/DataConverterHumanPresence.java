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
public class DataConverterHumanPresence extends DataConverterBase{

    public DataConverterHumanPresence() {
        super("000700", (byte) 0xB1, "PRSN");
    }

    @Override
    protected String convertData(byte[] data) {
        return data[0] == (byte) 0x41 ? "1" : "0";
    }
    
}
