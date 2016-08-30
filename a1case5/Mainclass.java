//Author: Arvind Nair

import java.io.*;
import java.util.*;
 
class Mainclass {//start of main class

	public static void main(String[] args)throws Exception {

/*Initializing the threads by passing their thread names*/

Masterobj MO=new Masterobj("MO");
Processobj1 PO1=new Processobj1("PO1");
Processobj2 PO2=new Processobj2("PO2");
Processobj3 PO3=new Processobj3("PO3");
Processobj4 PO4=new Processobj4("PO4");

/* Starting all the threads*/

MO.start();
PO1.start();
PO2.start();
PO3.start();
PO4.start();
	}//end of main method

}//end of class


/*Start of Masterobj*/
class Masterobj extends Thread {
	String name;
	
	static int countermo=0;//Master Object counter/clock
	static boolean flagmo=false;//Master Object Flag indicator
	static int[] a={0,0,0,0,0};//Array to store Process Objects' clock values
	static int[] b=new int[5];//Array to use for calculating offset
	
	/*Constructor of Master Object which takes in the parameter name*/
	 Masterobj(String threadname) throws InterruptedException{
		this.name=threadname;	
	}
	 
	 /*Function to receive Process Object clock values */
	 public static void receivemo(int pid,int c){
		 countermo++;
		 a[0]=countermo;
		 a[pid]=c;
		 return;
	 }
	 
	 /*Function to receive clock adjustment after a unit of time*/
	 public static void correctorpo(int pid,int c){
		 countermo++;
		 a[0]=countermo;
		 a[pid]=c;
		 b=a.clone();
		 flagmo=true;
		 return;
	 }
	 
	 /*Function to calculate the offset for each Process Object*/
	 public static void correctmo1(){
		 int sum=0;
		 for(int i=0;i<5;i++){
			 sum=sum+b[i];
		 }
		 int avg=sum/5;
		 int offsetmo=avg-countermo;
		 countermo=countermo+offsetmo;
		 int offsetpo=0;
		 for(int i=1;i<5;i++){
			 if(i==1){
				 offsetpo=avg-b[i];
				 Processobj1.receiveoffsetpo1(offsetpo);
				 countermo++;
			 }
			 else if(i==2){
				 offsetpo=avg-b[i];
				 Processobj2.receiveoffsetpo2(offsetpo); 
				 countermo++;
			 }
			 else if(i==3){
				 offsetpo=avg-b[i];
				 Processobj3.receiveoffsetpo3(offsetpo);
				 countermo++;
			 }
			 else if(i==4){
				 offsetpo=avg-b[i];
				 Processobj4.receiveoffsetpo4(offsetpo);
				 countermo++;
			 }
		 }
		 flagmo=false;
		 return;
	 }
	 
	 /*Function to output the Master Object clock value*/
	 public static void outputmo(int i){
		 System.out.println("MO Counter: "+i);
		 return;
	 }
	 
/*Starting point of thread Master Object*/
	public void run(){
		try {
		
			for(int i=0;i<100;i++){
			 
				if(flagmo==true){//Flag notifying Master Object to calculate the offsets of each Process Objects
				correctmo1();
			}
			outputmo(countermo);
			 Thread.sleep(100);
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	}//end Masterobj

/*Start of Processobj1*/
class Processobj1 extends Thread {
	String name;
	
	static int probpo1t=19;//Process Object 1 t unit sending probability
	static int probpo1=99;//Process Object 1 message sending probability
	static int counterpo1=0;//Process Object 1 counter
	static int offsetpo1=0;//Process Object 1 offset
	static boolean flagpo1=false;//Process Object 1 flag indicator

	/*Constructor of Process Object 1 which takes in the parameter name*/
	 Processobj1(String threadname) throws InterruptedException{
		this.name=threadname;
	}
	 
	 /*Function to send messages to other Process Objects and MAster Object */
	 public static void sendpo1(int k){
		 if(k==0){
	    		Processobj2.receivepo2(counterpo1);
	    		Masterobj.receivemo(1,counterpo1);
	    		counterpo1++;
	    		return;
	    	 }
	    	 else if(k==1){
	    		 Processobj3.receivepo3(counterpo1); 
	    		 Masterobj.receivemo(1,counterpo1);
	    		 counterpo1++;
	    		 return;
	    	 }
	    	 else if(k==2){
	    		 Processobj4.receivepo4(counterpo1);
	    		 Masterobj.receivemo(1,counterpo1);
	    		 counterpo1++;
	    		 return;
	    	 }
	 }
	 
	 /*Function to receive the clock values and adjust Process Object 1's clock accordingly if necessary*/
	 public static void receivepo1(int c){
		if(c<counterpo1){
		 counterpo1++;
		}
		else{
			counterpo1=c+1;
		}
		 return;
		 
	 }
	 
	 /*Function to receive the offset from Master Object for Process Object 1*/
	 public static void receiveoffsetpo1(int o){
		 offsetpo1=o;
		 System.out.println("PO1 offset: "+offsetpo1);
		 flagpo1=true;
		 return;
	 }
	 
	 /*Function to initiate after a unit of time sending of clock value to Master Object*/
	 public static void sendcorrectpo1(int s){
		 Masterobj.correctorpo(1,counterpo1);
		 counterpo1++;
		 return;
	 }
	 
	 /*Function to adjust the clock value of Process Object 1 as per the offset*/
	 public static void correctpo1(){
		 counterpo1=counterpo1+offsetpo1+1;
		 flagpo1=false;
		 return;
	 }
	 
	 /*Function to output the Process Object clock value*/
	 public static void outputpo1(int i){
		 System.out.println("PO1 Counter: "+i);
		 return;
	 }
	 
	 /*The starting point of thread Process Object 1*/
	public void run(){
	
		try {
	
			for(int i=0;i<100;i++){
		if(i%probpo1t==0){//specify the time to send Master Object the clock value of Process Object 1
			sendcorrectpo1(counterpo1);
		}
		
		else{
		
			if(flagpo1==true){//flag notifying Process Object 1 to adjust clock value as per offset
			correctpo1();
		}
		
			else{
		    Random rn=new Random();
	        int j=rn.nextInt(100);
	     
	         if(j>=0&&j<=probpo1){//Probability to make Process Object 1 decide whether to send message to other Process Objects
	    	 Random rn1=new Random();
	    	 int k=rn1.nextInt(3);//Probability to make Process Object 1 decide which Process Object to send message to
	    	 sendpo1(k);
	     }	
		} 
		}
	    outputpo1(counterpo1); 
		Thread.sleep(100);
	}
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

}
}//end Processobj1


/*Start of Processobj2*/
class Processobj2 extends Thread {
	String name;
	
	static int probpo2t=12;//Process Object 1 t unit sending probability
	static int probpo2=99;//Process Object 2 message sending probability
	static int counterpo2=0;//Process Object 2 counter
	static int offsetpo2=0;//Process Object 2 offset
	static boolean flagpo2=false;//Process Object 2 flag indicator
	
	/*Constructor of Process Object 2 which takes in the parameter name*/
	Processobj2(String threadname) throws InterruptedException{
		this.name=threadname;
	}
    
	 /*Function to send messages to other Process Objects and Master Object */
	public static void sendpo2(int k){
	 if(k==0){
 	   Processobj1.receivepo1(counterpo2);
 		Masterobj.receivemo(2,counterpo2);
 		counterpo2++;
 		return;
 	 }
 	 else if(k==1){
 		 Processobj3.receivepo3(counterpo2); 
 		 Masterobj.receivemo(2,counterpo2);
 		counterpo2++;
 		return;
 	 }
 	 else if(k==2){
 		 Processobj4.receivepo4(counterpo2);
 		 Masterobj.receivemo(2,counterpo2);
 		counterpo2++;
 		return;
 	 }
	 }
	
	 /*Function to receive the clock values and adjust Process Object 2's clock accordingly if necessary*/	
	public static void receivepo2(int c){
		 if(c<counterpo2){
			 counterpo2++;
			}
			else{
				counterpo2=c+1;
			}
		 return;
	 }
	
	 /*Function to initiate after a unit of time sending of clock value to Master Object*/
	 public static void sendcorrectpo2(int s){
		 Masterobj.correctorpo(2,counterpo2);
		 counterpo2++;
		 return;
	 }
	 
	 /*Function to receive the offset from Master Object for Process Object 2*/
	public static void receiveoffsetpo2(int o){
		 offsetpo2=o;
		 System.out.println("PO2 offset: "+offsetpo2);
		 flagpo2=true;
		 return;
	 }
	
	 /*Function to adjust the clock value of Process Object 2 as per the offset*/
	public static void correctpo2(){
		 counterpo2=counterpo2+offsetpo2+1;
		 flagpo2=false;
		 return;
	 }
	
	 /*Function to output the Process Object clock value*/
	public static void outputpo2(int i){
		 System.out.println("PO2 Counter: "+i);
		 return;
	 }

	 /*The starting point of thread Process Object 2*/
	public void run(){
		try {
		for(int i=0;i<100;i++){
			if(i%probpo2t==0){//specify the time to send Master Object the clock value of Process Object 1
				sendcorrectpo2(counterpo2);
			}	
		else{
			if(flagpo2==true){//flag notifying Process Object 2 to adjust clock value as per offset
				correctpo2();
			}
			else{
			Random rn=new Random();
		     int j=rn.nextInt(100); 
			if(j>=0&&j<=probpo2){//Probability to make Process Object 2 decide whether to send message to other Process Objects
			    	 Random rn1=new Random();
			    	 int k=rn1.nextInt(3);//Probability to make Process Object 2 decide which Process Object to send message to
			    	 sendpo2(k);
			    	 
				}
			}
		}
			outputpo2(counterpo2);
			Thread.sleep(100);
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	}//end Processobj2


/*Start of Processobj3*/
class Processobj3 extends Thread {
	String name;
	
	static int probpo3t=26;//Process Object 3 t unit sending probability
	static int probpo3=99;//Process Object 3 message sending probability
	static int counterpo3=0;//Process Object 3 counter
	static int offsetpo3=0;//Process Object 3 offset
	static boolean flagpo3=false;//Process Object 3 flag indicator
	
	/*Constructor of Process Object 3 which takes in the parameter name*/
	Processobj3(String threadname) throws InterruptedException{
		this.name=threadname;
	}
    
	 /*Function to send messages to other Process Objects and MAster Object */
	public static void sendpo3(int k){
	 if(k==0){
 		Processobj1.receivepo1(counterpo3);
 		Masterobj.receivemo(3,counterpo3);
 		counterpo3++;
 		return;
 	 }
 	 else if(k==1){
 		 Processobj2.receivepo2(counterpo3); 
 		 Masterobj.receivemo(3,counterpo3);
 		counterpo3++;
 		return;
 	 }
 	 else if(k==2){
 		 Processobj4.receivepo4(counterpo3);
 		 Masterobj.receivemo(3,counterpo3);
 		counterpo3++;
  		return;
 	 }
	 }

	 /*Function to receive the clock values and adjust Process Object 3's clock accordingly if necessary*/
	public static void receivepo3(int c){
		 if(c<counterpo3){
			 counterpo3++;
			}
			else{
				counterpo3=c+1;
			}
		 return;
	 }
	
	 /*Function to initiate after a unit of time sending of clock value to Master Object*/
	 public static void sendcorrectpo3(int s){
		 Masterobj.correctorpo(3,counterpo3);
		 counterpo3++;
		 return;
	 }
	 
	 /*Function to receive the offset from Master Object for Process Object 3*/
	public static void receiveoffsetpo3(int o){
		 offsetpo3=o;
		 System.out.println("PO3 offset: "+offsetpo3);
		 flagpo3=true;
		 return;
	 }
	
	 /*Function to adjust the clock value of Process Object 3 as per the offset*/
	public static void correctpo3(){
		 counterpo3=counterpo3+offsetpo3+1;
		 flagpo3=false;
		 return;
	 }
	
	 /*Function to output the Process Object clock value*/
	public static void outputpo3(int i){
		 System.out.println("PO3 Counter: "+i);
		 return;
	 }

	 /*The starting point of thread Process Object 3*/
	public void run(){
	
		try {
		
			for(int i=0;i<100;i++){
				if(i%probpo3t==0){//specify the time to send Master Object the clock value of Process Object 1
					sendcorrectpo3(counterpo3);
				}
			else{
			if(flagpo3==true){//flag notifying Process Object 3 to adjust clock value as per offset
				correctpo3();
			}
			
			else{
			Random rn=new Random();
		     int j=rn.nextInt(100); 
			
		     if(j>=0&&j<=probpo3){//Probability to make Process Object 3 decide whether to send message to other Process Objects
			    	 Random rn1=new Random();
			    	 int k=rn1.nextInt(3);//Probability to make Process Object 3 decide which Process Object to send message to
			    	sendpo3(k);
			    	
				}
			}
			}
			outputpo3(counterpo3);
			Thread.sleep(100);
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	}//end Processobj3


/*Start of Processobj4*/
class Processobj4 extends Thread {
	
	String name;
	
	static int probpo4t=35;//Process Object 4 t unit sending probability
	static int probpo4=99;//Process Object 4 message sending probability
	static int counterpo4=0;//Process Object 4 counter
	static int offsetpo4=0;//Process Object 4 offset
	static boolean flagpo4=false;//Process Object 4 flag indicator
	
	/*Constructor of Process Object 4 which takes in the parameter name*/
	Processobj4(String threadname) throws InterruptedException{
		this.name=threadname;
	}
    
	 /*Function to send messages to other Process Objects and Master Object */
	public static void sendpo4(int k){
	
		if(k==0){
 		Processobj1.receivepo1(counterpo4);
 		Masterobj.receivemo(4,counterpo4);
 		counterpo4++;
 		return;
 	 }
 	 
		else if(k==1){
 		 Processobj2.receivepo2(counterpo4); 
 		 Masterobj.receivemo(4,counterpo4);
 		counterpo4++;
 		 return;
 	 }
 	
		else if(k==2){
 		 Processobj3.receivepo3(counterpo4);
 		 Masterobj.receivemo(4,counterpo4);
 		counterpo4++;
 		 return;
 	 }
	 }
	
	 /*Function to receive the clock values and adjust Process Object 4's clock accordingly if necessary*/
	public static void receivepo4(int c){
		 if(c<counterpo4){
			 counterpo4++;
			}
			else{
				counterpo4=c+1;
			}
		 return;
	 }
	
	 /*Function to initiate after a unit of time sending of clock value to Master Object*/
	 public static void sendcorrectpo4(int s){
		 Masterobj.correctorpo(4,counterpo4);
		 counterpo4++;
		 return;
	 }
	 
	 /*Function to receive the offset from Master Object for Process Object 4*/
	public static void receiveoffsetpo4(int o){
		 offsetpo4=o;
		 System.out.println("PO4 offset: "+offsetpo4);
		 flagpo4=true;
		 return;
	 }
	
	 /*Function to adjust the clock value of Process Object 4 as per the offset*/
	public static void correctpo4(){
		 counterpo4=counterpo4+offsetpo4+1;
		 flagpo4=false;
		 return;
	 }
	 
	 /*Function to output the Process Object clock value*/
	public static void outputpo4(int i){
		 System.out.println("PO4 Counter: "+i);
		 return;
	 }
	
	 /*The starting point of thread Process Object 4*/
	 public void run(){
		try {
		for(int i=0;i<100;i++){
			if(i%probpo4t==0){//specify the time to send Master Object the clock value of Process Object 1
				sendcorrectpo4(counterpo4);
			}
			else{
			if(flagpo4==true){//flag notifying Process Object 4 to adjust clock value as per offset
				correctpo4();
			}
			
			else{
			Random rn=new Random();
		    int j=rn.nextInt(100);
			
		     if(j>=0&&j<=probpo4){//Probability to make Process Object 4 decide whether to send message to other Process Objects
			    	 Random rn1=new Random();
			    	 int k=rn1.nextInt(3);//Probability to make Process Object 4 decide which Process Object to send message to
			    	 sendpo4(k);
			    	}
			}
			}
			outputpo4(counterpo4);
			Thread.sleep(100);
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	}//end Processobj4