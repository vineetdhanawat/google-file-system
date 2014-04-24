import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class DaemonThreadServer extends Thread
{
	Socket socket;
	BufferedReader BR;
	InputOutputHandler IOH;
	
	DaemonThreadServer(Socket socket, InputOutputHandler IOH)
	{
		super();
		start();
		this.socket = socket;
		this.IOH = IOH;
		try {
			BR = ClientNode.serverReaders.get(socket);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		String message;
		try
		{
			while((message = BR.readLine() ) != null)
			{
				String tokens[] = message.split(",");
				String messageType = tokens[0];

				System.out.println("Message at "+ClientNode.clientNodeID+": "+message);
				if(messageType.equals("YES"))
				{
					switch(tokens[2])
					{
						case "0":
							ClientNode.isNode0Up = true;
							break;
						case "1":
							ClientNode.isNode1Up = true;
							break;
						case "2":
							ClientNode.isNode2Up = true;
							break;
					}
				}
				
				if(messageType.equals("DATA"))
				{
					//writer.println("DATA,"+ServerNode.serverNodeID+","+line);
					System.out.println("DATA READ:"+tokens[2]);
				}
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}