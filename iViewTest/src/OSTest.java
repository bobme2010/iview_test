import java.net.InetAddress;
import java.net.NetworkInterface;


public class OSTest 
{

	
	public static String getAddressMAC()
	{
		String mac = "";
		String os = System.getProperty("os.name").toLowerCase(); 
		if(os.contains("windows") && (os.contains("vista") || os.contains("7") || os.contains("8")|| os.contains("2012"))){
			mac = getWin7AddressMAC();		
        }else if(os.startsWith("windows")){
        	//mac = getWinAddressMAC();   
        }else{
        	//mac = getUnixAddressMAC();
        }
		return mac;
	}

	/**
	 * 获得win7 及以上版本的Mac地址
	 * @return Mac地址
	 * @throws Exception 
	 */
	public static String getWin7AddressMAC()
	{
		String mac = "";
		try
		{
	        // 获取本地IP对象   
	        InetAddress ia = InetAddress.getLocalHost();   
	        // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。   
	        byte[] macbyte = NetworkInterface.getByInetAddress(ia).getHardwareAddress();   
	  
	        // 下面代码是把mac地址拼装成String   
	        StringBuffer sb = new StringBuffer();   
	  
	        for (int i = 0; i < macbyte.length; i++) {   
	            if (i != 0) {   
	                sb.append("-");   
	            }   
	            // mac[i] & 0xFF 是为了把byte转化为正整数   
	            String s = Integer.toHexString(macbyte[i] & 0xFF);   
	            sb.append(s.length() == 1 ? 0 + s : s);   
	        } 
	        mac = sb.toString().toUpperCase();
		}catch(Exception e)
		{
			e.printStackTrace();   
		}
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回   
        return mac;   
    }   

	
	public static void main(String[] v)
	{
		String os = System.getProperty("os.name").toLowerCase(); 
		
		System.out.println(getAddressMAC());
	}
}
