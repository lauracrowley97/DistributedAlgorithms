package org.apache.myfaces.blank;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BSS_main_client {
    public static List<String> ipPortList; //containing all the ips and ports in use

    public static void main(String[] args) {
        ipPortList = new ArrayList<String>();
        ipPortList.add("rmi://145.94.205.254:2099");      // MacBook process 1
        ipPortList.add("rmi://145.94.205.254:2021");      // MacBook process 2
        ipPortList.add( "rmi://localhost:1099");          //local process 1 (Dell)
        ipPortList.add( "rmi://localhost:1021");           //local process 2 (Dell)

        BSS_RMI process;
        try {
            //security manager?
            Runtime.getRuntime().exec("rmiregistry 1099");
            LocateRegistry.createRegistry(1099);
            String ipPort1099 = "rmi://localhost:1099";
            BSS process1 = new BSS(ipPortList, ipPort1099);
            Naming.rebind(ipPort1099 + "/process", process1);

            //security manager?

            Runtime.getRuntime().exec("rmiregistry 1021");
            LocateRegistry.createRegistry(1021);
            String ipPort1021 = "rmi://localhost:1021";
            BSS process2 = new BSS(ipPortList, ipPort1021);
            Naming.rebind(ipPort1021 + "/process", process2);

            TimeUnit.SECONDS.sleep(30);
            //process1.broadcast(new Message("testing across machines", process1.getVClock()));

        } catch(Exception e)  {
            System.out.println("Client Exception: "+e);

        }
    }
}
