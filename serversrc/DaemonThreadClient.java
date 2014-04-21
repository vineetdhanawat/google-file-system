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

public class DaemonThreadClient extends Thread
{
	Socket socket;
	BufferedReader BR;
	InputOutputHandler IOH;
	
	DaemonThreadClient(Socket socket, InputOutputHandler IOH)
	{
		super();
		start();
		this.socket = socket;
		this.IOH = IOH;
		try {
			BR = ServerNode.clientReaders.get(socket);
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
				
				System.out.println("Message at "+ServerNode.serverNodeID+": "+messageType);
				if(messageType.equals("WRITE"))
				{
					long currentTS1 = TimeStamp.getTimestamp();
					PrintWriter writer = new PrintWriter(tokens[1]+"_"+ServerNode.serverNodeID+".txt", "UTF-8");
					writer.println(tokens[2]+"_"+tokens[3]);
					writer.close();
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