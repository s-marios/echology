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
public abstract class DataConverterBase implements DataConverter {

    protected String type;
    protected byte EPC;
    
    DataConverterBase(byte epc) {
        this.EPC = epc;
    }
    
    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String doConversion(byte[] data) {
        if (data == null || data.length == 0) {
            return "BADDATA";
        } else {
            return convertData(data);
        }
    }

    protected abstract String convertData(byte[] data);

    @Override
    public byte getEPC() {
        return EPC;
    }

}
