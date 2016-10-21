import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class EscanerRedLocal {

	public static void main(String[] args) throws IOException {
		
		InetAddress localhost = InetAddress.getLocalHost();
		byte[] ip = localhost.getAddress();
		for (int i = 1; i <= 254; i++)
		{
			ip[3] = (byte)i;
			InetAddress address = InetAddress.getByAddress(ip);
			if (address.isReachable(2000)){
				 System.out.println("IP: " + address.getHostAddress());
	             System.out.println("HOST: " + address.getHostName());
	             puertosAbiertos(1,65535,address.getHostAddress());
	             System.out.println();
			}
		}
	}

	private static void puertosAbiertos(int inicio, int fin, String ip) {

		System.out.println("Iniciando escaner de puertos en "+ip+"...");
		
		for(int i=inicio; i <=fin; i++)
			{
				try {
					Socket ServerSok = new Socket(ip,i);
					System.out.println("Puerto abierto: " + i );
					ServerSok.close();
		        }catch (Exception e){

		        }  
				//System.out.println("Puerto no abierto: " + i );
		}
	}	
}
