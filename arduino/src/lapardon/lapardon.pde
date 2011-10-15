#include <Usb.h>
#include <AndroidAccessory.h>

#define  COMMAND_PLAY      1
#define  COMMAND_SIMULATE  2

AndroidAccessory acc("shamoh",
    "LaPardon",
    "LaPardon - GDD2011 ADK/Arduino Project",
    "1.0",
    "http://gdd2011.kramolis.cz/",
    "0000000012345678");

void setup();
void loop();

void setup()
{
    Serial.begin(115200);
    Serial.println("\r\nStart\n");

    acc.powerOn();
}

void loop()
{
    byte MAX_BYTES = 280;
    byte err;
    byte idle;
    static byte count = 0;
    byte msg[2];
    long touchcount;

    if (acc.isConnected()) {
        int len = acc.read(msg, 2, 1);
        int i;
        byte b;
        uint16_t val;
        int x, y;
        char c0;

        if (len == 2) {
            byte command = msg[0];
            byte dataLength = msg[1];

            Serial.print(command, DEC);
            Serial.print(" : ");
            Serial.println(dataLength, DEC);

            byte data[dataLength];
            int dataLen = -1;

            while (dataLen == -1) {
                dataLen = acc.read(data, dataLength, 1);
                if ( dataLength != dataLen ) {
                    Serial.print("Wrong command DATA length: ");
                    Serial.println(dataLen);
                } else {
                    if (command == COMMAND_PLAY) {
                        Serial.print("PLAY [ ");
                        Serial.print(dataLen);
                        Serial.print(" ] : ");

                        for(int iii = 0; iii < dataLen; iii++) {
                            //TODO - cist po dvojicich a prevadet na pumpu a zvuk - zacit pumpou
                            if ( data[iii] < 16 ) {
                                Serial.print("0");
                            }
                            Serial.print(data[iii], HEX);
                            Serial.print(" ");
                        }
                        Serial.println(".");
                    } else if (COMMAND_SIMULATE == command) {
                        Serial.print("SIMULATE [ 1 ] : ");
                        if ( data[0] < 16 ) {
                            Serial.print("0");
                        }
                        Serial.println(data[0], HEX);
                    }
                }
            }
        } else {
            if ( len > 0 ) {
                Serial.print("Wrong command META length: ");
                Serial.println(len);
            }
        }

        msg[0] = 0x1;
        Serial.println("connected");
    } else {
        // reset outputs to default values on disconnect
        Serial.println("Phone NOT connected!!!");
    }

    delay(10);
//    delay(50);
}

