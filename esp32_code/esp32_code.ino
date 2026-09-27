#include <WiFi.h>
#include <HTTPClient.h>
#include <DHT.h>

// ---------------- WIFI ----------------
const char* ssid = "YOUR_WIFI_NAME";
const char* password = "YOUR_WIFI_PASSWORD";

// ---------------- SERVER URL ----------------
// Replace with your laptop IP
String serverName = "http://192.168.1.5:8080/api/sensor-data/add";

// ---------------- SENSOR PINS ----------------
#define GAS_SENSOR_PIN 34
#define FLAME_SENSOR_PIN 35
#define CURRENT_SENSOR_PIN 32
#define DHT_PIN 4

// ---------------- OUTPUT PINS ----------------
#define RED_LED_PIN 18
#define GREEN_LED_PIN 19
#define BUZZER_PIN 21

// ---------------- DHT ----------------
#define DHTTYPE DHT11
DHT dht(DHT_PIN, DHTTYPE);

// ---------------- THRESHOLDS ----------------
int gasThreshold = 2000;
float tempThreshold = 45.0;

void setup() {

  Serial.begin(115200);

  pinMode(FLAME_SENSOR_PIN, INPUT);

  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  dht.begin();

  // ---------------- WIFI CONNECT ----------------

  WiFi.begin(ssid, password);

  Serial.print("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi Connected");
  Serial.println(WiFi.localIP());
}

void loop() {

  // ---------------- READ SENSOR VALUES ----------------

  int gasValue = analogRead(GAS_SENSOR_PIN);

  int flameValue = digitalRead(FLAME_SENSOR_PIN);

  int currentValue = analogRead(CURRENT_SENSOR_PIN);

  float temperature = dht.readTemperature();

  // ---------------- PRINT VALUES ----------------

  Serial.println("------------------------");

  Serial.print("Gas Value: ");
  Serial.println(gasValue);

  Serial.print("Flame Value: ");
  Serial.println(flameValue);

  Serial.print("Current Value: ");
  Serial.println(currentValue);

  Serial.print("Temperature: ");
  Serial.println(temperature);

  // ---------------- ALERT LOGIC ----------------

  if (gasValue < gasThreshold &&
      flameValue == 1 &&
      temperature < tempThreshold) {

    digitalWrite(GREEN_LED_PIN, HIGH);

    digitalWrite(RED_LED_PIN, LOW);
    digitalWrite(BUZZER_PIN, LOW);
  }
  else {

    digitalWrite(GREEN_LED_PIN, LOW);

    digitalWrite(RED_LED_PIN, HIGH);
    digitalWrite(BUZZER_PIN, HIGH);
  }

  // ---------------- SEND DATA TO SPRING BOOT ----------------

  if (WiFi.status() == WL_CONNECTED) {

    HTTPClient http;

    http.begin(serverName);

    http.addHeader("Content-Type", "application/json");

    String jsonData = "{";
    jsonData += "\"deviceId\":\"KITCHEN_001\",";
    jsonData += "\"gasValue\":" + String(gasValue) + ",";
    jsonData += "\"flameValue\":" + String(flameValue) + ",";
    jsonData += "\"currentValue\":" + String(currentValue) + ",";
    jsonData += "\"temperature\":" + String(temperature);
    jsonData += "}";

    int httpResponseCode = http.POST(jsonData);

    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);

    Serial.println("Data Sent:");
    Serial.println(jsonData);

    http.end();
  }

  delay(5000);
}