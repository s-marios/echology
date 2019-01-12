/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

import infopoller.dataconverter.DataConverter;
import jaist.echonet.RemoteEchonetObject;

/**
 *
 * @author haha
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
