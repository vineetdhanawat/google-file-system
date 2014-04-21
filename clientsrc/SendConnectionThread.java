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
	int clientNodeID, SERVERNUMNODES, CLIENTNUMNODES;
	InputOutputHandler IOH;
	SendConnectionThread(int serverNodeID, InputOutputHandler IOH, int SERVERNUMNODES, int CLIENTNUMNODES)
	{
		super();
		start();
		this.clientNodeID = clientNodeID;
		this.IOH = IOH;
		this.SERVERNUMNODES = SERVERNUMNODES;
		this.CLIENTNUMNODES = CLIENTNUMNODES;
	}

	public void run()
	{
		Socket socket;
		for(int i=0;i<SERVERNUMNODES;i++)
		{
			// Each client node maintains socket with all servers
			String host = IOH.serverMap.get(Integer.toString(i)).get(0);
			int port = Integer.parseInt(IOH.serverMap.get(Integer.toString(i)).get(1));
			try
			{
				System.out.println("Connecting "+host+":"+port);
				socket = new Socket(host,port);
				System.out.println("Connection established");
				
				System.out.println("Socket at "+clientNodeID+" for sending to server"+i + " "+ socket);
				System.out.println("-------------------------");

				ClientNode.serverSocketMap.put(Integer.toString(i),socket);
				ClientNode.serverReaders.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
				ClientNode.serverWriters.put(socket,new PrintWriter(socket.getOutputStream()));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}