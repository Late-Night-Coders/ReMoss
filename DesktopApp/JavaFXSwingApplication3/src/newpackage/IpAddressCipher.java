/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IpAddressCipher {
    private String mIPAddress;
    private String mIPAdrressEncrypted;
    private short mMask;
    
    public IpAddressCipher() {
        mIPAddress = getIPAddress();
        mMask = getIPv4SubnetMask();
    }
    
    public String getIPAddress() {
        InetAddress IP;
        String ipAddressTemp = "";
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
            Logger.getLogger(IpAddressCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        return ipAddressReal;
    }
    
    private Boolean validateIPAddress(String ipAddress) {
        return !ipAddress.startsWith("127.") && !ipAddress.startsWith("0.") && !ipAddress.startsWith("169") && !ipAddress.contains(":");
    }
    
    private short getIPv4SubnetMask() {
        short subnetMask = 0;
        int i = 0;
        
        try {
            Enumeration enumNI = NetworkInterface.getNetworkInterfaces();
            
            while(enumNI.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) enumNI.nextElement();
                Enumeration enumAddresses = ni.getInetAddresses();
                
                while(enumAddresses.hasMoreElements()) {
                    String sType = ni.getInterfaceAddresses().get(i).getAddress().getClass().toString();

                    if (!sType.contains("IPv6")) {   
                        subnetMask = ni.getInterfaceAddresses().get(i).getNetworkPrefixLength();
                    }

                    i++;
                }
            }
        } 
        catch (SocketException ex) {
            Logger.getLogger(IpAddressCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return subnetMask;
    }

    public String encryptIPAddress() throws UnknownHostException {
        String CIDRaddr = mIPAddress + "/" + Integer.toString(mMask);
        System.out.println(CIDRaddr);
        SubnetUtils utils = new SubnetUtils(CIDRaddr);
        String mask = utils.getInfo().getNetmask();
        InetAddress ip = InetAddress.getByName(mIPAddress);
        InetAddress netmask = InetAddress.getByName(mask);
        
        String[] ipAddrParts=mIPAddress.split("\\.");
        String[] maskParts = mask.split("\\.");
        
        String finalIP = "";
        for(int i=0; i < 4; i++){
            int x = Integer.parseInt(ipAddrParts[i]);
            int y = Integer.parseInt(maskParts[i]);
            int z = x & y;
            System.out.println(z);
            if(z != x){
                finalIP += x+".";
            }
        }

        System.out.println(finalIP);
       
        String[] parts = finalIP.split("\\.");
        String hexIPAddress = "";
        
        for (String st: parts) {
            byte[] bStr = st.getBytes(Charset.forName("UTF-8"));
            int i = Integer.parseInt(st);
            String hex = Integer.toHexString(i);
            hexIPAddress = hexIPAddress + '.' + hex;         
        }
        
        int length = hexIPAddress.length();
        hexIPAddress = hexIPAddress.substring(1, length);
        
        return hexIPAddress;
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