#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <ArduinoJson.h>

// 1. Configuración de Hardware y Sensor
#define DHTPIN 25          // Conectado al pin G25
#define DHTTYPE DHT11      // Tipo de sensor: DHT11
DHT dht(DHTPIN, DHTTYPE);

// 2. Configuración de WiFi (Datos de tu red en España)
const char* ssid = "DIGIFIBRA-hCkh";
const char* password = "kbGKt7NHfES5";

// 3. Configuración de MQTT
const char* mqtt_server = "178.104.48.129";
const char* topic = "sensor/telemetry";
const char* clientID = "esp32_real_sensor_ino";

WiFiClient espClient;
PubSubClient client(espClient);

// Función para conectar al WiFi
void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Conectando a ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  // Esperar hasta que se establezca la conexión
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi conectado");
  Serial.println("Dirección IP: ");
  Serial.println(WiFi.localIP());
}

// Función para reconectar al servidor MQTT si se pierde la conexión
void reconnect() {
  while (!client.connected()) {
    Serial.print("Intentando conexión MQTT...");
    // Intentar conectar
    if (client.connect(clientID)) {
      Serial.println("conectado");
    } else {
      Serial.print("falló, rc=");
      Serial.print(client.state());
      Serial.println(" reintentando en 5 segundos");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  dht.begin();
}

void loop() {
  // Asegurar que el cliente MQTT permanezca conectado
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Leer datos cada 10 segundos
  static unsigned long lastMsg = 0;
  unsigned long now = millis();
  if (now - lastMsg > 10000) {
    lastMsg = now;

    // Leer los datos reales del sensor
    float h = dht.readHumidity();
    float t = dht.readTemperature();

    // Verificar si la lectura fue exitosa (isnan = Is Not a Number)
    if (isnan(h) || isnan(t)) {
      Serial.println("¡Error al leer el sensor DHT!");
      return;
    }

    // Usar ArduinoJson para construir el mensaje JSON
    StaticJsonDocument<200> doc;
    doc["temperature"] = t;
    doc["humidity"] = h;

    char buffer[256];
    serializeJson(doc, buffer);

    // Publicar el mensaje en el tópico MQTT
    Serial.print("Publicando mensaje: ");
    Serial.println(buffer);
    client.publish(topic, buffer);
  }
}