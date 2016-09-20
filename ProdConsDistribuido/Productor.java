package ProdConsDistribuido;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

public class Productor extends Thread{
	
	public static Socket socket = null;
	public static Productor productor;

	
	public static void main(String[] args) {
				
		try {
	        socket = new Socket("192.168.0.9", 3000);
		} catch (IOException e) {
			System.out.print("Error en conexión del Socket TCP: "+e);
		}
		
		while(true){
	        try{
				InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String mensaje = br.readLine();
                System.out.println(mensaje);
                
                productor = new Productor("P1");
                productor.start();
                
                break;
	        }catch(Exception e){
		        e.printStackTrace();
		        System.out.println("Error al conectar el cliente!");
	        }
	    }
	}
	

	public Productor(String name){
		super.setName(name);
	}
	
	public void run(){
        String nombre = Thread.currentThread().getName();
        System.out.println(nombre+" iniciado!");
		while(true){
			try {
	            Thread.sleep(1000);
	            
	            OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
      
                HashMap<String, String> hash = new HashMap<>();
       			hash.put("Nombre",productor.getName());
       			hash.put("Tipo","2");
       			
	            bw.write(hash.toString()+" \n");
                bw.flush();
                
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}