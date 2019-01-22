/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("USAGE: [timeinterval] [IP] [port]\r\n");
        
        //argument parsing
        //args[0] interval polling
        int interval = 5;
        if (args.length >= 1) {
            interval = Integer.parseInt(args[0]);
        }
        
        //args[1] ip address
        InetAddress address = null;
        try {
            if (args.length >= 2) {
                address = InetAddress.getByName(args[1]);
            } else {
                address = InetAddress.getByName("192.168.0.1");
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //args[2] port number
        int port = 2345;
        if (args.length >= 3) {
            port = Integer.parseInt(args[2]);
        }
        
        InfoServer server = new InfoServer(port);
        server.start();
        
        EchonetPoller poller = new EchonetPoller(address, server);
        poller.setPollingInterval(interval);
        poller.startPolling();

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
