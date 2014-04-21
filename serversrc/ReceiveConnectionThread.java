import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveConnectionThread extends Thread
{
	int serverNodeID, SERVERNUMNODES, CLIENTNUMNODES;
	InputOutputHandler IOH;
	ReceiveConnectionThread(int serverNodeID, InputOutputHandler IOH, int SERVERNUMNODES, int CLIENTNUMNODES)
	{
		super();
		start();
		this.serverNodeID = serverNodeID;
		this.IOH = IOH;
		// SERVERNUMNODES are expected number of server connections
		// Which is from all serverNodeID < current server node id
		this.SERVERNUMNODES = serverNodeID;
		this.CLIENTNUMNODES = CLIENTNUMNODES;

	}
	
	public void run()
	{
		try
		{
			// Start Node at the specified port; Fixed
			int port = Integer.parseInt(IOH.serverMap.get(Integer.toString(serverNodeID)).get(1));
			ServerSocket server = new ServerSocket(port);
			System.out.println("Node "+serverNodeID+" listening at "+port);
			
			// First connection
			int i = 0;

			while (SERVERNUMNODES>1)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket socket = server.accept();
				System.out.println("Socket at "+serverNodeID+" for listening server "+i + " "+ socket);
				System.out.println("-------------------------");
				
				ServerNode.serverSocketMap.put(Integer.toString(i),socket);
				ServerNode.serverReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
				ServerNode.serverWriters.put(socket,new PrintWriter(socket.getOutputStream()));
				
	            // incrementing i so that all incoming connections can be put in array in order.
	            i++;
	            
	            // Total no of incoming connections left
	            SERVERNUMNODES--;
			}
			
			i = 0;
			while (CLIENTNUMNODES>0)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket socket = server.accept();
				System.out.println("Socket at "+serverNodeID+" for listening client "+i + " "+ socket);
				System.out.println("-------------------------");
				
				ServerNode.clientSocketMap.put(Integer.toString(i),socket);
				ServerNode.clientReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
				ServerNode.clientWriters.put(socket,new PrintWriter(socket.getOutputStream()));
				
	            // incrementing i so that all incoming connections can be put in array in order.
	            i++;
	            
	            // Total no of incoming connections left
	            CLIENTNUMNODES--;
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}