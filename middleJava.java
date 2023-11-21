import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class MiddleWare {

    public static void main(String[] args) {
        // Iniciar el servidor en un hilo separado
        new Thread(() -> iniciarServidor(8080)).start();

        // Iniciar el cliente
        iniciarCliente("localhost", 8080);
    }

    static void iniciarServidor(int puerto) {
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor escuchando en el puerto " + puerto);

            // Esperar a que un cliente se conecte
            Socket clienteSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());

            // Crear los flujos de entrada/salida para la comunicación con el cliente
            BufferedReader entradaCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter salidaCliente = new PrintWriter(clienteSocket.getOutputStream(), true);

            // Leer mensajes del cliente y enviar respuestas
            while (true) {
                String mensaje = entradaCliente.readLine();
                if (mensaje == null || mensaje.equals("fin")) {
                    break;
                }
                System.out.println("Cliente dice: " + mensaje);

                // Responder al cliente
                salidaCliente.println("Respuesta desde el servidor: " + mensaje);
            }

            // Cerrar conexiones
            entradaCliente.close();
            salidaCliente.close();
            clienteSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void iniciarCliente(String servidor, int puerto) {
        try {
            // Conectar al servidor
            Socket socket = new Socket(servidor, puerto);
            System.out.println("Conectado al servidor en " + servidor + ":" + puerto);

            // Crear flujos de entrada/salida para la comunicación con el servidor
            BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salidaServidor = new PrintWriter(socket.getOutputStream(), true);

            // Enviar mensajes al servidor
            salidaServidor.println("Hola, servidor");

            // Leer respuestas del servidor
            String respuesta = entradaServidor.readLine();
            System.out.println("Servidor responde: " + respuesta);

            // Cerrar conexiones
            entradaServidor.close();
            salidaServidor.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
