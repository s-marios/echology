package echology.poller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smarios@jaist.ac.jp
 */
public class InfoServer {

    private final List<String> messageList;
    private final ServerSocket socket;
    private final List<Socket> clientSockets;
    private Thread diseminator;
    private Thread acceptConnections;

    public InfoServer(int port) throws IOException {
        messageList = Collections.synchronizedList(new ArrayList<String>());
        socket = new ServerSocket(port);
        clientSockets = new ArrayList<>();
    }

    public InfoServer() throws IOException {
        this(2345);
    }

    public void addMessage(String message) {
        synchronized (messageList) {
            messageList.add(message);
            messageList.notify();
        }
    }

    private void diseminate() {
        if (clientSockets.isEmpty()) {
            synchronized (messageList) {
                //empty the messageList
                messageList.clear();
            }
            //no clients, no updates, get out of here
            return;
        }
        synchronized (messageList) {
            Iterator<String> msgIterator = messageList.iterator();
            synchronized (clientSockets) {
                while (msgIterator.hasNext()) {
                    //get message and remove it from our list
                    String message = msgIterator.next();
                    msgIterator.remove();

                    Iterator<Socket> clientIterator = clientSockets.iterator();
                    while (clientIterator.hasNext()) {
                        Socket client = clientIterator.next();
                        try {
                            client.getOutputStream().write(message.getBytes());
                        } catch (IOException ex) {
                            //io error occured, most likely our client is dead, remove him
                            System.out.println("Client removed (IO exception): " + client.toString());
                            clientIterator.remove();
                        }
                    } //client iteration
                } //message iteration
            }
        }
    }

    public void start() {
        diseminator = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (InfoServer.this.messageList) {
                        try {
                            messageList.wait();
                            InfoServer.this.diseminate();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InfoServer.class
                                .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        acceptConnections = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket client = socket.accept();
                        client.setTcpNoDelay(true);
                        System.out.println("Client connected: " + client.toString());
                        synchronized (clientSockets) {
                            clientSockets.add(client);
                            clientSockets.notify();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(InfoServer.class
                            .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        acceptConnections.start();
        diseminator.start();
    }

    public void join() {
        try {
            while (diseminator.isAlive() || acceptConnections.isAlive()) {
                if (diseminator.isAlive()) {
                    diseminator.join();
                }
                if (acceptConnections.isAlive()) {
                    acceptConnections.join();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(InfoServer.class
                .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
