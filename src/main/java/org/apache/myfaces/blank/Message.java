package org.apache.myfaces.blank;

import java.io.Serializable;


public class Message implements Serializable {

    String m;
    int[] clock;

    /**
     * Constructor for the message of the process.
     * @param m the message
     * @param clock the latest known vector clock of the process
     */
    Message(String m, int[] clock){
        this.m = m;
        this.clock = clock;
    }

    /**
     * Getter for the content of the message.
     * @return the actual message
     */
    public String getMessage(){
        return m;
    }

    /**
     * Getter for the vector clock given by the process.
     * @return the actual vector clock
     */
    public int[] getVectorClock(){
        return clock;
    }
}
