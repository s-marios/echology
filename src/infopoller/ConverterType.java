/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

import infopoller.dataconverter.DataConverter;
import infopoller.dataconverter.DataConverterCO2;
import infopoller.dataconverter.DataConverterHumanPresence;
import infopoller.dataconverter.DataConverterHumidity;
import infopoller.dataconverter.DataConverterTemperature;
import infopoller.dataconverter.DataConverterVOC;
import jaist.echonet.EOJ;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author haha
 */
public class ConverterType {
    
    //TODO change enum to... class again?
    /*
    TEMP ("001100", "TEMP", new DataConverterTemperature()),
    HUMIDITY ("001200", "HMDT", new DataConverterHumidity()),
    //evoc and co2 are doing the same thing, use the same converter.
    EVOC ("001D00", "EVOC", new DataConverterCO2()),
    CO2 ("001B00", "CO2", new DataConverterCO2()),
    TEST("000700", "PRSN", new DataConverterTemperature());
    */        
    
    /**
     * A map that holds EOJ.hashCode() <-> ctype mappings
     */
    private final Map<Integer, DataConverter> cmap;
    private final Map<Integer, DataConverter> infomap;
    
    private static final ConverterType singleton;
    //I always love seeing this...
    static {
        singleton = new ConverterType();
    }
    
    ConverterType(){
        cmap = new ConcurrentHashMap<>();
        addPolling(new DataConverterTemperature());
        addPolling(new DataConverterHumidity());
        addPolling(new DataConverterVOC());
        addPolling(new DataConverterCO2());
        
        
        infomap = new ConcurrentHashMap<>();
        addNotification(new DataConverterHumanPresence());
    }
    
    private void addPolling(DataConverter converter){
        this.cmap.put(converter.getEOJ().hashCode(), converter);
    }
    
    private void addNotification(DataConverter converter) {
        this.infomap.put(converter.getEOJ().hashCode(), converter);
    }
    
    static DataConverter getPollingConverter(EOJ eoj){
        EOJ normalized = new EOJ(eoj.getClassGroupCode(), eoj.getClassCode(), (byte) 0x00);
        return singleton.cmap.get(normalized.hashCode());
    }
    
    static DataConverter getNotificationConverter(EOJ eoj){
        EOJ normalized = new EOJ(eoj.getClassGroupCode(), eoj.getClassCode(), (byte) 0x00);
        return singleton.infomap.get(normalized.hashCode());
    }
    
}
