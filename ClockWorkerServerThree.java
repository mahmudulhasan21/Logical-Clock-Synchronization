/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clockworkerserverthree;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class ClockWorkerServerThree {
    
    static String addressName = "localhost";
    static int portNum = 8083;
    //static long timeDifference = 0;
    static int time;
    
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here
            Socket sc = new Socket("localhost", 8080);
            System.out.println("Worker : (localhost, 8083) connected to main server ");
            
            //Calendar calendar = Calendar.getInstance();
            //long time = calendar.getTimeInMillis();
            //System.out.println("Time : " + time);
            //System.out.println("Starting Worker Server Time : " + calendar.getTimeInMillis());
            
            // create random time count
            Random rand = new Random();
            time = rand.nextInt(70) + 7;
            System.out.println("Starting Worker Server Time : " + time);
            
            DataInputStream dis = new DataInputStream(sc.getInputStream());
            DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
            
            dos.writeUTF(addressName);
            dos.writeInt(portNum);
            
            sc.close();
        } catch (IOException ex) {
            //Logger.getLogger(ClockWorkerServerThree.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("socket closing error ");
        }
        
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException ex) {
            //Logger.getLogger(ClockWorkerServerThree.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Sleep time error ");
        }
        
        ServerSocket ss = new ServerSocket(8083);
        while(true){
            Socket scW = ss.accept();
            
            
            Thread processThread = new Thread (new service(scW));
            processThread.start();
                
            
        }
    }
    
}

class service implements Runnable {
    Socket scW;
    DataOutputStream dosW;
    DataInputStream disW;
    
    //Calendar calendar = Calendar.getInstance();
    //long receiveTime;
    //long sendTime;
    //long differenceTime;
    //long takeTime;
    
    int receiveTime;
    int sendTime;
    int differenceTime;
    int takeTime;
    
    public service(Socket s) throws IOException{
        scW =s;
        disW = new DataInputStream(scW.getInputStream());
        dosW = new DataOutputStream(scW.getOutputStream());
    }
    public void run() {
        
            
        try {
            String str = disW.readUTF();
            if (str.equalsIgnoreCase("SERVER")) {
                int activeProcess = disW.readInt();
                for(int i=0; i<activeProcess; i++){
                    String processAddress = disW.readUTF();
                    int processPort = disW.readInt();
                    if((processAddress.equalsIgnoreCase(ClockWorkerServerThree.addressName)) && (ClockWorkerServerThree.portNum == processPort)){
                        continue;
                    }
                    
                    Socket scC = new Socket(processAddress, processPort);
                    DataInputStream disC = new DataInputStream(scC.getInputStream());
                    DataOutputStream dosC = new DataOutputStream(scC.getOutputStream());
                    
                    dosC.writeUTF("WORKER");
                    //Implementing Time Scheduling 
                    //sendTime = calendar.getTimeInMillis()+ClockWorkerServer.timeDifference;
                    
                    sendTime = ClockWorkerServerThree.time;
                    dosC.writeInt(sendTime);
                    dosC.writeUTF("Hi_From_wrkr_8083");
                    ClockWorkerServerThree.time = ClockWorkerServerThree.time +1;
                    
                    takeTime = disC.readInt();
                    String msg = disC.readUTF();
                    ClockWorkerServerThree.time = ClockWorkerServerThree.time +1;
                    //receiveTime = calendar.getTimeInMillis()+ClockWorkerServer.timeDifference;
                    receiveTime = ClockWorkerServerThree.time;
                    
                    System.out.println(" ");
                    System.out.println("send : Hi_From_wrkr_8083");
                    System.out.println("received : "+msg);
                    
                    System.out.println("From input Send Time : "+takeTime);
                    System.out.println("received Time : "+receiveTime);
                    
                    if(takeTime >= receiveTime)
                    {
                        differenceTime = (takeTime - receiveTime);
                        //ClockWorkerServer.timeDifference = ClockWorkerServer.timeDifference + differenceTime +3;
                        ClockWorkerServerThree.time = ClockWorkerServerThree.time +differenceTime + 1;
                        //long updateTime = calendar.getTimeInMillis()+ClockWorkerServer.timeDifference;
                        System.out.println("Time difference : "+differenceTime);
                        System.out.println("Updated machine Time : "+ClockWorkerServerThree.time);
                    }
                    
                    scC.close();
                    
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        ClockWorkerServerThree.time = ClockWorkerServerThree.time + 5;
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(ClockWorkerServerThree.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Sleep time error ");
                    }
                }
            }
            
            if (str.equalsIgnoreCase("WORKER")){
                
                takeTime = disW.readInt();
                String msg = disW.readUTF();
                //receiveTime = calendar.getTimeInMillis()+ClockWorkerServer.timeDifference;
                receiveTime = ClockWorkerServerThree.time;
                
                //System.out.println(" ");
                //System.out.println("received : "+msg);
                //System.out.println("send : Thanks_From_wrkr_8081");
                
                //sendTime = calendar.getTimeInMillis()+ClockWorkerServer.timeDifference;
                ClockWorkerServerThree.time = ClockWorkerServerThree.time + 1;
                sendTime = ClockWorkerServerThree.time;
                dosW.writeInt(sendTime);
                dosW.writeUTF("Thanks_From_wrkr_8083");
            }
            scW.close();
        } catch (IOException ex) {
            //Logger.getLogger(service.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Checking the connection : active or not");
        }
    }
}