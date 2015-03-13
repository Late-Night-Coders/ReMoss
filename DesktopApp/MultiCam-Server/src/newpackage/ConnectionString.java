package newpackage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionString {
    private final String mIPAddress;
    private final short mMask;
    
    public ConnectionString() {
        mIPAddress = getIPAddress();
        mMask = getIPv4SubnetMask();
    }
    
    public String getIPAddress() {
        String ipAddressTemp;
        String ipAddressReal = "";
        
        try {
            Enumeration enumNI = NetworkInterface.getNetworkInterfaces();
            
            while(enumNI.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) enumNI.nextElement();
                Enumeration enumAddresses = ni.getInetAddresses();
                
                while(enumAddresses.hasMoreElements()) {
                    Object elem = enumAddresses.nextElement();
                    InetAddress ia = (InetAddress) elem;
                    ipAddressTemp = ia.getHostAddress();                      
                    if (validateIPAddress(ipAddressTemp)) {
                        ipAddressReal = ipAddressTemp;
                    }
                }       
            }
        } 
        catch (SocketException ex) {
            Logger.getLogger(ConnectionString.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        return ipAddressReal;
    }
    
    private Boolean validateIPAddress(String ipAddress) {
        return !ipAddress.startsWith("127.") && !ipAddress.startsWith("0.") && 
                !ipAddress.startsWith("169") && !ipAddress.contains(":");
    }
    
    private short getIPv4SubnetMask() {
        short subnetMask = 0;
        String sType;
        
        try {
            Enumeration enumNI = NetworkInterface.getNetworkInterfaces();
            
            while(enumNI.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) enumNI.nextElement();
                Enumeration enumAddresses = ni.getInetAddresses();
                int i = 0;
                while(enumAddresses.hasMoreElements()) {  
                    sType = ni.getInterfaceAddresses().get(i).getAddress().getClass().toString();

                    if (!sType.contains("Inet6Address")) {   
                        subnetMask = ni.getInterfaceAddresses().get(i).getNetworkPrefixLength();
                        if(System.getProperty("os.name").toLowerCase().contains("windows")){
                            subnetMask += 8;
                        }
                        break;
                    }
                    i++;
                }
                if(subnetMask != 0){
                    break;
                }
            }

        } 
        catch (SocketException ex) {
            Logger.getLogger(ConnectionString.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return subnetMask;
    }
    
    private String createConnectionString(String[] ipAddress, String[] mask) {
        String connectionString = "";
        
        for (int i = 0; i < 4; i++) {
            int x = Integer.parseInt(ipAddress[i]);
            int y = Integer.parseInt(mask[i]);
            int z = x & y;

            if(z != x){
                String st = Integer.toString(x);
                connectionString += fillWithZeros(st);
            }
        }
        
        return connectionString;
    }

    public String encrypt() throws UnknownHostException {
        
        String CIDRaddr = mIPAddress + "/" + Integer.toString(mMask);
        SubnetUtils utils = new SubnetUtils(CIDRaddr);
        String mask = utils.getInfo().getNetmask();
        
        String[] ipAddressArray = mIPAddress.split("\\.");
        String[] maskArray = mask.split("\\.");
        
        String connectionString = createConnectionString(ipAddressArray, maskArray);
        
        return connectionString;
    }
    
    private String fillWithZeros(String s) {
        while (s.length() < 3) {
            s = "0" + s;
        }
        
        return s;
    }
    
    public static int byteArrayToInt(byte[] b) 
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }
    
    public static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);   
        ret[1] = (byte) ((a >> 8) & 0xFF);   
        ret[2] = (byte) ((a >> 16) & 0xFF);   
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }
}