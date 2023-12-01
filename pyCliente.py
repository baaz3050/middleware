import socket

def start_client():
    host = '127.0.0.1'
    port = 8888

    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((host, port))

    welcome_msg = client.recv(1024).decode('utf-8')
    print(welcome_msg)

    while True:
        message = input("Ingrese un mensaje (o 'cerrar' para salir): ")

        if message.lower() == 'cerrar':
            # Enviar mensaje de cierre al servidor
            client.send(message.encode('utf-8'))
            break
        elif message.startswith('enviar_a'):
            # Enviar mensaje a un cliente específico
            client.send(message.encode('utf-8'))
        else:
            # Enviar mensaje al servidor
            client.send(message.encode('utf-8'))

        response = client.recv(1024).decode('utf-8')
        print(response)

    client.close()

if __name__ == "__main__":
    start_client()
