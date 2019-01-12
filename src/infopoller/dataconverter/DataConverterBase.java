/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller.dataconverter;

import jaist.echonet.EOJ;

/**
 *
 * @author haha
 */
public abstract class DataConverterBase implements DataConverter {

    /**
     * A string representation of the type of information.
     * Examples: "HMDT", "TMPR", "PRSN" others.
     */
    protected String type;
    /**
     * The Property code associated with this converter
     */
    protected byte EPC;
    
    /**
     * The echonet object type associated with this converter
     */
    protected EOJ eoj;
    
    DataConverterBase(String eoj, byte epc, String infotype) {
        this.EPC = epc;
        this.eoj = new EOJ(eoj);
        this.type = infotype;
    }
    
    @Override
    public String getInfoTypeString() {
        return this.type;
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
    
    @Override
    public EOJ getEOJ() {
        return eoj;
    }

}
