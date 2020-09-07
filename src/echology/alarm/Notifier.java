/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echology.alarm;

import echology.poller.InfoServer;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.util.Utils;
import java.io.IOException;

/**
 *
 * @author smarios <smarios@jaist.ac.jp>
 */
public class Notifier implements EchoEventListener {

    public static final int NOTIFY_PORT = 3371;
    private final InfoServer server;

    public Notifier(InfoServer server) throws IOException {
        this.server = server;
    }
    

    public void registerWith(EchonetNode context) {
        context.registerForNotifications(null, null, null, null, null, this);
    }

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        return false;
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        StringBuilder message = new StringBuilder("INF,");
        message.append(robject.getQueryIp().getHostAddress());
        message.append(":");
        message.append(robject.getEOJ().toString());
        message.append(String.format(":0x%02X", property.getPropertyCode()));
        message.append(",");
        message.append(Utils.toHexString(property.read()));
        message.append("\n");
        server.addMessage(message.toString());
        return true;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
    }
}