import time
import os

RUTA_ENTRADA = os.path.join("..", "canal_comunicacion", "entrada_servidor.txt")
RUTA_SALIDA = os.path.join("..", "canal_comunicacion", "salida_servidor.txt")

def cliente():
    print("=== Cliente de Sistemas Distribuidos ===")
    
    while True:
        mensaje = input("\nIngrese un mensaje para enviar al servidor (o 'salir' para terminar): ")
        if mensaje.lower() == "salir":
            print("Cerrando el cliente...")
            break
        
        if not mensaje.strip():
            print("El mensaje no puede estar vacío.")
            continue

        if os.path.exists(RUTA_SALIDA):
            with open(RUTA_SALIDA, "w", encoding="utf-8") as f:
                f.write("")
        with open(RUTA_ENTRADA, "w", encoding="utf-8") as f:
            f.write(mensaje)
        print("[CLIENTE] Mensaje escrito en entrada_servidor.txt correctamente.")

        print("[CLIENTE] Esperando respuesta del servidor...")
        time.sleep(2)

        if os.path.exists(RUTA_SALIDA):
            with open(RUTA_SALIDA, "r", encoding="utf-8") as f:
                respuesta = f.read().strip()
            
            if respuesta:
                print(f"[CLIENTE] Respuesta recibida del Servidor: {respuesta}")
            else:
                print("[CLIENTE] No se recibió respuesta aún o el archivo está vacío.")
        else:
            print("[CLIENTE] El archivo de salida no existe.")

if __name__ == "__main__":
    cliente()