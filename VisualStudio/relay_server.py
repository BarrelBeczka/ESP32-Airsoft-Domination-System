import socket
import threading

# Konfiguracja
LOCAL_WOKWI_HOST = '127.0.0.1'
LOCAL_WOKWI_PORT = 8180      # Port na którym domyślnie nasłuchuje Wokwi

# Port, na który będzie łączyć się Android
RELAY_PORT = 8181

def handle_client(client_socket):
    target_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        # Połączenie do zablokowanego lokalnego portu Wokwi
        target_socket.connect((LOCAL_WOKWI_HOST, LOCAL_WOKWI_PORT))
        
        def forward(src, dst):
            try:
                while True:
                    data = src.recv(4096)
                    if not data: break
                    dst.sendall(data)
            except:
                pass
            finally:
                src.close()
                dst.close()

        # Dwa wątki do obsługi ruchu w obie strony
        threading.Thread(target=forward, args=(client_socket, target_socket), daemon=True).start()
        threading.Thread(target=forward, args=(target_socket, client_socket), daemon=True).start()
    except Exception as e:
        print(f"[ERROR] Nie można połączyć się z Wokwi na {LOCAL_WOKWI_HOST}:{LOCAL_WOKWI_PORT}. Czy symulacja jest włączona?")
        client_socket.close()

def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Bindowanie na 0.0.0.0, aby Android (10.0.2.2) mógł się połączyć
    server.bind(('0.0.0.0', RELAY_PORT))
    server.listen(5)
    print("==================================================")
    print(f"[OK] TUNEL POSREDNICZACY (RELAY SERVER) URUCHOMIONY!")
    print(f"Nadsłuchuję dla Androida na porcie: {RELAY_PORT}")
    print(f"Przekierowuję ruch do Wokwi na: {LOCAL_WOKWI_HOST}:{LOCAL_WOKWI_PORT}")
    print("==================================================")
    print(f"Wpisz w aplikacji Android następujący adres IP:")
    print(f"->  10.0.2.2:{RELAY_PORT}  <-")
    print("==================================================")

    try:
        while True:
            client_socket, addr = server.accept()
            print(f"[INFO] Nowe połączenie z {addr}")
            handle_client(client_socket)
    except KeyboardInterrupt:
        print("\nWyłączanie tunelu...")
    finally:
        server.close()

if __name__ == '__main__':
    main()
