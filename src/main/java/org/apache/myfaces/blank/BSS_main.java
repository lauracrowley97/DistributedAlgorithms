/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package org.apache.myfaces.blank;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/*
 The main class BSS_main creates all the processes of the distributed algorithm
 that will run on a single host.
 */

public class BSS_main {
    public static List<String> ipPortList; // global ip and port list of all processes
    static String otherIP = "131.180.230.57";

    private BSS_main() {}
    public static void main (String[] args) {
        ipPortList = new ArrayList<String>();
        ipPortList.add("rmi://localhost:2099");	// local process 1 (MacBook)
        ipPortList.add("rmi://localhost:2021");	// local process 2 (MacBook)
        ipPortList.add("rmi://131.180.230.57:1099"); // Dell process 1
        ipPortList.add("rmi://131.180.230.57:1021"); // Dell process 2


        BSS_RMI process;
        System.out.println("Configuring RMI Registry");

        try {
            // Create and install a security manager

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            Runtime.getRuntime().exec("rmiregistry 2099");
            LocateRegistry.createRegistry(2099);
            String ipPort2099 = "rmi://localhost:2099";
            BSS process1 = new BSS(ipPortList, ipPort2099);
            Naming.rebind(ipPort2099+ "/process", process1);	// own ip
            // Create and install a security manager

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            Runtime.getRuntime().exec("rmiregistry 2021");
            LocateRegistry.createRegistry(2021);
            String ipPort2021 = "rmi://localhost:2021";	// own ip
            BSS process2 = new BSS(ipPortList, ipPort2021);
            Naming.rebind(ipPort2021 + "/process", process2);


            System.out.println("RMI Registry configured");


            // If running server execute the below code once
            int n = 4;
            while(n <3){
                //System.out.println(Arrays.toString(process1.getVClock()));
                process1.broadcast(new Message("---> MESSAGE: Msg 1 from process 1! ", process1.getVClock()));
                process1.broadcast(new Message("---> MESSAGE: Msg 2 from process 1! ", process1.getVClock()));
                try{
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e){
                    System.out.println("error during broadcast: " + e);
                }
                process2.broadcast(new Message("---> MESSAGE: Msg 1 from process 2! ", process2.getVClock()));
                //System.out.println("In Loop");
                n++;
                TimeUnit.SECONDS.sleep(2);

            }
            // do something (broad or specific message of this process)
            // maybe through console

        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}