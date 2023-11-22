import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class MiddleWare {

    private static final String LOG_FILE = "log.txt";

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

            while (true) {
                // Esperar a que un cliente se conecte
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());

                // Crear un hilo separado para manejar la comunicación con el cliente
                new Thread(() -> {
                    try {
                        // Crear los flujos de entrada/salida para la comunicación con el cliente
                        BufferedReader entradaCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                        PrintWriter salidaCliente = new PrintWriter(clienteSocket.getOutputStream(), true);

                        // Leer y enviar mensajes al cliente
                        String mensaje;
                        do {
                            mensaje = entradaCliente.readLine();
                            System.out.println("Cliente dice: " + mensaje);

                            // Escribir el mensaje recibido en el archivo de registro
                            escribirEnArchivo(LOG_FILE, "Cliente dice: " + mensaje);

                            // Responder al cliente
                            salidaCliente.println("Respuesta desde el servidor: " + mensaje);

                            // Escribir el mensaje enviado en el archivo de registro
                            escribirEnArchivo(LOG_FILE, "Respuesta desde el servidor: " + mensaje);
                        } while (!mensaje.equals("fin"));

                        // Cerrar conexiones
                        entradaCliente.close();
                        salidaCliente.close();
                        clienteSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
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

            // Leer y enviar mensajes al servidor
            BufferedReader entradaUsuario = new BufferedReader(new InputStreamReader(System.in));
            String mensaje;
            do {
                // Leer mensaje del usuario
                mensaje = entradaUsuario.readLine();

                // Escribir el mensaje enviado en el archivo de registro
                escribirEnArchivo(LOG_FILE, "Cliente dice: " + mensaje);

                // Enviar mensaje al servidor
                salidaServidor.println(mensaje);

                // Leer respuesta del servidor
                String respuesta = entradaServidor.readLine();
                System.out.println("Servidor responde: " + respuesta);

                // Escribir la respuesta recibida en el archivo de registro
                escribirEnArchivo(LOG_FILE, "Servidor responde: " + respuesta);
            } while (!mensaje.equals("fin"));

            // Cerrar conexiones
            entradaUsuario.close();
            entradaServidor.close();
            salidaServidor.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void escribirEnArchivo(String archivo, String mensaje) {
        try {
            FileWriter fileWriter = new FileWriter(archivo, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(mensaje);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}