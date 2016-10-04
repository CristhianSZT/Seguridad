package ServidorUV;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EscuchadorNavegador extends Thread {
	
	private Socket socketNavegador = null;
    
    public EscuchadorNavegador(Socket socket) {
        this.socketNavegador = socket;
    }
    
    public void run() {
        try {
        	
            BufferedReader entradaNavegador = new BufferedReader(new InputStreamReader(socketNavegador.getInputStream()));
            PrintWriter salidaNavegador = new PrintWriter(new OutputStreamWriter(socketNavegador.getOutputStream(),"8859_1"),true);
            
            String cadena;
            String get = "GET";
            String host = "Host";
            int puerto = 80;
            boolean bandera = false;
            
            do          
            {           
                cadena = entradaNavegador.readLine();
                if(cadena != null && cadena.length() != 0){
                    
                    if(cadena.contains(get)){
                        get = cadena;
                    }else if(cadena.contains(host)){
                    	cadena = cadena.replace(" ", "");
                    	String[] array = cadena.split(":");
                    	if(array.length==3){
                    		host=array[1];
                    		puerto = Integer.parseInt(array[2]);
                    	}else{
                    		host = array[1];
                    	}
                    bandera = true;
                    break;
                    }
                }                   
            }
            while (cadena != null && cadena.length() != 0);
            
            if(bandera){
                System.out.println("{{"+ get+" }}"+"{{"+ host+" }}"+ "{{"+ puerto+"}}");
                String respuesta =ConectaServidorUV( get, host,  puerto);;
                salidaNavegador.println(respuesta);
            }
            
        }catch (IOException e){
        	System.out.println(e.toString());
        }
        
        try {
            socketNavegador.close();
        } catch (IOException e) {
        	System.out.println(e.toString());
        }
    }

	private String ConectaServidorUV(String get, String hostNavegador, int puertoNavegador) {
		int puerto = 9090;
		String respuesta = "";
        ServerSocket servidorClienteNavegador = null;
        Socket socketClienteNavegador  = null;
                
        try {
        	servidorClienteNavegador = new ServerSocket(puerto);
            System.out.println("Esperando conexion cliente...");
            socketClienteNavegador = servidorClienteNavegador.accept();
            
            DataOutputStream envioCliente= new DataOutputStream(socketClienteNavegador.getOutputStream());
            
            envioCliente.writeUTF(get+","+hostNavegador+","+puertoNavegador+" \n\r");
            
            BufferedReader entradaCliente = new BufferedReader (new InputStreamReader(socketClienteNavegador.getInputStream()));
            
            for (String line; (line = entradaCliente.readLine()) != null;) {
            	respuesta+=line+"\n";
            }
          envioCliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
	          if (servidorClienteNavegador != null) try { servidorClienteNavegador.close(); } catch (IOException logOrIgnore) {} 
	          if (socketClienteNavegador != null) try { socketClienteNavegador.close(); } catch (IOException logOrIgnore) {} 
	      }
        
        return respuesta;
	}

}
