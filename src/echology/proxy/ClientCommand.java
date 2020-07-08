package echology.proxy;

import jaist.echonet.EOJ;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetNode;
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
 * A command received from the client. Contains information necessary to make a
 * an ECHONET Lite query using {@link EchonetNode#makeQuery(jaist.echonet.AbstractEchonetObject, jaist.echonet.AbstractEchonetObject, jaist.echonet.ServiceCode, java.util.List, java.util.List, jaist.echonet.EchoEventListener)
 * makeQuery()}. IP address, ECHONET Lite object, property code and an optional
 * value for set queries.
 *
 * A command target is of the form IP:EOJ:PC[,value]. IP can be a multicast
 * address; EOJ may specify multiple instances using class instance zero; PC is
 * a hexadecimal representation of the property code to be queried, and value is
 * the data used with Set commands.
 *
 * Example of valid commands - set all lights in the network to ON
 * 224.0.23.0:029000:0x80,0x30 - get the operation status of the target
 * 192.168.0.1:001101 192.168.0.1:001101:0x80
 *
 * Responses follow the same RESULT,IP:EOJ:PC[,value] where RESULT := [OK|NG]
 * for success and failure respectively
 *
 * example: - OK,192.168.0.105:001101:0xE0,0x00F3
 *
 * @author smarios@jaist.ac.jp
 */
public class ClientCommand implements EchoEventListener {

    private SelectionKey client_key;

    /**
     * The target of this command (IP:EOJ:EPC combination), as a string
     */
    private final String target;

    /**
     * An optional field representing the set data used in set commands
     */
    private byte[] value;

    //target ip
    private InetAddress ip;
    //target eoj
    private final EOJ eoj;
    //target property code
    private final byte property_code;

    //example command: "IP:EOJ:PropCode[,Value]
    ClientCommand(String command) throws InvalidTargetException {
        StringTokenizer commandTokenizer = new StringTokenizer(command, ",");
        try {
            this.target = commandTokenizer.nextToken();
        } catch (NoSuchElementException ex) {
            throw new InvalidTargetException(null);
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
            Logger.getLogger(ClientCommand.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidTargetException("invalid IP address: " + ipstring);
        }

        try {
            this.eoj = new EOJ(eojstring);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ClientCommand.class.getName()).log(Level.SEVERE, null, ex);
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

            //regeneratee target, in case of multicasts and/or multiple targets
            char_response.append(answer.getResponder().getQueryIp().getHostAddress());
            char_response.append(":");
            char_response.append(answer.getResponder().getEOJ().toString());

            for (EchonetProperty property : answer.getProperties()) {
                char_response.append(String.format(":0x%02X", property.getPropertyCode()));
                
                byte[] property_data = property.read();
                if (property_data != null && property_data.length > 0) {
                    char_response.append(",");

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
            Logger.getLogger(ClientCommand.class.getName()).log(Level.SEVERE, null, ex);
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
