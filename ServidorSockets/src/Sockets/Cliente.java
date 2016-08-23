package Sockets; 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
	 
public class Cliente {
	
public static void main(String args[]) {
Socket cliente;
DataInputStream entrada;
DataOutputStream salida;
String mensaje, respuesta;
try {
cliente = new Socket(InetAddress.getLocalHost(), 3000);  
entrada = new DataInputStream(cliente.getInputStream()); 
salida = new DataOutputStream(cliente.getOutputStream());
mensaje = "SO, nombre equipo, dirección mac, unidades, fecha y hora, usuario, procesos y velocidad del procesador";
salida.writeUTF(mensaje);                                 
respuesta = entrada.readUTF();                           
System.out.println("El cliente solicita: " + mensaje);
System.out.println("Respuesta Server: " + respuesta);
cliente.close();                                        
} catch (IOException e) {
System.out.println("Error: " + e.getMessage());
}
}
}