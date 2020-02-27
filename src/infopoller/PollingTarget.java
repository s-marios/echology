package infopoller;

import infopoller.dataconverter.DataConverter;
import jaist.echonet.RemoteEchonetObject;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class PollingTarget extends InformationTarget {

    /**
     * @return the converter
     */
    public DataConverter getConverter() {
        return converter;
    }

    /**
     * @return the robject
     */
    public RemoteEchonetObject getRemoteObject() {
        return robject;
    }

    private final DataConverter converter;
    private final RemoteEchonetObject robject;

    public PollingTarget(RemoteEchonetObject robject, DataConverter converter) {
        this.robject = robject;
        this.converter = converter;
    }

}
