
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{
	
	public static ArrayList<Socket> listaSocket = new ArrayList<Socket>();
    public static boolean terminaEscuchador = false;
	
	public static void main(String[] args) {
		
		int tiempoEspera = 4000;
		String mensajeSalida = "Necesito Ayuda";
        
        DatagramSocket socket = null;
        
        try{
            socket = new DatagramSocket();
        }catch(IOException e){
            System.out.println(e);
        }
        
        int aux = 0;
        
        while(true){
            try {
                
                socket.setSoTimeout(tiempoEspera);
                while(true){
                    byte inblock[] = new  byte[256];
                    DatagramPacket inpacket = new DatagramPacket(inblock, inblock.length);
                    socket.receive(inpacket); 

                    String inString = new String(inpacket.getData(), 0, inpacket.getLength());
                    System.out.println("Cliente Disponible: "+inString);
                    System.out.println("Dirección IP: "+inpacket.getAddress());
                }
            }catch (SocketTimeoutException e) {
                aux ++;
                
                if(aux==5){
                    enviaDatos(socket, "Iniciamos");
                    configuraTCP();
                    socket.close();
                    break;
                }else{                    
                    enviaDatos(socket, mensajeSalida);
                    continue;
                }
                
            }catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
	}
	
	private static void enviaDatos(DatagramSocket socket, String outstr) {
        try {
            byte outblock[] = outstr.getBytes();
            InetAddress address = InetAddress.getByName("255.255.255.255");            
            DatagramPacket packet = new DatagramPacket(outblock, outblock.length, address, 2000);
            socket.send(packet);
            System.out.println("Servidor envia: "+outstr);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	private static void configuraTCP() {
		int numPuertos =  65535;
		int totalPuertos = 0;
				
	    ServerSocket socketServidor=null;
	    BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
	    String cadena;
	    
	    System.out.println("Servidor Inicializado...");
	    try{
	        socketServidor = new ServerSocket(3000);
	    }
	    catch(IOException e){
	    	e.printStackTrace();
	    	System.out.println("Error al crear servidor!");
	    }
	    
        new Thread(new EscuchadorConexiones(socketServidor)).start();
        
        System.out.println("(Introduce Inicio para iniciar):");
        
        try {
			 while(true){
				 cadena = br.readLine();
				 if(cadena.compareTo("Inicio")==0){
					 terminaEscuchador = true;
					 break;
				 }
			 }
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
   	 	
   	 	if(listaSocket.size()>0){
   	 		
   	   	 	int puertoInicial = 1;
   	   	 	int puertoFinal = numPuertos/listaSocket.size();
   	   	 	
	   	 	for(int i=0; i<listaSocket.size(); i++){
	   	 		 Socket socketCliente = listaSocket.get(i);
	    	 	 new Thread(new EscuchadorRespuesta(socketCliente)).start();
		       	 if(socketCliente.isConnected()){
		       		try{
		       			HashMap<String, String> hash = new HashMap<String, String>();
		       			hash.put("Mensaje", "Escanear");
		       			hash.put("Inicio", String.valueOf(puertoInicial));
		       			hash.put("Fin", String.valueOf(puertoFinal));
		       			OutputStream os = socketCliente.getOutputStream();
		       			OutputStreamWriter osw = new OutputStreamWriter(os);
			            BufferedWriter bw = new BufferedWriter(osw);
			            bw.write(hash.toString()+" \n");
			            bw.flush();
			            totalPuertos = totalPuertos + (numPuertos/listaSocket.size());          
			            puertoInicial = puertoFinal + puertoInicial;
			            puertoFinal = puertoFinal + puertoFinal;
			            Thread.sleep(1000);
			       	 }catch(Exception e){
					        e.printStackTrace();
					        System.out.println("Error al enviar datos al cliente!");
				    }
		       	 } 
	       }
   	 	}else{
   	 		System.out.println("Lo tengo que hacer solo!");
   	 	}
	}
	
	public void agregaCliente(Socket e){
		listaSocket.add(e);
	}

}

class EscuchadorRespuesta implements Runnable{  
	
	Socket socketCliente=null;

    public EscuchadorRespuesta(Socket socketCliente){
        this.socketCliente = socketCliente;
    }
    
    public void run() {
    	while(true){
	        try{
				InputStream is = socketCliente.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String mensaje = br.readLine();
                System.out.println(mensaje);
                socketCliente.close();
                break;
	        }catch(Exception e){
		        e.printStackTrace();
		        System.out.println("Error al conectar el cliente!");
		
	        }
	    }
    }
    
}

class EscuchadorConexiones implements Runnable{  
	
	Socket socketCliente=null;
	ServerSocket socketSeervidor=null;

    public EscuchadorConexiones(ServerSocket socketServidor){
        this.socketSeervidor=socketServidor;
    }
    
    public void run() {
    	while(!Server.terminaEscuchador){
	        try{
				socketCliente = socketSeervidor.accept();
	            System.out.println("Cliente Conectado!");        
	            Server servidor = new Server();
	            servidor.agregaCliente(socketCliente);
	        }catch(Exception e){
		        e.printStackTrace();
		        System.out.println("Error al conectar el cliente!");
		
		    }
	    }
    }
    
}
