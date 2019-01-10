/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

import jaist.echonet.RemoteEchonetObject;

/**
 *
 * @author haha
 */
public class PollingTarget {

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

    public String formatData(byte[] data) {
        StringBuilder builder = new StringBuilder();
        builder.append(robject.getQueryIp().toString().substring(1));
        builder.append(":");
        builder.append(robject.getEOJ().toString());
        builder.append(":");
        builder.append(String.format("0x%02X", converter.getEPC()));
        builder.append(",");
        builder.append(converter.getType());
        builder.append(",");
        builder.append(converter.doConversion(data));
        builder.append("\n");
        return builder.toString();
    }
}
