package org.apache.myfaces.blank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/* The remote interface BSS_RMI defines the methods that can be called remotely. */

public interface BSS_RMI extends Remote {



    //For lab
    public void broadcast (Message m) throws RemoteException;
    public void receive (Message m, int processID) throws RemoteException;
    public void deliver (Message m) throws RemoteException;

    public int[] getVClock() throws RemoteException;

}
