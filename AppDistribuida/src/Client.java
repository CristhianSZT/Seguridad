
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
	
    public static InetAddress direccionTCP;

	public static void main(String[] args) {
		
		DatagramSocket socket = null;
        
        try{
            socket = new DatagramSocket(2000);
        }catch(IOException e){
            System.out.println(e);
        }
        
        boolean runServer = true;
        while (runServer) {
            try{
                byte block[] = new byte[256];
                DatagramPacket packet = new DatagramPacket(block, block.length);
                socket.receive(packet);
                
                int lenght = packet.getLength();
                byte inblock[] = packet.getData();
                String inmsn = new String(inblock, 0, lenght);
                direccionTCP = packet.getAddress();
                
                System.out.println("Mensaje de: "+packet.getAddress());
                System.out.println("Cadena: " + inmsn);

                String outmsn = null;
                
                int aux = 0;
                                
                switch (inmsn) {
                    case "Necesito Ayuda":
                        outmsn = System.getProperty("user.name");
                        break;
                    case "Iniciamos":
                        configuraClienteTCP();
                        aux = 1;
                        break;
                    default:
                        outmsn = "Qué quieres decir " + inmsn + " ?";
                        break;
                }
                
                if(aux!=1){
                	byte outblock[] = outmsn.getBytes();
                    InetAddress reAddress = packet.getAddress();
                    int returnport = packet.getPort();
                    DatagramPacket outpacket = new DatagramPacket(outblock, outblock.length, reAddress, returnport);
                    socket.send(outpacket);
                }else{
                    runServer = false;
                }
            }catch (SocketTimeoutException s) {
                socket.close();
            }catch(IOException e){
    			System.out.print("Error al conectar el Socket UDP: "+e);
            }            
        }
	}

	private static void configuraClienteTCP() {
		
		Socket socket = null;
		
		try {
			
			System.out.println(direccionTCP.getHostAddress());
	        InetAddress address = InetAddress.getByName(direccionTCP.getHostAddress());
	        socket = new Socket(address, 3000);
	        	        			
	        while(true)
	        {   	            
	            InputStream is = socket.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String message = br.readLine();
	            
	            message = message.replace("{","");
	            message = message.replace("}","");
	            String[] keyValuePairs = message.split(",");              
	            HashMap<String, String> map = new HashMap<String, String>();            

	            for(String pair : keyValuePairs) {
	                String[] entry = pair.split("=");                  
	                map.put(entry[0].trim(), entry[1].trim());          
	            }
	            
	            if(map.get("Mensaje").compareTo("Escanear")==0){	
	            	System.out.println("Escanenado puertos...");
	            	ArrayList<String> puertosAbiertos = new ArrayList<String>();
	            	for (int i = Integer.valueOf(map.get("Inicio")); i <= Integer.valueOf(map.get("Fin")); i++) {
	            		try {
	            			if(i==3000){
		            			puertosAbiertos.add("Puerto " + i + " abierto");
	            			}else{
	            				Socket socketCheck = new Socket();
		            			socketCheck.connect(new InetSocketAddress("localhost", i), 1000);
		            			socketCheck.close();
		            			puertosAbiertos.add("Puerto " + i + " abierto");
	            			}
	                   } catch (Exception ex) {
	                   }
	                 }
	            	
	            	OutputStream os = socket.getOutputStream();
	                OutputStreamWriter osw = new OutputStreamWriter(os);
	                BufferedWriter bw = new BufferedWriter(osw);
	      
	                String sendMessage = puertosAbiertos.toString()+" \n";
	                bw.write(sendMessage);
	                bw.flush();
	            	
	                System.out.println("Conexión finalizada!");
	                break;
	            }
	            
	        }
	        
		} catch (IOException e) {
			System.out.print("Error al conectar el Socket TCP: "+e);
		}finally {
	        try{
	            socket.close();
	        }catch(Exception e){
				System.out.print("Error al desconectar el Socket TCP: "+e);
	        }
	    }
		
	}
	
}