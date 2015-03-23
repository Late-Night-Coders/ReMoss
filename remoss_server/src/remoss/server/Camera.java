/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package remoss.server;

/**
 *
 * @author Administrateur
 */
public class Camera {
    int mID;
    int mPort;
    boolean mRunning = false;
    
    public Camera(int id, int port){
        mID = id;
        mPort = port;
    }
    
    public boolean AssignCamera(){
        return true;
    }
    
    public boolean DeAssignCamera(){
        return true;
    }

}
