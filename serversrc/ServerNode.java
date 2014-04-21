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

	// Total number of server nodes in the system;
	public static int SERVERNUMNODES = 0;
	
	// Total number of client nodes in the system;
	public static int CLIENTNUMNODES = 0;
	
	// ID number of this node instance
	public static int serverNodeID = 0;



	public static void main(String[] args)
	{

		// User will let the node know its nodeID
		if (args.length > 0)
		{
			try
			{
				serverNodeID = Integer.parseInt(args[0]);
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
			ReceiveConnectionThread RCT = new ReceiveConnectionThread(serverNodeID,IOH,SERVERNUMNODES,CLIENTNUMNODES);
			System.out.println("Listener Started");
			
			// Sleep so that all servers/listeners can can be started
			Thread.sleep(10000);
			
			SendConnectionThread SCT = new SendConnectionThread(serverNodeID,IOH,SERVERNUMNODES,CLIENTNUMNODES);
			
			// Sleep so that socket connections can be made
			Thread.sleep(5000);
			
			// Starting threads for always read listeners
		/*	for (int i=0;i<NUMNODES;i++)
			{
				if (i!=nodeID)
				{
					DaemonThread DT = new DaemonThread(socketMap.get(Integer.toString(i)),IOH, RA);
					System.out.println("SocketID"+DT);
					System.out.println("Started thread at "+nodeID+" for listening "+i);
				}
			}*/
			
			// Initialization Message
			/*if (nodeID == 0)
			{
				new Thread()
				{
					public void run()
					{
						broadcast("START");
					}
				 }.start();
				 RA.requestCriticalSection();
			}*/
		}
		catch (Exception e)
		{
			//TODO add error handling
		}
	}

}
