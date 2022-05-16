package echology.app;

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
    int pollingPort = 2345;
    List<String> filter = new ArrayList<>();
    boolean doPolling = true;
    boolean doProxy = true;
    int proxyPort = 3361;
    boolean notify = true;
    int notifyPort = 3371;

    ParseArgs(String[] args) {
        int arglength = args.length;
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
                        pollingPort = Integer.parseInt(args[++i]);
                        break;
                    case "-pp":
                        proxyPort = Integer.parseInt(args[++i]);
                        break;
                    case "-ppp":
                        notifyPort = Integer.parseInt(args[++i]);
                        break;
                    case "-f":
                        parseFilter(args[++i]);
                        break;
                    case "--no-polling":
                        doPolling = false;
                        break;
                    case "--no-proxy":
                        doProxy = false;
                        break;
                    case "--no-notify":
                        notify = false;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Arguments - ");
        sb.append("poller port: ");
        sb.append(Integer.toString(pollingPort));
        sb.append(", interval: ");
        sb.append(Integer.toString(interval));
        sb.append(", Hostname: ");
        if (address == null) {
            sb.append("(empty)");
        } else {
            sb.append(address);
        }
        sb.append(", filter: ");
        for (String ftype : filter) {
            sb.append(ftype);
            sb.append(" ");
        }
        return sb.toString();
    }
}
