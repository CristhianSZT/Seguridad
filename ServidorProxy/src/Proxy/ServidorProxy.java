package Proxy;

import java.net.*;
import java.io.*;

public class ServidorProxy {
	
    public static void main(String[] args) throws IOException {
        
    	ServerSocket servidorSocket = null;
        boolean escuchando = true;

        int puerto = 10000;	
        
        try {
            servidorSocket = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en: " + puerto);
        } catch (IOException e) {
            System.err.println("Error al conectar el servidor: " + args[0]);
            System.exit(-1);
        }

        while (escuchando) {
            new ConexionHTTP(servidorSocket.accept()).start();
        }
        servidorSocket.close();
    }
}