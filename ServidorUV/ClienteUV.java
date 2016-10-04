
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteUV {

	public static void main(String [] array) throws InterruptedException 
    {
        int puerto=9090;
        String ip="74.208.234.113";
        while(true){
        	 try
             {           
        		 Socket cliente = new Socket(ip,puerto);
                 BufferedReader entCliente=new BufferedReader(new InputStreamReader(cliente.getInputStream()));              
                 String cadena= entCliente.readLine();
                 System.out.println(cadena);
                 String[] datos = cadena.split(",");
                 String get= datos[0];
                 get=get.substring(2);
                 String host= datos[1];
                 String port= datos[2];
                 port=port.replace(" ","");
                 
                 int puertohttp=Integer.parseInt(port);
                 PrintWriter salCliente = new PrintWriter(cliente.getOutputStream()); 
                 ClienteUV proceso = new ClienteUV();
                 String resul= proceso.PeticionCliente(get,host,puertohttp);
                 salCliente.println(resul);            
                 salCliente.close();
                 entCliente.close();
                 cliente.close();
             }
             catch(Exception e)
             {
                 System.out.println(e.getMessage());
             }
        	 finally {
        		 Thread.sleep(1000);
        	 }
        }
    }
	
	public String PeticionCliente(String get,String host, int puerto) throws IOException{
		 
		  String hostname = host;
	      int port = puerto;
	      String resul="";
	      Socket socket = null;
	      PrintWriter writer = null;
	      BufferedReader reader = null;

	      try {
	          socket = new Socket(hostname, port);
	          
	          writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	          writer.println(get);
	          writer.println("Host: " + hostname);
	          writer.println("Accept: */*");
	          writer.println("User-Agent: Java");
	          writer.println("");
	          writer.flush();
	          try{
	       	 
	          	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	              for (String line; (line = reader.readLine()) != null;) {
	            	  resul+=line+"\n";
	              }
	          }
	          catch(IOException logOrIgnore){
	          	 if (writer != null) { writer.close(); }
	          }
	          
	      } finally {
	          if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {} 
	          if (writer != null) { writer.close(); }
	          if (socket != null) try { socket.close(); } catch (IOException logOrIgnore) {} 
	      }
			
	      return resul;
		}
}
