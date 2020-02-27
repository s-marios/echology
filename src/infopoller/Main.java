package infopoller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import proxy.EchonetProxy;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("USAGE: [-t timeinterval] [-i IP] [-p port] [-f filterstring]\r\n");

        ParseArgs pargs = new ParseArgs(args);
        System.out.println(pargs.toString());

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
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            testing.start();
        }

    }

}
