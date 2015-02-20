package stevenseesall.com.camera;


public class IPAddressCipher {
    public static String decryptIPAddress(String address) {
        String[] parts = address.split("\\.");
        String ipAddress = "";

        for (String st: parts) {
            int dec = Integer.parseInt(st, 16);
            String sDec = Integer.toString(dec);
            ipAddress = ipAddress + '.' + sDec;
        }

        int length = ipAddress.length();
        ipAddress = ipAddress.substring(1, length);

        return ipAddress;
    }
}
