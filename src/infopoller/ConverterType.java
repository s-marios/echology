/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

import jaist.echonet.EOJ;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author haha
 */
public enum ConverterType {
    //TODO change the .class to just a new instance of the ctype to avoid exceptions
    TEMP ("001100", "TEMP", new TemperatureDataConverter()),
    HUMIDITY ("001200", "HMDT", new HumidityDataConverter());
            
    private final String eoj;
    private final String type;
    private final DataConverter converter;
    /**
     * A map that holds EOJ.hashCode() <-> ctype mappings
     */
    private static final Map<Integer, DataConverter> cmap;
    
    //I always love seeing this...
    static {
        cmap = new ConcurrentHashMap<>();
        for (ConverterType ctype : EnumSet.allOf(ConverterType.class)){
            cmap.put(new EOJ(ctype.eoj).hashCode(), ctype.converter);
        }
    }
    
    ConverterType(String eoj, String type, DataConverter converter){
        this.eoj = eoj;
        this.type = type;
        this.converter = converter;
        this.converter.setType(type);
    }
    
    static DataConverter getConverter(EOJ eoj){
        EOJ normalized = new EOJ(eoj.getClassGroupCode(), eoj.getClassCode(), (byte) 0x00);
        return cmap.get(normalized.hashCode());
    }
}
