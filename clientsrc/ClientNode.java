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
import java.util.TimerTask;

public class ClientNode 
{
	public static ServerSocket server;
	static String objName="vineet";
	public static int writeServerID; 
	
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

	public static void main(String[] args)
	{
		// User will let the node know its nodeID
		if (args.length > 0)
		{
			try
			{
				clientNodeID = Integer.parseInt(args[0]);
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
				writeServerID = getHash(objName);
				System.out.print("writeServerID:"+writeServerID);

				Socket bs = serverSocketMap.get(String.valueOf(writeServerID));
				System.out.println("bs:"+bs);
				PrintWriter writer = serverWriters.get(bs);
	            writer.println("WRITE,"+objName+",HELLO,1");
	            writer.flush();
	            System.out.println("Sending WRITE to server:"+writeServerID);
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
