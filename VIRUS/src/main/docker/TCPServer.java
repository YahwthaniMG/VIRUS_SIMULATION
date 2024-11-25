import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TCPServer {
    private static final int PORT = 7777;
    private static final List<String> deathRecords = Collections.synchronizedList(new ArrayList<>());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        printHeader();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en puerto " + PORT);
            
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\nNueva conexión aceptada desde: " + 
                                    clientSocket.getInetAddress().getHostAddress());
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    new Thread(clientHandler).start();
                    
                } catch (IOException e) {
                    System.err.println("Error al aceptar conexión: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static void printHeader() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║           Registro de Muertes VIRUS          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    public static void addDeathRecord(String message) {
        String timestamp = dateFormat.format(new Date());
        String record = timestamp + " - " + message;
        deathRecords.add(record);
        
        // Imprimir el nuevo registro
        System.out.println("\n=== Nuevo Registro de Muerte ===");
        System.out.println(record);
        
        // Mostrar los últimos 5 registros
        printLastRecords(5);
    }

    private static void printLastRecords(int count) {
        System.out.println("\n=== Últimos " + count + " Registros ===");
        deathRecords.stream()
            .skip(Math.max(0, deathRecords.size() - count))
            .forEach(System.out::println);
        System.out.println("================================");
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String mensajeCliente = in.readLine();
            TCPServer.addDeathRecord(mensajeCliente);
            
            // Enviar respuesta al cliente
            out.println("Registro de muerte recibido y almacenado exitosamente");
            
        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }
}