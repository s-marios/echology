package echology.poller.dataconverter;

import jaist.echonet.EOJ;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of the available converters in the system.
 *
 * Retrieve available converters using the {@link Converters#getConverters(jaist.echonet.RemoteEchonetObject, echology.poller.dataconverter.Converters.TYPE) 
 * getConverters} static method. Register new converters in the constructor of
 * this class.
 *
 * @author smarios@jaist.ac.jp
 */
public class Converters {

    /**
     * Converter type
     */
    public enum TYPE {
        /**
         * Fixed interval polling converter
         */
        POLLING,
        /**
         * Notification-based converter using callbacks
         */
        NOTIFICATION,
    }

    /**
     * A map that holds computeHash() to converters mappings
     */
    private final Map<Integer, DataConverter> cmap;
    private final Map<Integer, DataConverter> infomap;

    private static final Converters singleton;

    //I always love seeing this...
    static {
        singleton = new Converters();
    }

    Converters() {
        cmap = new ConcurrentHashMap<>();
        addPolling(new DataConverterTemperature());
        addPolling(new DataConverterByte("001200", (byte) 0xE0, "HMDT"));
        addPolling(new DataConverterShort("001D00", (byte) 0xE0, "VOC"));
        addPolling(new DataConverterShort("001B00", (byte) 0xE0, "CO2"));
        addPolling(new DataConverterShort("000D00", (byte) 0xE0, "LUX"));
        //custom monotonic property on the node profile
        addPolling(new DataConverterInt("0EF000", (byte) 0xFE, "MONO"));
        addPolling(new DataConverterOperationStatus("029000", "LGHT"));

        infomap = new ConcurrentHashMap<>();
        addNotification(new DataConverterHumanPresence());
    }

    private void addPolling(DataConverter converter) {
        this.cmap.put(computeHash(converter.getEOJ(), converter.getEPC()), converter);
    }

    private void addNotification(DataConverter converter) {
        this.infomap.put(computeHash(converter.getEOJ(), converter.getEPC()), converter);
    }

    /**
     * Retrieve a list of the available converters for this remote object. Make
     * sure you have called
     * {@link RemoteEchonetObject#updatePropertyList() updatePropertyList()}
     * on the remote object before you use this method!
     *
     * @param robject the remote object of interest (instance code is ignored)
     * @param converter_type the type of the converter requested, polling or
     * notification-based
     * @return a list of the converters of type converter_type known for this
     * remote object class
     */
    public static Iterable<DataConverter> getConverters(RemoteEchonetObject robject, TYPE converter_type) {
        List<DataConverter> converters = new ArrayList<>();
        EOJ eoj = robject.getEOJ().getEOJWithInstanceCode((byte) 0x00);

        Map<Integer, DataConverter> map = converter_type == TYPE.POLLING ? singleton.cmap : singleton.infomap;
        for (EchonetProperty property : robject.getPropertyList()) {
            converters.add(map.get(computeHash(
                    eoj,
                    property.getPropertyCode())
            ));
        }
        return converters;
    }

    static private int computeHash(EOJ eoj, byte property_code) {
        //TODO this should be enough, but lol, what a hash code
        //look into more proper hashing
        return eoj.hashCode() * property_code * 13;
    }

    /**
     * Register new converters. Intended for use by external packages
     *
     * @param converter the new converter to register
     * @param type type of the converter, polling or notification-based
     */
    public static void register(DataConverter converter, TYPE type) {
        switch (type) {
            case POLLING:
                singleton.addPolling(converter);
                break;
            case NOTIFICATION:
                singleton.addNotification(converter);
                break;
        }
    }
}
