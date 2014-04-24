import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class ClientNode 
{
	public static ServerSocket server;
	static String objName1="vineet";
	static String objName2="vineet";
	public static int writeServerID;
	public static int readServerID;
	
	// Hashmaps used to store Server sockets, read and write buffers
    public static HashMap<String,Socket> serverSocketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> serverReaders = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> serverWriters = new HashMap<Socket,PrintWriter>();

	// Total number of server nodes in the system;
	public static int SERVERNUMNODES = 0;
	
	// Total number of client nodes in the system;
	public static int CLIENTNUMNODES = 0;
	
	// ID number of this node instance
	public static int clientNodeID = 0;
	public static boolean isNodeUp;
	public static boolean isNode0Up,isNode1Up,isNode2Up;

	public static void main(String[] args)
	{
		// User will let the node know its nodeID
		if (args.length > 0)
		{
			try
			{
				clientNodeID = Integer.parseInt(args[0]);
				isNodeUp = Boolean.valueOf(args[1]);
				System.out.println("Node status:"+isNodeUp);
		    }
			catch (NumberFormatException e)
			{
				System.err.println("Argument must be an integer");
				System.exit(1);
		    }
		}
		try 
		{
			// reading config file
			InputOutputHandler IOH = new InputOutputHandler();
			
			SERVERNUMNODES = IOH.readServerConfig();
			CLIENTNUMNODES = IOH.readClientConfig();
			
			//Must Be Run In A New Thread To Avoid Thread Blocking			
			// Sleep so that all servers/listeners can can be started
			Thread.sleep(10000);

			SendConnectionThread SCT = new SendConnectionThread(clientNodeID,IOH,SERVERNUMNODES,CLIENTNUMNODES);
			
			// Sleep so that socket connections can be made
			Thread.sleep(5000);
			
			// Starting threads for always read listeners
			for (int i=0;i<SERVERNUMNODES;i++)
			{
					DaemonThreadServer DTS = new DaemonThreadServer(serverSocketMap.get(Integer.toString(i)),IOH);
					System.out.println("SocketID"+DTS);
					System.out.println("Started thread at "+clientNodeID+" for listening server"+i);
			}
			
			System.out.print("WRITE STARTING");
			// Write Objects to Server
			Thread.sleep(5000);

			
			try
			{
				if (clientNodeID == 0)
				{
					writeServerID = getHash(objName1);
					System.out.print("writeServerID:"+writeServerID);
					
					checkNodeUp(writeServerID,"0");
					checkNodeUp((writeServerID+1)%SERVERNUMNODES,"1");
					checkNodeUp((writeServerID+2)%SERVERNUMNODES,"2");

					// Check for which nodes are up
					Thread.sleep(10000);
					System.out.println("isNode0Up:"+isNode0Up);
					System.out.println("isNode1Up:"+isNode1Up);
					System.out.println("isNode2Up:"+isNode2Up);
					if(isNode0Up && isNode1Up && isNode2Up)
					{
						System.out.println("Sending WRITE to nodes");
						sendRequest("WRITE",writeServerID,objName1,"Hello");
			            Thread.sleep(1000);
			            sendRequest("REPLICATE",(writeServerID+1) % SERVERNUMNODES,objName1,"Hello");
			            sendRequest("REPLICATE",(writeServerID+2) % SERVERNUMNODES,objName1,"Hello");
					}
					else if(isNode0Up && isNode1Up)
					{
						sendRequest("WRITE",writeServerID,objName1,"Hello");
			            Thread.sleep(1000);
			            sendRequest("REPLICATE",(writeServerID+1) % SERVERNUMNODES,objName1,"Hello");
					}
					else if(isNode1Up && isNode2Up)
					{
						sendRequest("WRITE",(writeServerID+1) % SERVERNUMNODES,objName1,"Hello");
			            Thread.sleep(1000);
			            sendRequest("REPLICATE",(writeServerID+2) % SERVERNUMNODES,objName1,"Hello");
					}
					else if(isNode0Up && isNode2Up)
					{
						sendRequest("WRITE",writeServerID,objName1,"Hello");
			            Thread.sleep(1000);
			            sendRequest("REPLICATE",(writeServerID+2) % SERVERNUMNODES,objName1,"Hello");
					}
					isNode0Up = false;
					isNode1Up = false;
					isNode2Up = false;
					
					System.out.println("Reading object now");
					Thread.sleep(1000);

					readServerID = getHash(objName1);
					System.out.print("readServerID:"+readServerID);

					// READ CODE
					checkNodeUp(readServerID,"0");
					checkNodeUp((readServerID+1)%SERVERNUMNODES,"1");
					checkNodeUp((readServerID+2)%SERVERNUMNODES,"2");
					// TODO: Logic for choosing read node
					readServerID += getReadServer(isNode0Up,isNode1Up,isNode2Up);
					readRequest(readServerID%SERVERNUMNODES,objName1);
					
					
				}
				if (clientNodeID == 1)
				{
					writeServerID = getHash(objName2);
					System.out.print("writeServerID:"+writeServerID);
					
					sendRequest("WRITE",writeServerID,objName2,"HelloMoto");
		            Thread.sleep(1000);
		            sendRequest("REPLICATE",(writeServerID+1) % SERVERNUMNODES,objName2,"HelloMoto");
		            sendRequest("REPLICATE",(writeServerID+2) % SERVERNUMNODES,objName2,"HelloMoto");
				}
				
				//writeServerID = getHash(objName1);

	            
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			
		}
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
	
	public static int getReadServer(boolean b1, boolean b2, boolean b3)
	{
		int ret = -1;
		Random r = new Random();
		if(b1 && b2 && b3){
			ret = r.nextInt(3);
		}
		else if(b1 && b2 && !b3){
			ret = r.nextInt(2);
		}
		else if(b1 && !b2 && b3){
			ret = r.nextInt(2);
			if(ret==1)
				ret =  ret+1;
		}
		else if (!b1 && b2 && b3){
			ret = r.nextInt(2)+1;
		}
		else if(b1){
			ret = 0;
		}else if(b2){
			ret = 1;
		}else if (b3){
			ret = 2;
		}
		return ret;
	}
	static void sendRequest(String request, int writeServerID, String objName, String message)
	{
		Socket bs = serverSocketMap.get(String.valueOf(writeServerID));
		System.out.println("bs:"+bs);
		PrintWriter writer = serverWriters.get(bs);
        writer.println(request+","+objName+","+clientNodeID+","+message);
        writer.flush();
        System.out.println("Sending "+request+" to server:"+writeServerID);
	}
	
	static void readRequest(int readServerID, String objName)
	{
		Socket bs = serverSocketMap.get(String.valueOf(readServerID));
		System.out.println("bs:"+bs);
		PrintWriter writer = serverWriters.get(bs);
        writer.println("READ,"+objName+","+clientNodeID);
        writer.flush();
        System.out.println("Sending READ to server:"+readServerID);
	}
	
	static void checkNodeUp(int writeServerID, String order)
	{
		Socket bs = serverSocketMap.get(String.valueOf(writeServerID));
		System.out.println("bs:"+bs);
		PrintWriter writer = serverWriters.get(bs);
        writer.println("PING,"+clientNodeID+","+order+","+isNodeUp);
        writer.flush();
        System.out.println("Sending PING to server:"+writeServerID+","+order+","+isNodeUp);
	}
	
	/**
	 * Takes object name as a string and returns an integer value between 0 to 6
	 */
	public static int getHash(String objName){
		int hash;
		hash = objName.hashCode() % SERVERNUMNODES;
		System.out.println("hashcode:"+objName.hashCode());
		return Math.abs(hash);
	}

}
