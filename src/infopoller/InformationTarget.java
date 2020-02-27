package infopoller;

import infopoller.dataconverter.DataConverter;
import jaist.echonet.RemoteEchonetObject;

/**
 *
 * @author smarios@jaist.ac.jp
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
