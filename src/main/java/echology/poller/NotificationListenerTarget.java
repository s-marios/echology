package echology.poller;

import echology.poller.dataconverter.DataConverter;
import jaist.echonet.EOJ;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class NotificationListenerTarget extends InformationTarget implements EchoEventListener {

    private final InfoServer server;
    private final DataConverter converter;
    private final EOJ eoj;

    public NotificationListenerTarget(DataConverter converter, EOJ eoj, InfoServer server) {
        this.converter = converter;
        this.server = server;
        this.eoj = eoj;
    }

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        throw new UnsupportedOperationException("Misused listener. Fix your code.");
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        //TODO appropriate handling of the instance byte, i.e. aggregate on class code.
        if (!robject.getEOJ().equals(this.eoj) || property.getPropertyCode() != converter.getEPC()) {
            return false;
        }
        //we have a match, process it!
        byte[] data = property.read();
        server.addMessage(formatData(robject, converter, data));
        return true;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        throw new UnsupportedOperationException("Misused listener. Fix your code.");
    }

}
