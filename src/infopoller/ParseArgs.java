package infopoller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author smarios@jaist.ac.jp
 */
class ParseArgs {

    int interval = 5;
    InetAddress address = null;
    int port = 2345;
    List<String> filter = new ArrayList<>();

    ParseArgs(String[] args) {
        int arglength = args.length;
        this.setupDefaultAddress();
        for (int i = 0; i < arglength; i++) {
            try {
                switch (args[i]) {
                    case "-t":
                        interval = Integer.parseInt(args[++i]);
                        break;
                    case "-i":
                        address = InetAddress.getByName(args[++i]);
                        break;
                    case "-p":
                        port = Integer.parseInt(args[++i]);
                        break;
                    case "-f":
                        parseFilter(args[++i]);
                        break;
                    default:
                        System.out.println("Unrecognized option: " + args[i]);
                }
            } catch (NumberFormatException ex) {
                System.out.printf("Bad number: %s, ignoring option...\n", args[i]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                //we're out of bounds, let's wrap this up and 
                //return whatever we managed to setup.
                System.out.println("Index out of bounds, stopping further arg processing...");
                return;
            } catch (UnknownHostException ex) {
                System.out.printf("Bad ip address/hostname specified: %s, ignoring..\n", args[i]);
            }
        }
    }

    private void parseFilter(String arg) {
        this.filter = new ArrayList<>();
        StringTokenizer strtok = new StringTokenizer(arg, ",");
        while (strtok.hasMoreTokens()) {
            filter.add(strtok.nextToken());
        }
    }

    private void setupDefaultAddress() {
        try {
            address = InetAddress.getByName("192.168.0.1");
        } catch (UnknownHostException ex) {
            System.out.println("It's not happening.");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParseArgs ");
        sb.append("port: ");
        sb.append(Integer.toString(port));
        sb.append(" interval: ");
        sb.append(Integer.toString(interval));
        sb.append(" Hostname: ");
        sb.append(address.toString());
        sb.append(" filter: ");
        for (String ftype : filter) {
            sb.append(ftype);
            sb.append(" ");
        }
        return sb.toString();
    }
}
