/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clockmainserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClockMainServer {

    static int processCount =0;
    static int activeProcess =0;
    static String[] processAddress =  new String[50];
    static String[] tempAddress =  new String[50];
    static int[] processPort =  new int[50];
    static int[] tempPort =  new int[50];
   
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("Main Server start, port: 8080");
            for(int z =0 ;z<50;z++)
            {
                processAddress[z] = null;
                tempAddress[z] = null;
                processPort[z] = 0;
                tempPort[z] = 0;
            }
            
            processCount = 0;
            
            Thread processThread = new Thread (new service());
            processThread.start();
                
            while (processCount < 50) {
                
                Socket sc = ss.accept();
                System.out.println(processCount);
                DataInputStream dis = new DataInputStream(sc.getInputStream());
                DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
                
                processAddress[processCount] = dis.readUTF();
		processPort[processCount] = dis.readInt();
                System.out.println("Process Address: "+processAddress[processCount]);
                System.out.println("Process port : "+processPort[processCount]);

                sc.close();
                
                processCount++;
                //System.out.println("adding one, so get:  "+ processCount);
            }   
        } catch (IOException ex) {
            //Logger.getLogger(ClockMainServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Main Server starting error");
        }
        
    }
    
}

class service implements Runnable {

    Socket sc;
    int count = 0;
    DataInputStream dis ;
    DataOutputStream dos;
    String str;
    
    public service(){
        System.out.println("Main Server Thread start");
    }
    
    public void run() {
        
        while (true)
        {
            
            // ekhane active array ta check korbe, check kore count rekhe rekhe temp e save korbe, then every server alada vabe connect kore array pathabe
            if(ClockMainServer.processCount>1){
                ClockMainServer.activeProcess =0;
                for(int i=0;i<ClockMainServer.processCount;i++){
                    ClockMainServer.tempAddress[i] = null;
                    ClockMainServer.tempPort[i] = 0 ; 
                }

                for(int i=0;i<ClockMainServer.processCount;i++){
                    for(int kk =i+1; kk<ClockMainServer.processCount; kk++)
                    {
                        if (ClockMainServer.processPort[i] == ClockMainServer.processPort[kk])
                        {
                            ClockMainServer.processPort[kk] = 10;
                        }
                    }
                    try {
                        sc = new Socket(ClockMainServer.processAddress[i], ClockMainServer.processPort[i]);
                    } catch (IOException ex) {
                        System.out.println("Error connection :"+ClockMainServer.processAddress[i]+","+ClockMainServer.processPort[i]);
                        continue;
                    }
                    
                    ClockMainServer.tempAddress[ClockMainServer.activeProcess] = ClockMainServer.processAddress[i];
                    ClockMainServer.tempPort[ClockMainServer.activeProcess] = ClockMainServer.processPort[i] ;
                    ClockMainServer.activeProcess ++;
                    
                    
                    
                    System.out.println("Added to active process list");
                    System.out.println("Active Process : "+ ClockMainServer.activeProcess);
                    System.out.println("Address : "+ClockMainServer.tempAddress[ClockMainServer.activeProcess-1]);
                    System.out.println("Port : " +ClockMainServer.tempPort[ClockMainServer.activeProcess-1]);
                    try {
                        sc.close();
                    } catch (IOException ex) {
                        //Logger.getLogger(service.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("socket closing error");
                    }
                }
                
                
                System.out.println("ACTIVE    gggggg: "+ ClockMainServer.activeProcess);

                for(int i=0; i<ClockMainServer.activeProcess; i++){
                    try {
                        sc = new Socket(ClockMainServer.tempAddress[i], ClockMainServer.tempPort[i]);
                        dis = new DataInputStream(sc.getInputStream());
                        dos = new DataOutputStream(sc.getOutputStream());

                        dos.writeUTF("SERVER");
                        dos.writeInt(ClockMainServer.activeProcess);
                        System.out.println("Sending to worker : "+ClockMainServer.tempAddress[i]+", port: "+ClockMainServer.tempPort[i]);

                        for(int ii=0; ii<ClockMainServer.activeProcess; ii++){
                            System.out.println("Sending address and port");
                            System.out.println("Address : "+ClockMainServer.tempAddress[ii]);
                            System.out.println("Port : "+ClockMainServer.tempPort[ii]);
                            dos.writeUTF(ClockMainServer.tempAddress[ii]);
                            dos.writeInt(ClockMainServer.tempPort[ii]);
                        }
                        sc.close();
                    } catch (IOException ex) {
                        //Logger.getLogger(service.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("socket error");
                    }
                }   
            }  
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                //Logger.getLogger(service.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("sleep time error");
            }
        }           
    }   
}

