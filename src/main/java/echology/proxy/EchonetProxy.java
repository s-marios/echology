package echology.proxy;

import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.EchonetDummyProperty;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.ServiceCode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An ECHONET Lite command proxy. Having fun with some channels and java's nio.
 *
 * @author Marios Sioutis
 */
public class EchonetProxy implements Runnable {

    public static final int PROXY_PORT = 3361;
    private static final int POLL_TIMEOUT = 1000;
    private final ServerSocketChannel server_channel;
    private final Selector selector;
    private boolean done;
    private EchonetNode context;
    private Map<SelectionKey, Client> client_buffers;

    private static final boolean NO_BLOCKING = false;

    public EchonetProxy(int port) throws IOException {
        selector = Selector.open();
        server_channel = ServerSocketChannel.open();
        server_channel.configureBlocking(NO_BLOCKING);
        server_channel.bind(new InetSocketAddress(port));
        server_channel.register(selector, SelectionKey.OP_ACCEPT);

        client_buffers = new HashMap<>();
    }

    public EchonetProxy() throws IOException {
        this(PROXY_PORT);
    }

    @Override
    public void run() {
        this.done = false;
        while (!done) {
            try {
                selector.select(POLL_TIMEOUT);
                //BUGFIX iterate over the keys with iterator to avoid concurrent modificaiton exception
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    //remove the key from the selected keys set...
                    keys.remove();
                    //...and process it.
                    if (key.isAcceptable() && key.channel() instanceof ServerSocketChannel) {
                        //our server socket has a client wanting to connect
                        //accept and register with the selector
                        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                        client.configureBlocking(NO_BLOCKING);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("New Proxy Client Connected: " + client.getRemoteAddress().toString());
                    } else if (key.isReadable()) {
                        //a client send us stuff to do
                        //read its stuff and pass it to a que for further processing
                        int bytes_read = readFromClient(key);
                        if (bytes_read == -1) {
                            //our client disconnected. deal with it.
                            String remote_address = ((SocketChannel) key.channel()).getRemoteAddress().toString();
                            System.out.println("Proxy Client Disconnected: " + remote_address);
                            key.channel().close();
                            //let's cancel this key and never speak of it again.
                            key.cancel();
                            //remove the client from our list
                            client_buffers.remove(key);
                        }
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(EchonetProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void start(EchonetNode context) {
        this.context = context;

        Thread server_thread = new Thread(this);
        server_thread.start();
    }

    /**
     *
     * @param key
     * @return bytes read from client, -1 for disconnect
     * @throws IOException
     */
    int readFromClient(SelectionKey key) throws IOException {
        //get the client command buffer or make one if none's present
        Client client_buffer = client_buffers.get(key);
        if (client_buffer == null) {
            client_buffer = new Client(key);
            client_buffers.put(key, client_buffer);
        }

        int result = client_buffer.receiveData();

        for (ClientCommand command : client_buffer.getCommands()) {
            System.out.println("command: " + command.toString());

            //perform whatever the client command wants us to
            executeClientCommand(command);
        }

        return result;
    }

    private void executeClientCommand(ClientCommand command) {
        //get our remote object, the target of this command
        RemoteEchonetObject remoteObject = context.getRemoteObject(command.getIp(), command.getEOJ());

        //start by setting up a get operation for this property
        ServiceCode service = ServiceCode.Get;
        EchonetProperty property = new EchonetDummyProperty(command.getEPC());

        //if we do have a value, change to Set opertation
        if (command.getValue() != null) {
            service = ServiceCode.SetC;
            property = new EchonetCharacterProperty(command.getEPC(), true, true, false, command.getValue());
        }

        //perform the actual command
        List<EchonetProperty> propertyList = Collections.singletonList(property);
        context.makeQuery(context.getNodeProfileObject().getEchonetObject(),
            remoteObject, service, propertyList, null, command);
    }

}
