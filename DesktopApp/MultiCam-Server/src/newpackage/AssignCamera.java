package newpackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AssignCamera implements Runnable {
    Boolean isCam1Used = false;
    Boolean isCam2Used = false;
    Boolean isCam3Used = false;
    Boolean isCam4Used = false;
    Boolean isCam5Used = false;
    Boolean isCam6Used = false;
    int noCam = 1;
    int mHeight = 0;
    int mWidth = 0;
    String mIP = "";
    NewJFrame mJFrame;
    int mPort = 0;
    
    public AssignCamera(NewJFrame jf){
        mJFrame = jf;
    }
    
    @Override 
    public void run(){
        ServerSocket serverSocket;
        try {
            while(true){
                serverSocket = new ServerSocket(44444);
                System.out.println("En attente de paquets pour assignation Caméra...");
                try (Socket ipSocket = serverSocket.accept()) {
                    mIP = readIP(ipSocket);
                    System.out.println("IP RECU" + mIP);
                }
                serverSocket.close();
                sendCamera(mIP);
                System.out.println("Cam assignée");
                serverSocket.close();
                (new Thread(new UDPThread(mJFrame, mPort, noCam))).start();
                noCam++;
            }
        }
        catch(IOException e){

        }
    }
    
    public String readIP(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        
        return dis.readLine();
    }

    public void sendCamera(String ip) throws IOException {
        if(mIP != null){
            try (Socket AskSocket = new Socket(ip, 44444); 
                    DataOutputStream AskSocketout = new DataOutputStream(AskSocket.getOutputStream())) {
                if(!isCam1Used){
                    isCam1Used = true;
                    mPort = 40000;
                    AskSocketout.writeInt(mPort);
                }
                else
                    if(!isCam2Used){
                        isCam2Used = true;
                        mPort = 40001;
                        AskSocketout.writeInt(mPort);
                    }
                    else
                        if(!isCam3Used){
                            isCam3Used = true;
                            mPort = 40002;
                            AskSocketout.writeInt(mPort);
                        }
                        else
                            if(!isCam4Used){
                                isCam4Used = true;
                                mPort = 40003;
                                AskSocketout.writeInt(mPort);
                            }
                            else
                                if(!isCam5Used){
                                    isCam5Used = true;
                                    mPort = 40004;
                                    AskSocketout.writeInt(mPort);
                                }
                                else
                                    if(!isCam6Used){
                                        isCam6Used = true;
                                        mPort = 40005;
                                        AskSocketout.writeInt(mPort);
                                    }
            }
        }
    }
}

