import time
import os

RUTA_ENTRADA = os.path.join("..", "canal_comunicacion", "entrada_servidor.txt")
RUTA_SALIDA = os.path.join("..", "canal_comunicacion", "salida_servidor.txt")

def inicializar_archivos():
    """Asegura que los archivos de comunicación existan."""
    os.makedirs(os.path.dirname(RUTA_ENTRADA), exist_ok=True)
    if not os.path.exists(RUTA_ENTRADA):
        with open(RUTA_ENTRADA, "w", encoding="utf-8") as f:
            f.write("")
    if not os.path.exists(RUTA_SALIDA):
        with open(RUTA_SALIDA, "w", encoding="utf-8") as f:
            f.write("")

def procesar_servidor():
    inicializar_archivos()
    print("=== Servidor activo e iniciado ===")
    print(f"Escuchando peticiones en: {RUTA_ENTRADA}")
    
    ultimo_mensaje_procesado = ""

    while True:
        try:
            if os.path.exists(RUTA_ENTRADA):
                with open(RUTA_ENTRADA, "r", encoding="utf-8") as f:
                    contenido = f.read().strip()
                if contenido and contenido != ultimo_mensaje_procesado:
                    print(f"\n[SERVIDOR] Nuevo mensaje recibido: '{contenido}'")
                    respuesta = f"PROCESADO: {contenido.upper()}"
                    with open(RUTA_SALIDA, "w", encoding="utf-8") as f_salida:
                        f_salida.write(respuesta)
                    
                    print(f"[SERVIDOR] Respuesta generada y enviada: '{respuesta}'")
                    ultimo_mensaje_procesado = contenido
        except Exception as e:
            print(f"[SERVIDOR] Error durante el procesamiento: {e}")
        time.sleep(1)

if __name__ == "__main__":
    procesar_servidor()