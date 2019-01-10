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
        InfoServer server = new InfoServer();
        server.start();

        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            } else {
                address = InetAddress.getByName("192.168.0.1");
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        EchonetPoller poller = new EchonetPoller(address, server);
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
