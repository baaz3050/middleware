import socket
import threading

def handle_client(client_socket):
    while True:
        data = client_socket.recv(1024)
        if not data:
            break
        print(f"Mensaje recibido: {data.decode()}")
        confirmacion_mensaje = "Mensaje recibido..."
        client_socket.send(confirmacion_mensaje.encode())

#Parte del codigo que indica a que socket se quiere conectar
def start_client_mv2():
    #Digitar a que socket desea mandarle mensaje
    addr = input("address: ")
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((addr, 5555))

    client_receive_thread = threading.Thread(target=handle_client, args=(client,))
    client_receive_thread.start()

    while True:
        mensaje = input("Ingrese un mensaje: ")
        with open("misMensajes.txt", "a") as archivo:
            archivo.write(mensaje + "\n")
        client.send(mensaje.encode())

def start_client_mv3():
    #Digitar a que socket desea mandarle mensaje
    addr = input("address: ")
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((addr, 5555))

    client_receive_thread = threading.Thread(target=handle_client, args=(client,))
    client_receive_thread.start()

    while True:
        mensaje = input("Ingrese un mensaje: ")
        with open("misMensajes.txt", "a") as archivo:
            archivo.write(mensaje + "\n")
        client.send(mensaje.encode())

#Parte de codigo que indica por que direccion y puerto va a estar escuchando el socket
def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('127.0.0.1', 5555))
    server.listen(5)
    print("Servidor escuchando en el puerto 5555...")

    while True:
        client, addr = server.accept()
        print(f"Conexion entrante desde {addr}")
        client_handler = threading.Thread(target=handle_client, args=(client,))
        client_handler.start()

server_thread = threading.Thread(target=start_server)
client_thread_mv2 = threading.Thread(target=start_client_mv2)
client_thread_mv3 = threading.Thread(target=start_client_mv3)

server_thread.start()
client_thread_mv2.start()
client_thread_mv3.start()
