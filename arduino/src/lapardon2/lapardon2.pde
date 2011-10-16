#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("shamoh",
    "LaPardon",
    "LaPardon - GDD2011 ADK/Arduino Project",
    "1.0",
    "http://gdd2011.kramolis.cz/",
    "0000000012345678");

void setup()
{
    Serial.begin(115200);
    Serial.println("\r\nStart\n");

    acc.powerOn();
}

void loop()
{
    byte msg[2];

    if (acc.isConnected()) {
//        int len = acc.read(msg, 2, 1);

        Serial.println("*** Connected!!!");
    } else {
        Serial.println("Phone NOT connected!!!");
    }

    delay(10);
//    delay(50);
//    delay(200);
}

/*
Data packet error: 5
Device addressed... Requesting device descriptor.
Setup packet error: D
Device descriptor cannot be retrieved. Trying again
*/
