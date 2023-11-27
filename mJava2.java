import java.io.*;
import java.net.*;

class MiddleWare {

    public static void main(String[] args) {
        // Iniciar el servidor en un hilo separado
        new Thread(() -> iniciarServidor(8080)).start();

        // Iniciar el cliente en un hilo separado
        new Thread(() -> iniciarCliente("192.168.224.133", 8080)).start();
    }

    static void iniciarServidor(int puerto) {
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor escuchando en el puerto " + puerto);

            // Bucle para manejar múltiples clientes
            while (true) {
                // Esperar a que un cliente se conecte
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());

                // Crear un hilo para manejar la conexión con el cliente
                new Thread(() -> manejarConexion(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void iniciarCliente(String direccionIP, int puerto) {
        try {
            // Conectar al servidor
            Socket socket = new Socket(direccionIP, puerto);
            System.out.println("Conectado al servidor en " + direccionIP + ":" + puerto);

            // Crear flujos de entrada/salida para la comunicación con el servidor
            BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salidaServidor = new PrintWriter(socket.getOutputStream(), true);

            // Hilo para manejar la entrada del servidor
            new Thread(() -> {
                try {
                    // Leer respuestas del servidor
                    while (true) {
                        String respuesta = entradaServidor.readLine();
                        if (respuesta == null || respuesta.equals("fin")) {
                            break; // Salir del bucle si el servidor envía "fin"
                        }
                        System.out.println("Servidor responde: " + respuesta);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Cerrar conexiones con el servidor
                    try {
                        entradaServidor.close();
                        salidaServidor.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Hilo para manejar la entrada del usuario desde la consola
            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    // Leer mensaje desde la consola y enviarlo al servidor
                    String mensajeUsuario = consola.readLine();
                    salidaServidor.println(mensajeUsuario);

                    // Salir si el usuario ingresa "finCliente"
                    if (mensajeUsuario.equals("finCliente")) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void manejarConexion(Socket clienteSocket) {
        try {
            // Crear los flujos de entrada/salida para la comunicación con el cliente
            BufferedReader entradaCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter salidaCliente = new PrintWriter(clienteSocket.getOutputStream(), true);

            // Leer mensajes del cliente y enviar respuestas
            while (true) {
                String mensaje = entradaCliente.readLine();
                if (mensaje == null || mensaje.equals("fin")) {
                    break; // Salir del bucle si el cliente envía "fin"
                }
                System.out.println("Cliente dice: " + mensaje);

                // Responder al cliente
                salidaCliente.println("Respuesta desde el servidor: " + mensaje);
            }

            // Cerrar conexiones con un cliente específico
            entradaCliente.close();
            salidaCliente.close();
            clienteSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
