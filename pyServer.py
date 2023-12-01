import socket
import threading
import time

# Estructura para rastrear clientes
clients = {}

def handle_client(client_socket, addr):
    client_socket.send(b"Bienvenido al servidor. Envia 'cerrar' para desconectar.\n")

    # Agrega al diccionario de clientes
    clients[addr] = client_socket

    while True:
        data = client_socket.recv(1024).decode('utf-8')

        if not data:
            print(f"[{addr}] Cliente desconectado.")
            del clients[addr]
            break

        print(f"[{addr}] Mensaje recibido: {data}")

        with open('mensajes.txt', 'a') as file:
            timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
            file.write(f"[{timestamp}] [{addr}] {data}\n")

        # Envía el mensaje a todos los clientes conectados
        broadcast_message(addr, f"{timestamp} - {data}")

    client_socket.close()

def broadcast_message(sender_addr, message):
    # Envía el mensaje a todos los clientes excepto al remitente
    for addr, client_socket in clients.items():
        if addr != sender_addr:
            try:
                client_socket.send(message.encode('utf-8'))
            except socket.error:
                # Si hay un error al enviar el mensaje al cliente, se elimina de la lista
                print(f"[{addr}] Cliente desconectado.")
                del clients[addr]

def start_server():
    host = '127.0.0.1'
    port = 8888

    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind((host, port))
    server.listen(5)

    print(f"[*] Servidor escuchando en {host}:{port}")

    while True:
        client, addr = server.accept()
        print(f"[*] Conexión aceptada de {addr}")

        client_handler = threading.Thread(target=handle_client, args=(client, addr))
        client_handler.start()

if __name__ == "__main__":
    start_server()
