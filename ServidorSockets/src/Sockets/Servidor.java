package Sockets;	 
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.File;
import java.util.ArrayList;
import javax.swing.filechooser.FileSystemView;


public class Servidor {
	
	public static void main(String args[]) {
		ServerSocket servidor;
		Socket conexion;
		DataOutputStream salida;
		DataInputStream entrada;
		int num = 0;
		try {
		servidor = new ServerSocket(3000);
		System.out.println("Servidor online");
		String sSistemaOperativo = System.getProperty("os.name");
		String usuarioActual = System.getProperty("user.name");
		InetAddress localHost = InetAddress.getLocalHost();
		Date date = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

		while (true) {
		conexion = servidor.accept();    
		num++;

		System.out.println("Conexion numero " + num + " desde: " + conexion.getInetAddress().getHostName());
		entrada = new DataInputStream(conexion.getInputStream());
		salida = new DataOutputStream(conexion.getOutputStream());
		String mensaje = entrada.readUTF();   
		System.out.println("Conexion n." + num + " mensaje: " + mensaje);
		salida.writeUTF("\n Sistema Operativo: " + sSistemaOperativo + "\n Nombre de equipo: " + localHost.getHostName() +  "\n Direccion MAC: " + obtieneMac() + "\n Unidades de disco: " + obtieneUnidades() + "\n Fecha y hora: " + hourdateFormat.format(date) + "\n Usuario actual: " + usuarioActual + "\n Procesos: " + obtieneProcesos()); 
		conexion.close();                        
		}
		} catch (IOException e) {
		}
		}
	
	private static String obtieneMac() {
        InetAddress ip;
	try {
		ip = InetAddress.getLocalHost();

		NetworkInterface network = NetworkInterface.getByInetAddress(ip);

		byte[] mac = network.getHardwareAddress();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		}
		
                return sb.toString();

	} catch (IOException e) {
            e.printStackTrace();
	}
        return "";
    }


    private static String obtieneProcesos() {        
        String procesos = "";
        
        try {
            Process p = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                procesos+=line+"\n";
            }
	} catch (Exception ex) {
            System.err.println(ex.getMessage());
	}

        return procesos;
    }
    
    private static ArrayList obtieneUnidades() {
        ArrayList discos = new ArrayList();
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();

        paths = File.listRoots();

        for(File path:paths)
        {
            discos.add(path + " " + fsv.getSystemTypeDescription(path));
        }
        
        return discos;
    }

}