package stevenseesall.com.camera;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.text.format.Formatter;

public class ConnectionString {

    private int mGateway;

    public ConnectionString(int gateway) {
        mGateway = gateway;
    }

    public String decrypt(String serverAddress) {
        String gatewayAddress = Formatter.formatIpAddress(mGateway);
        String[] connectionStringArray = splitEveryThirdCharacter(serverAddress);
        String[] gatewayAddressArray = gatewayAddress.split("\\.");
        String[] hostAddressArray = new String[4];

        String ipAddress = "";

        int addrLen = connectionStringArray.length;
        int gatewayLen = 4 - addrLen;
        int j = 0;

        for (int i = 0; i < gatewayLen; i++) {
            hostAddressArray[i] = gatewayAddressArray[i];
        }

        for (int i = gatewayLen; i < 4; i++) {
            String st = connectionStringArray[j];
            st = removeFilling(st);
            hostAddressArray[i] = st;
            j++;
        }

        for (String st: hostAddressArray) {
            ipAddress = ipAddress + '.' + st;
        }

        ipAddress = ipAddress.substring(1, ipAddress.length());

        return ipAddress;
    }

    private String[] splitEveryThirdCharacter(String s) {
        String[] sArray;
        int length;

        if (s.length() % 3 != 0) {
            System.console().printf("Connection string is not a multiple of 3.");
        }

        length = s.length() / 3;
        sArray = new String[length];
        int j = 0;

        for (int i = 0; i < length; i++) {
            sArray[i] = s.substring(j, j + 3);
            j += 3;
        }

        return sArray;
    }

    private String removeFilling(String s) {
        while (s.length() > 1 && s.startsWith("0")) {
            s = s.substring(1, s.length());
        }

        return s;
    }
}
