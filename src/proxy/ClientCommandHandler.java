/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import jaist.echonet.EOJ;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.ServiceCode;
import jaist.echonet.util.Utils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class ClientCommandHandler implements EchoEventListener {

    private SelectionKey client_key;
    private final String target;
    private final EOJ eoj;
    private byte[] value;
    private final byte property_code;
    private InetAddress ip;

    //example command: "IP:EOJ:PropCode[,Value]
    ClientCommandHandler(String command) throws InvalidTargetException {
        StringTokenizer commandTokenizer = new StringTokenizer(command,",");
        try {
            this.target = commandTokenizer.nextToken();
        } catch (NoSuchElementException ex) {
            throw new InvalidTargetException("null target");
        }
        //handle the target: ip, eoj, property_code
        StringTokenizer tokenizer = new StringTokenizer(target, ":");

        String ipstring = null;
        String eojstring = null;
        String property_code_string = null;

        try {
            ipstring = tokenizer.nextToken();
            eojstring = tokenizer.nextToken();
            property_code_string = tokenizer.nextToken();
        } catch (NoSuchElementException ex) {
            if (ipstring == null || eojstring == null) {
                throw new InvalidTargetException("bad command target: " + target);
            }
            if (property_code_string == null) {
                throw new InvalidTargetException("no property code");
            }
        }

        byte[] propCodeAsBytes = Utils.hexStringToByteArray(property_code_string);
        //bytes are signed, thus we actually expect 0x80 -0xFF range, thus 'negative' or zero
        if (propCodeAsBytes == null || propCodeAsBytes.length == 0 || propCodeAsBytes[0] > 0) { 
            throw new InvalidTargetException("invalid property code: " + property_code_string);
        } else {
            this.property_code = propCodeAsBytes[0];
        }

        try {
            this.ip = InetAddress.getByName(ipstring);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidTargetException("invalid IP address: " + ipstring);
        }

        try {
            this.eoj = new EOJ(eojstring);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidTargetException("invalid EOJ: " + eojstring);
        }

        //handling the optional value argument
        try {
            value = Utils.hexStringToByteArray(commandTokenizer.nextToken());
        } catch (NoSuchElementException ex) {
            //this is fine
            value = null;
        }

    }

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        ByteBuffer response;
        if (answer == null) {
            response = ByteBuffer.allocate(16);
            response.asCharBuffer().append("NG\n");
        } else {
            response = ByteBuffer.allocate(512);
            CharBuffer char_response = response.asCharBuffer();
            
            ServiceCode responseCode = answer.getResponseCode();
            if (responseCode == ServiceCode.Get_Res || responseCode == ServiceCode.Set_Res) {
                char_response.append("OK,");
            } else {
                char_response.append("NG,");
            }
            
            //reuse target, eoj from before
            char_response.append(target);
            char_response.append(",");

            for (EchonetProperty property : answer.getProperties()) {
                byte[] property_data = property.read();
                if (property_data != null) {
                    //we got some data back
                    //convert to string and put in the buffer
                    char_response.append(Utils.toHexString(property_data));
                }
            }
            char_response.append("\n");
        }

        try {
            ((SocketChannel) getClient_key().channel()).write(response);
        } catch (IOException ex) {
            Logger.getLogger(ClientCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("target: ");
        sb.append(this.target);
        sb.append(", eoj: ");
        sb.append(this.eoj);
        if (this.value != null) {
            sb.append(", value: ");
            sb.append(Utils.toHexString(this.value));
        }
        return sb.toString();
    }

    public EOJ getEOJ() {
        return this.eoj;
    }

    public InetAddress getIp() {
        return this.ip;
    }

    public byte[] getValue() {
        return this.value;
    }

    byte getEPC() {
        return this.property_code;
    }

    /**
     * @return the client_key
     */
    SelectionKey getClient_key() {
        return client_key;
    }

    /**
     * @param client_key the client_key to set
     */
    void setClient_key(SelectionKey client_key) {
        this.client_key = client_key;
    }

}
