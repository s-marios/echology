package infopoller;

import infopoller.dataconverter.DataConverter;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.RemoteEchonetObject;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class EchonetPoller implements Runnable {

    private final EchonetNode context;
    private final InfoServer server;
    private final List<PollingTarget> targets;
    private long pollingInterval;
    private final List<String> filter;

    public EchonetPoller(InetAddress address, InfoServer server) {
        context = new EchonetNode(address);
        this.server = server;
        this.targets = new ArrayList<>();
        this.filter = new ArrayList<>();
        this.pollingInterval = 10000;
        context.start();
    }

    public void startPolling() {
        setup();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(this, 1000, this.pollingInterval, TimeUnit.MILLISECONDS);
    }

    private void setupPolling(List<RemoteEchonetObject> robjects) {

        for (RemoteEchonetObject robject : robjects) {
            DataConverter converter = ConverterType.getPollingConverter(robject.getEOJ());
            if (isValidConverter(converter)) {
                targets.add(new PollingTarget(robject, converter));
            }
        }

        if (!targets.isEmpty()) {
            long timeout = this.pollingInterval / targets.size();
            System.out.format("Setting query timeout to: %d ms \n", timeout);
            for (PollingTarget target : targets) {
                //set an aggressive timeout for requests
                //make sure they all finish in time
                target.getRemoteObject().setTimeout(timeout);
            }
        }
    }

    private void doPolling() {
        for (PollingTarget target : targets) {
            String formatedData = pollTarget(target);
            if (formatedData != null) {
                System.out.print("formatedData: " + formatedData);
                server.addMessage(formatedData);
            }
        }
    }

    private String pollTarget(PollingTarget target) {
        RemoteEchonetObject robject = target.getRemoteObject();
        DataConverter dc = target.getConverter();

        byte[] result = robject.readProperty(target.getConverter().getEPC());
        if (result != null && result.length != 0) {
            return target.formatData(robject, dc, result);
        }
        //we failed
        System.out.println(String.format("Polling failed for object: %s EOJ: %s", robject.getQueryIp().toString(), robject.getEOJ().toString()));
        return null;
    }

    /**
     * @param pollingInterval the pollingInterval to set in seconds
     */
    public void setPollingInterval(int pollingInterval) {
        //internally it's milliseconds
        this.pollingInterval = pollingInterval * 1000;
    }

    @Override
    public void run() {
        doPolling();
    }

    private void setupNotificationHandling(List<RemoteEchonetObject> robjects) {
        //TODO see TODO on notification listener target
        //handling of the instance byte
        Set<EOJ> eojset = new HashSet<>();
        for (RemoteEchonetObject robject : robjects) {
            eojset.add(robject.getEOJ());
        }

        for (EOJ eoj : eojset) {
            DataConverter nconverter = ConverterType.getNotificationConverter(eoj);
            if (isValidConverter(nconverter)) {
                NotificationListenerTarget nlt = new NotificationListenerTarget(nconverter, eoj, server);
                context.registerForNotifications(null, eoj.getClassGroupCode(), eoj.getClassCode(), eoj.getInstanceCode(), nconverter.getEPC(), nlt);
            }
        }
    }

    private void setup() {
        List<RemoteEchonetObject> robjects = context.getNodeDiscovery().discoverAllObjectsBlocking();
        for (RemoteEchonetObject robject : robjects) {
            System.out.println(String.format("robject: %s eoj: %s", robject.getQueryIp().toString(), robject.getEOJ().toString()));
        }
        setupPolling(robjects);
        setupNotificationHandling(robjects);
    }

    public void setFilter(List<String> filter) {
        this.filter.addAll(filter);
    }

    boolean isValidConverter(DataConverter converter) {
        if (converter == null) {
            return false;
        }
        if (filter.contains(converter.getInfoTypeString())) {
            return false;
        }
        return true;
    }

    public EchonetNode getContext() {
        return context;
    }
}
