package Proxy;

import java.net.*;
import java.io.*;
import java.util.*;

public class ConexionHTTP extends Thread {
    
	private Socket socket = null;
    
    public ConexionHTTP(Socket socket) {
        super("ConexionHTTP");
        this.socket = socket;
    }

    public void run() {
        try {
        	
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"8859_1"),true);

            String entrada;
            int contador = 0;
            String urlSolicitada = "";
            
            while ((entrada = in.readLine()) != null) {
                try {
                    StringTokenizer tok = new StringTokenizer(entrada);
                    tok.nextToken();
                } catch (Exception e) {
                    break;
                }
                if (contador == 0) {
                    String[] datos = entrada.split(" ");
                    urlSolicitada = datos[1];
                    System.out.println("Solicitud de: " + urlSolicitada);
                }
                contador++;
            }

            String respuesta ="<H5>Servidor Proxy de Cristhian Zavala</H5>";
            out.println(respuesta);
          
        } catch (Exception e) {
        	System.err.println("Ha ocurrido un error: " + e);
        }
        
        if (socket != null) {
        	try {
        		socket.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
    }
}