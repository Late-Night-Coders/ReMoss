package stevenseesall.com.camera;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.text.format.Formatter;

public class IPAddressCipher {

    public int mGateway;

    public IPAddressCipher(Context context) {
        WifiManager mWifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo mDhcpInfo = mWifiMgr.getDhcpInfo();
        int mNetmask = mDhcpInfo.netmask;
        mGateway = mDhcpInfo.gateway;
    }

    public String decryptIPAddress(String serverAddress) {

        String gatewayAddr = Formatter.formatIpAddress(mGateway);
        String[] splittedAddr = serverAddress.split("\\.");
        String[] splittedGateway = gatewayAddr.split("\\.");
        String[] splittedFinalAddr = new String[4];

        String ipAddress = "";

        int addrLen = splittedAddr.length;
        int gatewayLen = 4 - addrLen;
        int j = 0;

        for (int i = 0; i < gatewayLen; i++) {
            splittedFinalAddr[i] = splittedGateway[i];
        }

        for (int i = gatewayLen; i < 4; i++) {
            String st = splittedAddr[j];
            int dec = Integer.parseInt(st, 16);
            String sDec = Integer.toString(dec);
            splittedFinalAddr[i] = sDec;
            j++;
        }

        for (String st: splittedFinalAddr) {
            ipAddress = ipAddress + '.' + st;
        }

        ipAddress = ipAddress.substring(1, ipAddress.length());

        return ipAddress;
    }
}
