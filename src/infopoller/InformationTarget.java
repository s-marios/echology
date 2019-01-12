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
public class InformationTarget {

    public String formatData(RemoteEchonetObject robject, DataConverter converter, byte[] data) {
        StringBuilder builder = new StringBuilder();
        builder.append(robject.getQueryIp().toString().substring(1));
        builder.append(":");
        builder.append(robject.getEOJ().toString());
        builder.append(":");
        builder.append(String.format("0x%02X", converter.getEPC()));
        builder.append(",");
        builder.append(converter.getInfoTypeString());
        builder.append(",");
        builder.append(converter.doConversion(data));
        builder.append("\n");
        return builder.toString();
    }
    
}
