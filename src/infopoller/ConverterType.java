package infopoller;

import infopoller.dataconverter.DataConverter;
import infopoller.dataconverter.DataConverterCO2;
import infopoller.dataconverter.DataConverterHumanPresence;
import infopoller.dataconverter.DataConverterHumidity;
import infopoller.dataconverter.DataConverterLux;
import infopoller.dataconverter.DataConverterTemperature;
import infopoller.dataconverter.DataConverterVOC;
import infopoller.dataconverter.DataConverterDistance;
import infopoller.dataconverter.DataConverterOperationStatus;
import jaist.echonet.EOJ;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class ConverterType {

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

    ConverterType() {
        cmap = new ConcurrentHashMap<>();
        addPolling(new DataConverterTemperature());
        addPolling(new DataConverterHumidity());
        addPolling(new DataConverterVOC());
        addPolling(new DataConverterCO2());
        addPolling(new DataConverterLux());
        addPolling(new DataConverterOperationStatus("029000", "LGHT"));

        infomap = new ConcurrentHashMap<>();
        addNotification(new DataConverterHumanPresence());
        addNotification(new DataConverterDistance());
        //addNotification(new DataConverterOperationStatus("029000", "LGHT"));
    }

    private void addPolling(DataConverter converter) {
        this.cmap.put(converter.getEOJ().hashCode(), converter);
    }

    private void addNotification(DataConverter converter) {
        this.infomap.put(converter.getEOJ().hashCode(), converter);
    }

    static DataConverter getPollingConverter(EOJ eoj) {
        EOJ normalized = new EOJ(eoj.getClassGroupCode(), eoj.getClassCode(), (byte) 0x00);
        return singleton.cmap.get(normalized.hashCode());
    }

    static DataConverter getNotificationConverter(EOJ eoj) {
        EOJ normalized = new EOJ(eoj.getClassGroupCode(), eoj.getClassCode(), (byte) 0x00);
        return singleton.infomap.get(normalized.hashCode());
    }

}
