import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SendConnectionThread extends Thread
{
	int serverNodeID, SERVERNUMNODES, CLIENTNUMNODES;
	InputOutputHandler IOH;
	SendConnectionThread(int serverNodeID, InputOutputHandler IOH, int SERVERNUMNODES, int CLIENTNUMNODES)
	{
		super();
		start();
		this.serverNodeID = serverNodeID;
		this.IOH = IOH;
		this.SERVERNUMNODES = SERVERNUMNODES;
		this.CLIENTNUMNODES = CLIENTNUMNODES;
	}

	public void run()
	{
		Socket socket;
		for(int i=0;i<SERVERNUMNODES;i++)
		{
			// Send connection to all nodes with nodeID > current node
			if (serverNodeID < i)
			{
				String host = IOH.serverMap.get(Integer.toString(i)).get(0);
				int port = Integer.parseInt(IOH.serverMap.get(Integer.toString(i)).get(1));
				try
				{
					System.out.println("Connecting "+host+":"+port);
					socket = new Socket(host,port);
					System.out.println("Connection established");
					
					System.out.println("Socket at "+serverNodeID+" for sending to "+i + " "+ socket);
					System.out.println("-------------------------");
					
					ServerNode.serverSocketMap.put(Integer.toString(i),socket);
					ServerNode.serverReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
					ServerNode.serverWriters.put(socket,new PrintWriter(socket.getOutputStream()));
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}