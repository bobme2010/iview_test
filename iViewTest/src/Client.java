

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Client extends Thread 
{
	private static final String ELEMENT_ROOT = "MCU_XML_API";
	
	private static final int TIMEOUT = 3 * 1000; //default connection time out:3 seconds
	
	private static final int SOTIMEOUT = 1 * 1000; //default read time out:1 seconds
	
	private boolean _threadExitFlag = false; 
	
	private boolean _threadFinished = false; 
		
	private Socket _socket = null; 
	private InputStream _reader;  
	private PrintWriter _writer;      
	private StringBuffer _recievedBuffer = new StringBuffer(); 
	
	
	public PrintWriter getWriter()
	{
		return  this._writer;
	}
	
	public void startService(String ip) throws Exception
	{
		_threadExitFlag=false;
		this.createSocketConnection(ip);
		this.start();
	}

	public void stopService()
	{
		this._threadExitFlag =true;
		
		while(!_threadFinished)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		this.CloseSocketConnection();
	}

		public void run()
	{			
		byte[] bs = new byte[4096];
		while(this._threadExitFlag == false)
		{
			this.getMessageFromServer(bs);
			this.processMessage();
		}
		this._threadFinished = true;
	}
	

	private void createSocketConnection(String ip) throws Exception 
	{
		System.out.println("IP:" +ip );
		this._socket= new Socket(); 
		this._socket.connect(new InetSocketAddress(ip,3336),TIMEOUT);
		this._socket.setKeepAlive(true);
		this._socket.setSoTimeout(SOTIMEOUT); 
		
		if(this._socket.isConnected())
				System.out.println("connect iView Server success!");
    	
		this._writer = new PrintWriter(new OutputStreamWriter(this._socket.getOutputStream(), "UTF-8"), true);
		this._reader = this._socket.getInputStream();
		
	}

	private void CloseSocketConnection() {
		try
		{
			if(this._socket != null) this._socket.close();
			if(this._reader != null) this._reader.close();
			if(this._writer != null) this._writer.close();
		}catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	

	private void getMessageFromServer(byte[] bs)
	{
		try
		{
			int i = this._reader.read(bs);
			if(i == -1) throw new SocketException("socket reader is -1");
			String msg = new String(bs,0,i,"utf-8"); 
			
			this._recievedBuffer.append(msg);
		}catch(SocketTimeoutException e)
		{
			
		}		
		catch(SocketException e)
		{
			System.out.println("iView Connection Error:" + e.getMessage());
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {ex.printStackTrace();}
			
		}
		catch(Exception e)
		{
			if(!this._threadExitFlag) System.out.println("get message from iView Server Error:\n" + e.getMessage());
			try {
				Thread.sleep(500);   // When an exception occurred ,we must sleep a while. If we not sleep , there will be a lot of useless log.
			} catch (InterruptedException ex) {ex.printStackTrace();}
		}

	}
	
	private void processMessage() 
	{	
		try
		{
			String[] responseXMLArray = this.pickupXmlStrings(this._recievedBuffer,ELEMENT_ROOT); 
			
			if (responseXMLArray.length > 0) 
	        {
	            for(int i = 0;i < responseXMLArray.length;i++)
	            {
	            	//make sure there're no other characters before the xml header.                    	
	                if (!responseXMLArray[i].startsWith("<?xml"))
	                {
	                	int ind = responseXMLArray[i].indexOf("<?xml");
	                    if (ind > 0)
	                    	responseXMLArray[i] = responseXMLArray[i].substring(ind);
	                }
	                System.out.println("recieve: " + responseXMLArray[i]);
	            }
	        } 
		}catch(Exception e)
		{
			System.out.println("Process Response Message Excepiton:\n"+ e);
		}
	}
		
	  /**
     * Pickup xml strings from a string buffer according the root element name.
     *
     * @param buf input buf, the buf content may be changed.
     * @param rootElementName the root element name.
     * @return a xml string array, the array length maybe zero if no completed xml can be found.
     */
    private String[] pickupXmlStrings(StringBuffer buf,String rootElementName)
    {
         String endToken = "</" + rootElementName + ">";
         return this.pickupStrings(buf,endToken);
    }
    
	/**
     * Pickup Strings.
     */
    private String[] pickupStrings(StringBuffer buf,String endToken)
    {
         ArrayList<String> strs = new ArrayList<String>();
         int index = buf.indexOf(endToken); 
         
         if(index == -1)
             return new String[0];

         if(index + endToken.length() >= buf.length())
         {
             strs.add(buf.toString());
             buf.delete(0,buf.length());
         }
         else
         {
             //batchCommands.
             String tmpStr = buf.substring(0,index + endToken.length());
             strs.add(tmpStr);
             buf.delete(0,index + endToken.length());
             index = buf.indexOf(endToken);
             while(index != -1)
             {
                 tmpStr = buf.substring(0,index + endToken.length());
                 strs.add(tmpStr);
                 buf.delete(0,index + endToken.length());
                 index = buf.indexOf(endToken);
             }
         }
         return (String[])strs.toArray(new String[strs.size()]);
     }
}
