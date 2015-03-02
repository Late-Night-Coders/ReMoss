/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Administrateur
 */
public class AssignCamera implements Runnable {
        @Override 
        public void run(){
            ServerSocket serverSocket;
            try {
                while(true){
                    serverSocket = new ServerSocket(44444);
                    System.out.println("En attente de paquets pour assignation Caméra...");
                    final Socket clientSocket = serverSocket.accept();
                    System.out.println("Pacquet accepté");
                    try{
                        readStr(clientSocket);
                        clientSocket.close();
                        serverSocket.close();
                    }
                    catch(IOException e){
                        System.out.println(e);          
                    }
                }
            }
            catch(Exception e){
                
            }
        }
        
        public void readStr(Socket socket) throws IOException {
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            
            String check = dis.readLine();
            System.out.println("check: " + check);
            if(check != null){
                Socket AskSocket = new Socket(check, 44444);
                DataOutputStream AskSocketout = new DataOutputStream(AskSocket.getOutputStream());
                
                if(!isCam1Used){
                    isCam1Used = true;
                    AskSocketout.writeInt(40000);
                }
                else
                    if(!isCam2Used){
                        isCam2Used = true;
                        AskSocketout.writeInt(40001);
                    }
                    else
                        if(!isCam3Used){
                            isCam3Used = true;
                            AskSocketout.writeInt(40002);
                        }
                        else
                            if(!isCam4Used){
                                isCam4Used = true;
                                AskSocketout.writeInt(40003);
                            }
                            else
                                if(!isCam5Used){
                                    isCam5Used = true;
                                    AskSocketout.writeInt(40004);
                                }
                                else
                                    if(!isCam6Used){
                                        isCam6Used = true;
                                        AskSocketout.writeInt(40005);
                                    }
                
                AskSocketout.close();
                AskSocket.close();
            }
        }
    }
}
