import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

public class Test {
	
	private static final String REGIST_NOTIFY = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<MCU_XML_API>" +
			"	<Version>7</Version>" +
			"	<Request>" +
			"		<RegUnreg_For_All_Notifications_Request>" +
			"			<RequestID>1</RequestID>" +
			"		    <Register>True</Register>" +
			"		</RegUnreg_For_All_Notifications_Request>" +
			"	</Request>" +
			"</MCU_XML_API>";


	public static void main(String[] args) throws Exception 
	{
//		Test t = new Test();
//		
//		t.test();
		
		
		Date s = new Date(1410884627431L);
		
		System.out.println(s.toLocaleString());
		
		Date s1 = new Date(1410884687000L);
		
		System.out.println(s1.toLocaleString());
		
	}
	
	public void test() throws Exception
	{
		Client client = new Client();		

		//client.startService("192.168.22.92");
		client.startService("avayalab.vicp.cc");
		sendXMLRequest(client,REGIST_NOTIFY);	
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			String keyinput = br.readLine() ;
			
			if("q".equalsIgnoreCase(keyinput)) 
				break;
			
			else if("0".equalsIgnoreCase(keyinput)) //查看房间状态信息
				sendHTTPRequest("getConfControlData?roomid=6124");
			
			else if("1".equalsIgnoreCase(keyinput)) //创建会议房间，epname必须固定为-1
			sendHTTPRequest("inviteParty?roomid=6124&epname=-1");
			
			else if("2".equalsIgnoreCase(keyinput)) //删除会议,清除所有参会方
				sendHTTPRequest("deleteParty?roomid=6124");

			else if("3".equalsIgnoreCase(keyinput)) //邀请参会， epname就是参会方的名称
			sendHTTPRequest("inviteParty?roomid=6124&epname=test1");
			
			else if("4".equalsIgnoreCase(keyinput)) //断开某一参会方， epname就是参会方的名称
			sendHTTPRequest("deleteParty?roomid=6124&epname=test1");
			
			
		
			
			
		}
		
		//client.stopService();
		System.out.println("Thanks for your use, bye!");

		
	}
	
	
	private void sendXMLRequest(Client client,String reqStr)
	{
		PrintWriter pw = client.getWriter();
		System.out.println("send:    " + reqStr);
		pw.write(reqStr);
		pw.flush();

	}

	
	private String sendHTTPRequest(String reqStr)
	{

		String tmp = "http://localhost:9400/servlet/" + reqStr;
		
		System.out.println("http request: "  + reqStr );

		String response = HttpTookit.doGet(tmp, "", "utf-8",true);
		
		System.out.println("http response: "  + response );
		return response;
		
	}
}
