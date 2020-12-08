
#include <Adafruit_NeoPixel.h>
#include <ESP8266WiFi.h>  //for esp8266
//#include <WiFi.h>  //for esp32
#include <WiFiUdp.h>


#define PIN 2 

// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS 255 // Popular NeoPixel ring size
  byte incomingPacket[4]; //assining a dynamic size byte array for incoming buffer

Adafruit_NeoPixel pixels(NUMPIXELS, PIN, NEO_RGB + NEO_KHZ800);
WiFiUDP Udp;
unsigned int localUdpPort = 55667; //port for communicating with app

int  udpSize,r,g,b,n;
#define DELAYVAL 1 // Time (in milliseconds) to pause between pixels

const char* ssid = "Simorgh";
const char* password = "h,2@&fg0+";

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("WiFi connected");

  Udp.begin(localUdpPort);        //begin udp connection
  pixels.begin(); // INITIALIZE NeoPixel strip object (REQUIRED)
}

void loop() {
  //delay(5);

  pixels.clear(); // Set all pixel colors to 'off'
  int packetSize = Udp.parsePacket(); //parsig the size of incoming udp packet

  if (packetSize)
  {
    //Serial.println("Here");
    Udp.read(incomingPacket , packetSize); //reading udp array
    Udp.flush();
    Serial.write(incomingPacket, packetSize);
    n = incomingPacket[0];
    r = incomingPacket[1];
    g = incomingPacket[2];
    b = incomingPacket[3];
  }
  //int r= random(0,255);
  //int g = random(0,255);
  //int b = random(0,255);
      pixels.setBrightness(55);

for(int i=0;i<n;i++){
    pixels.setPixelColor(i, pixels.Color(r, g, b ));

    pixels.show();   // Send the updated pixel colors to the hardware.
    delay(DELAYVAL); // Pause before next pass through loop
  }
}
