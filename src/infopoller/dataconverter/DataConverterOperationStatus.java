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
public class DataConverterOperationStatus extends DataConverterBase {

    public DataConverterOperationStatus(String eoj, String infotype) {
        super(eoj, (byte) 0x80, infotype);
    }

    @Override
    protected String convertData(byte[] data) {
        if (data[0] == (byte) 0x30) {
            return Integer.toString(1);
        } else {
            return Integer.toString(1);
        }
    }

}
