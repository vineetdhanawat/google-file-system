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
			BR = ServerNode.serverReaders.get(socket);
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

				System.out.println("Message at "+ServerNode.serverNodeID+": "+message);
				
				// writer.println("ORDER,"+objName+","+clientNodeID");
				if(messageType.equals("ORDER"))
				{
					String[] writeOrder = {tokens[1],tokens[2]};
					ServerNode.writeOrder.add(writeOrder);
					System.out.println("SOP of Order"+ServerNode.writeOrder);
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