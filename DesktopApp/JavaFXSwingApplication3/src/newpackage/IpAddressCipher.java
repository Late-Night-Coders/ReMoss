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
    
    public IpAddressCipher() {
        mIPAddress = getIPAddress();
    }
    
    public String getIPAddress() {
        InetAddress IP;
        String ipAddress = "";
        String ipAddressReal = "";
        
        Enumeration enumNI;  // Enumeration de network interfaces.
        try {
            enumNI = NetworkInterface.getNetworkInterfaces();
            
            while(enumNI.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) enumNI.nextElement();
                Enumeration ee = ni.getInetAddresses();
                
                while(ee.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ee.nextElement();
                    ipAddress = ia.getHostAddress();
                    
                    if (!ipAddress.startsWith("127.") && !ipAddress.startsWith("0.") && !ipAddress.startsWith("0:") && !ipAddress.contains(":")) {
                        mIPAddress = ipAddress;
                    }
                }
             }
        } catch (SocketException ex) {
            Logger.getLogger(IpAddressCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        return mIPAddress;
    }

    public String encryptIPAddress() {
        String[] parts = mIPAddress.split("\\.");
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