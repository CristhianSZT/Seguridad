package ProdConsDistribuido;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

	public static ArrayList<Socket> listaSocket = new ArrayList<>();
    public static boolean terminaEscuchador = false;
    
	public static void main(String[] args) {				
	    ServerSocket socketServidor=null;
	    BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
	    String cadena;
	    
	    System.out.println("Servidor Inicializado...");
	    try{
	        socketServidor = new ServerSocket(3000);
	    }
	    catch(IOException e){
	    	e.printStackTrace();
	    	System.out.println("Error al crear el servidor!");
	    }
	    
	    new Thread(new EscuchadorConexiones(socketServidor)).start();
        
        System.out.println("(Escribe Inicio para comenzar):");
        
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
        	Bodega  bodega = new Bodega();
	   	 	for(int i=0; i<listaSocket.size(); i++){
		   	 	try{
		   	 		Socket socketCliente = listaSocket.get(i);
		   	 		OutputStream os = socketCliente.getOutputStream();
	       			OutputStreamWriter osw = new OutputStreamWriter(os);
		            BufferedWriter bw = new BufferedWriter(osw);
		            bw.write("Inicia \n");
		            bw.flush();
		   	 		new Thread(new EscuchadorRespuesta(socketCliente, bodega)).start();
		   	 	}catch(Exception e){
			        e.printStackTrace();
			        System.out.println("Error al conectar el cliente!");
		        }
	       }
   	 	}
	}
	
	public void agregaCliente(Socket e){
		listaSocket.add(e);
	}
}

class EscuchadorRespuesta implements Runnable{  
	
	Socket socketCliente=null;
	Bodega bodega;

    public EscuchadorRespuesta(Socket socketCliente, Bodega bodega){
        this.socketCliente = socketCliente;
        this.bodega = bodega;
    }
    
    public void run() {
    	while(true){
	        try{
				InputStream is = socketCliente.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String message = br.readLine();
	            
	            message = message.replace("{","");
	            message = message.replace("}","");
	            String[] keyValuePairs = message.split(",");              
	            HashMap<String,String> map = new HashMap<>();               

	            for(String pair : keyValuePairs) {
	                String[] entry = pair.split("=");                  
	                map.put(entry[0].trim(), entry[1].trim());          
	            }
                
                synchronized (bodega) {
                	                	
                	if(map.get("Tipo").equals("1")){
    					bodega.wait();
    					System.out.println("Consumidor quita producto");
    					bodega.sacarProducto();
    					System.out.println("Total de productos: "+bodega.getProductos());
                	}else if(map.get("Tipo").equals("2")){
                		System.out.println("Productor pone producto");
    					bodega.agregarProducto();
    					System.out.println("Total de productos: "+bodega.getProductos());
    					bodega.notify();
                	}
	                
	            }
                
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
