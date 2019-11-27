package org.apache.myfaces.blank;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*  The remote algorithm class BSS implements the remote interface BSS_RMI,
in which the actual work of a single process of the distributed algorithm is perform */

public class BSS extends UnicastRemoteObject implements BSS_RMI {

    // vectorClock stores the values of the process local clocks
    // and we get it by getting the index that corresponds to the appropriate proccess
    // NOTE: the local vector clocks are all initialized with 0 when the system starts

    private int[] vectorClock; // Vector clock to represent the state of the whole system by this process.
    private List<String> ipPortList = new ArrayList<String>(); // List of all the ip addresses of the other processes.

    private int n = 2;


    private Set<Message> buffer = new HashSet<Message>(); // Buffer to store not yet delivered messages
    private int increment = 1;	// value by which we increment the local clock
    private int indexLocalClock;

    /*
    Constructor for a single process in a distributed algorithm
    */

    public BSS(List<String> ipPortList, String currentIpPort) throws RemoteException{
        //System.out.println("Process constructor: ");

        // initialise vectorClock ??

        // add that to be the Clock value of this new process on the index that is the id of the process
        this.ipPortList = ipPortList; // The list of IPs and port combinations where the other processes are located.
        int numProcesses = ipPortList.size();
        vectorClock = new int[numProcesses]; // Create a vector clock with zeros corresponding to the logical clock
        //System.out.println(vectorClock[0]);
        //System.out.println(vectorClock[1]);
        // assigning the corresponding local clock index to the current process by looking up its IP and port

        for(int i = 0; i < numProcesses; i++) {
            if (ipPortList.get(i).equals(currentIpPort)) {
                indexLocalClock = i;
            }
        }
    }

    public static void main(String[] args) {
        try{

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void broadcast(Message m) throws RemoteException {
        //System.out.println("Broadcast");

        updLocalClock();
        //System.out.println("clock updated");

        BSS_RMI otherProcess;
        //System.out.println("new process");
        for (int i = 0; i< ipPortList.size(); i++ ){
            // System.out.println(ipPortList.get(i) +"/process");
            //System.out.println(i);
            //System.out.println(indexLocalClock);
            if(i != indexLocalClock){
                try{
                    System.out.println("Broadcasting from " + ipPortList.get(indexLocalClock) + "to process " + ipPortList.get(i));
                    //System.out.println(ipPortList.get(i) +"/process");
                    otherProcess = (BSS_RMI) Naming.lookup(ipPortList.get(i) +"/process"); //TESTING for 2 processes
                    //System.out.println("Creating new message"+ m.getMessage() + m.getVectorClock());
                    Message msgOut = new Message(m.getMessage(),m.getVectorClock());

                   // System.out.println("Message Created " + msgOut);
                    otherProcess.receive(msgOut, indexLocalClock);
                    //System.out.println("Message Received");

                }catch (Exception e) {
                    System.out.println("Broadcast Exception: " + e);
                }
            }
        }


    }

    /*
     Receive a message, check the order (i.e. the received message's clock) and if it satisfies the HB order,
     deliver, otherwise put in buffer
     If HB not satisfied, put in buffer and then call deliver() once HB satisfied
     */

    @Override
    public void receive(Message m, int processID) throws RemoteException {

        System.out.println("Start receiving");


        if (processID == 1) {
            System.out.println("I am sleeping" + m.getMessage());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            //System.out.println("Receiving message"+ m.getMessage() + Arrays.toString(m.getVectorClock()));
            /* check condition for delivery */
            if (deliveryCondition(m, processID)) {
                deliver(m);
                System.out.println("Delivery condition met");

                /* check for messages in buffer that can now be delivered as a result of  delivering the last message */
                while (!buffer.isEmpty()) {
                    for (Message oldMsg : buffer) {
                        if (deliveryCondition(oldMsg, processID)) {

                            deliver(oldMsg);
                        }
                    }
                }
            } else {
                buffer.add(m);
            }
        }

     /*
     Once HB order is satisfied, deliver the message content only, no need to keep the clock value too
     */
     @Override
     public void deliver(Message m) throws RemoteException {

        System.out.println("Start delivering");


        // update its own clock
        updateVClock(m);

        // print the message + the vector clock to confirm the HB relationship
        System.out.println(m.getMessage() + Arrays.toString(m.getVectorClock()));

        // remove from the buffer
        if(buffer.contains(m)) {
            buffer.remove(m);
        }
    }

    /*
     GETTER for the vector clock
     */
    @Override
    public int[] getVClock() throws RemoteException {
        return vectorClock;
    }

    /**
     * GETTER: for local clock value of the current process.
     * @return int of the clock value
     */
    public int getLocalClock() {
        return vectorClock[indexLocalClock];
    }

    /**
     * Update the local clock value with nonce.
     */
    public void updLocalClock() {
        vectorClock[indexLocalClock] += increment;
    }


    /**
     * Checks whether the vector clock received follows the last received message of that process.
     * @param m the message to compare its vector clock with
     * @return TRUE, when the message received was indeed like expected
     */
    private boolean deliveryCondition(Message m, int index) {
        // System.out.println("checkVectorClocks");
        boolean deliverable = true;
        if ((vectorClock[index] + increment != m.getVectorClock()[index])) {
            deliverable = false;
        }
        return deliverable;
    }

    /**
     * Update the vector clock of the current process, by taking the maximum of the elements of both vector clocks.
     * @param m
     */
    private void updateVClock(Message m) {
        //System.out.println("Update vectorClock");
        int length = m.getVectorClock().length;
        for (int i = 0; i < length; i++) {
            if ((vectorClock[i]) < m.getVectorClock()[i]) {
                vectorClock[i] = m.getVectorClock()[i];
            }
        }
    }
    
    
}
