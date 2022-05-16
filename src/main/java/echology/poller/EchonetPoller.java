package echology.poller;

import echology.poller.dataconverter.Converters;
import echology.poller.dataconverter.DataConverter;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.RemoteEchonetObject;
import java.util.ArrayList;
import java.util.List;
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

    public EchonetPoller(EchonetNode context, InfoServer server) {
        this.context = context;
        this.server = server;
        this.targets = new ArrayList<>();
        this.filter = new ArrayList<>();
        this.pollingInterval = 10000;
    }

    public void startPolling() {
        setup();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(this, 1000, this.pollingInterval, TimeUnit.MILLISECONDS);
    }

    private void setupPolling(List<RemoteEchonetObject> robjects) {

        for (RemoteEchonetObject robject : robjects) {
            for (DataConverter converter : Converters.getConverters(robject, Converters.TYPE.POLLING)) {
                if (isValidConverter(converter)) {
                    targets.add(new PollingTarget(robject, converter));
                }
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
        for (RemoteEchonetObject robject : robjects) {
            for (DataConverter converter : Converters.getConverters(robject, Converters.TYPE.NOTIFICATION)) {
                if (isValidConverter(converter)) {
                    EOJ reoj = robject.getEOJ();
                    NotificationListenerTarget nlt = new NotificationListenerTarget(converter, reoj, server);
                    context.registerForNotifications(null, reoj.getClassGroupCode(), reoj.getClassCode(), reoj.getInstanceCode(), converter.getEPC(), nlt);
                }
            }
        }
    }

    private void setup() {
        List<RemoteEchonetObject> robjects = context.getNodeDiscovery().discoverAllObjectsBlocking();
        for (RemoteEchonetObject robject : robjects) {
            robject.updatePropertyList();
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
