package echology.app;

import echology.alarm.Notifier;
import echology.poller.EchonetPoller;
import echology.poller.InfoServer;
import java.io.IOException;
import echology.proxy.EchonetProxy;
import jaist.echonet.EchonetNode;

/**
 * A polling and command proxy application
 *
 * @author smarios@jaist.ac.jp
 */
public class PollProxy {

    public static void main(String[] args) throws IOException {

        System.out.println("USAGE: [--no-polling] [--no-proxy] [--no-notify] [-t timeinterval] [-i IP] [-p pollingport] [-pp proxyport] [-ppp notifyport] [-f filterstring]");
        System.out.println("  filterstring is a comma-separated list of data type");
        System.out.println("  filterstring example: TMP,VOC,C02,HMDT etc.\r\n");

        ParseArgs pargs = new ParseArgs(args);
        System.out.println(pargs.toString());

        if (!(pargs.doPolling || pargs.doProxy || pargs.notify)) {
            System.out.println("Warning: all operation modes disabled. Exiting...");
            System.exit(1);
        }

        EchonetNode context = new EchonetNode(pargs.address);
        context.start();

        if (pargs.doPolling) {
            System.out.println("Polling port is: " + pargs.pollingPort);
            InfoServer server = new InfoServer(pargs.pollingPort);
            server.start();

            EchonetPoller poller = new EchonetPoller(context, server);
            poller.setPollingInterval(pargs.interval);
            poller.setFilter(pargs.filter);
            poller.startPolling();
        }

        if (pargs.doProxy) {
            System.out.println("Proxy port is: " + pargs.proxyPort);
            EchonetProxy proxy = new EchonetProxy(pargs.proxyPort);
            proxy.start(context);
        }

        if (pargs.notify) {
            System.out.println("Notification port is: " + pargs.notifyPort);
            InfoServer server = new InfoServer(pargs.notifyPort).buffering(false);
            server.start();

            Notifier notifier = new Notifier(server);
            notifier.registerWith(context);
        }

    }

}
