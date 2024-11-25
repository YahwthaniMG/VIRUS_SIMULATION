package com.virus.network;

import java.io.*;
import java.net.*;

public class TCPClient {
    private static final String SERVER_ADDRESS = "10.7.5.217";
    private static final int SERVER_PORT = 7777;
    
    public void sendMessage(String message) {
        System.out.println("TCPClient: Intentando enviar mensaje: " + message);
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT), 5000);
            
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                System.out.println("TCPClient: Conexión establecida");
                out.println(message);
                
                String response = in.readLine();
                System.out.println("TCPClient: Respuesta recibida: " + response);
            }
        } catch (IOException e) {
            System.err.println("TCPClient: Error de conexión - " + e.getMessage());
        }
    }
}