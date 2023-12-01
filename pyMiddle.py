from pyServer import start_server
from pyCliente import start_client


def main():
    #Iniciar el servidor
    server_thread = start_server()

    #iniciar el cliente
    start_client()

    # Esperar a que el hilo del servidor termine (puede omitirse si no es necesario)
    #server_thread.join()


if __name__ == "__main__":
    main()