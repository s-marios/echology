package echology.app;

import echology.poller.EchonetPoller;
import echology.poller.InfoServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import echology.proxy.EchonetProxy;

/**
 * A polling and command proxy application
 *
 * @author smarios@jaist.ac.jp
 */
public class PollProxy {

    public static void main(String[] args) throws IOException {

        System.out.println("USAGE: [-t timeinterval] [-i IP] [-p port] [-f filterstring]");
        System.out.println("  filterstring is a comma-separated list of data type");
        System.out.println("  example: TMP,VOC,C02,HMDT etc.\r\n");

        ParseArgs pargs = new ParseArgs(args);
        System.out.println(pargs.toString());
        System.out.println("Default proxy port is: " + EchonetProxy.PROXY_PORT);

        InfoServer server = new InfoServer(pargs.port);
        server.start();

        EchonetPoller poller = new EchonetPoller(pargs.address, server);
        poller.setPollingInterval(pargs.interval);
        poller.setFilter(pargs.filter);
        poller.startPolling();

        EchonetProxy proxy = new EchonetProxy();
        proxy.start(poller.getContext());

        if (false) {
            //testing..
            Thread testing = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        try {
                            Thread.sleep(3000);
                            server.addMessage("Message: " + i++ + "\r\n");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PollProxy.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            testing.start();
        }

    }

}
