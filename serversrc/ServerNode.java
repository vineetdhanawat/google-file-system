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

public class ServerNode 
{
	public static ServerSocket server;
	
	// Hashmaps used to store Server sockets, read and write buffers
    public static HashMap<String,Socket> serverSocketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> serverReaders = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> serverWriters = new HashMap<Socket,PrintWriter>();
    
	// Hashmaps used to store Client sockets, read and write buffers
    public static HashMap<String,Socket> clientSocketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> clientReaders = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> clientWriters = new HashMap<Socket,PrintWriter>();
    
    // Public Order
    public static List<String[]> writeOrder = new ArrayList<String[]>();
    public static List<String[]> bufferedOrder = new ArrayList<String[]>();

	// Total number of server nodes in the system;
	public static int SERVERNUMNODES = 0;
	
	// Total number of client nodes in the system;
	public static int CLIENTNUMNODES = 0;
	
	// ID number of this node instance
	public static int serverNodeID = 0;
	public static boolean isNodeUp;



	public static void main(String[] args)
	{

		// User will let the node know its nodeID
		if (args.length > 0)
		{
			try
			{
				serverNodeID = Integer.parseInt(args[0]);
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
			
			System.out.println("SERVERNUMNODES:"+SERVERNUMNODES);
			System.out.println("CLIENTNUMNODES:"+CLIENTNUMNODES);
			//Must Be Run In A New Thread To Avoid Thread Blocking
			ReceiveConnectionThread RCT = new ReceiveConnectionThread(serverNodeID,IOH,SERVERNUMNODES,CLIENTNUMNODES);
			System.out.println("Listener Started");
			
			// Sleep so that all servers/listeners can can be started
			Thread.sleep(10000);
			
			SendConnectionThread SCT = new SendConnectionThread(serverNodeID,IOH,SERVERNUMNODES,CLIENTNUMNODES);
			
			// Sleep so that socket connections can be made
			Thread.sleep(5000);
			
			// Starting threads for always read listeners
			for (int i=0;i<SERVERNUMNODES;i++)
			{
				if (i!=serverNodeID)
				{
					DaemonThreadServer DTS = new DaemonThreadServer(serverSocketMap.get(Integer.toString(i)),IOH);
					System.out.println("SocketID"+DTS);
					System.out.println("Started thread at "+serverNodeID+" for listening server"+i);
				}
			}
			Thread.sleep(5000);
			for (int i=0;i<CLIENTNUMNODES;i++)
			{
					DaemonThreadClient DTC = new DaemonThreadClient(clientSocketMap.get(Integer.toString(i)),IOH);
					System.out.println("SocketID"+DTC);
					System.out.println("Started thread at "+serverNodeID+" for listening client"+i);
			}
			Thread.sleep(5000);
			
			/*// Initialization Message
			//if (serverNodeID == 0)
			{
				new Thread()
				{
					public void run()
					{
						broadcast("START");
					}
				 }.start();
			}*/

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
		return hash;
	}
	
    /**
	* Broadcasts a message to all servers and clients in the outputStreams arraylist.
	*/
	public static void broadcast(String message)
	{
		for(int i=0; i<SERVERNUMNODES; i++)
		{
			if (i!=serverNodeID)
			{
				try
				{
					System.out.println("Sending "+message+" to server "+i);
					Socket bs = serverSocketMap.get(Integer.toString(i));
					System.out.println("socket:"+bs);
					PrintWriter writer = serverWriters.get(bs);
		            writer.println(message);
	                writer.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		for(int i=0; i<CLIENTNUMNODES; i++)
		{
			try
			{
				System.out.println("Sending "+message+" to client "+i);
				Socket bs = clientSocketMap.get(Integer.toString(i));
				PrintWriter writer = clientWriters.get(bs);
	            writer.println(message);
                writer.flush();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

}
