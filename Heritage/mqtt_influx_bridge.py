import asyncio
import json
from fastapi import FastAPI
from aiomqtt import Client
from influxdb_client import InfluxDBClient, Point
from influxdb_client.client.write_api import SYNCHRONOUS

# === 1. Configuración de InfluxDB y MQTT ===
INFLUX_URL = "http://178.104.48.129:8086"
INFLUX_TOKEN = "token de Influx"
INFLUX_ORG = "proyecto_ingenieria"
INFLUX_BUCKET = "sensor_data"

# Si este script corre en el mismo servidor que el Broker MQTT, usa "localhost"
MQTT_BROKER = "localhost"
MQTT_TOPIC = "sensor/telemetry" # El canal donde el ESP32 publica los datos

# === 2. Inicialización de Clientes ===
app = FastAPI()
influx_client = InfluxDBClient(url=INFLUX_URL, token=INFLUX_TOKEN, org=INFLUX_ORG)
write_api = influx_client.write_api(write_options=SYNCHRONOUS)

# === 3. Lógica Principal: Puente entre MQTT e InfluxDB ===
async def mqtt_to_influx_bridge():
    print(f"🚀 El puente MQTT-InfluxDB está activo. Escuchando el tópico: {MQTT_TOPIC}...")
    
    async with Client(MQTT_BROKER) as client:
        await client.subscribe(MQTT_TOPIC)
        async for message in client.messages:
            try:
                # El ESP32 envía JSON: {"temperature": 25.5, "humidity": 60}
                payload = json.loads(message.payload.decode())
                print(f"📩 Datos recibidos: {payload}")

                # Construir el punto de datos para InfluxDB
                point = Point("environment_data") \
                    .tag("dispositivo", "esp32_casa_espana") \
                    .field("temperatura", float(payload.get("temperature", 0))) \
                    .field("humedad", float(payload.get("humidity", 0)))

                # Guardar en la base de datos
                write_api.write(bucket=INFLUX_BUCKET, org=INFLUX_ORG, record=point)
                print("✅ Datos guardados correctamente en InfluxDB")

            except Exception as e:
                print(f"❌ Error al procesar los datos: {e}")

# === 4. Eventos de inicio de FastAPI ===
@app.on_event("startup")
async def startup_event():
    # Creamos una tarea en segundo plano para no bloquear la API de FastAPI
    asyncio.create_task(mqtt_to_influx_bridge())

@app.get("/")
def read_root():
    return {
        "estado": "El puente IoT está funcionando",
        "mensaje": "Esperando telemetría del ESP32"
    }