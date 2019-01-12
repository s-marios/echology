/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author haha
 */
public class EchonetPoller implements Runnable{

    private final EchonetNode context;
    private final InfoServer server;
    private final List<PollingTarget> targets;

    public EchonetPoller(InetAddress address, InfoServer server) {
        context = new EchonetNode(address);
        this.server = server;
        this.targets = new ArrayList<>();
        context.start();
    }

    public void startPolling() {
        setup();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(this, 1, 10, TimeUnit.SECONDS);
    }

    private void setupPolling(List<RemoteEchonetObject> robjects) {
       
        for (RemoteEchonetObject robject : robjects) {
            DataConverter converter = ConverterType.getPollingConverter(robject.getEOJ());
            if (converter != null) {
                targets.add(new PollingTarget(robject, converter));
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

        return null;
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
            if (nconverter != null) {
                NotificationListenerTarget nlt = new NotificationListenerTarget(nconverter, eoj, server);
                context.registerForNotifications(null, eoj.getClassGroupCode(), eoj.getClassCode(), eoj.getInstanceCode(), nconverter.getEPC(), nlt); 
            }
        }
    }
    
    private void setup(){
        List<RemoteEchonetObject> robjects = context.getNodeDiscovery().discoverAllObjectsBlocking();
        setupPolling(robjects);
        setupNotificationHandling(robjects);
    }

}
