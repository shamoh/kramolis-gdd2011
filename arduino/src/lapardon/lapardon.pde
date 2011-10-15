#include <Usb.h>
#include <AndroidAccessory.h>

#define COMMAND_NONE      0
#define COMMAND_PLAY      1
#define COMMAND_SIMULATE  2

// connected to the base of the transistor
#define TRANSISTOR_PIN    12

AndroidAccessory acc("shamoh",
    "LaPardon",
    "LaPardon - GDD2011 ADK/Arduino Project",
    "1.0",
    "http://gdd2011.kramolis.cz/",
    "0000000012345678");

//void setup();
//void loop();

byte pumpMap1[255];
byte pumpMap2[255];

byte currentCommand = COMMAND_NONE;
int transistorValue = 0; // value written to the transistor : 0-255
long iterace = 0;

void setup()
{
    Serial.begin(115200);
    Serial.println("\r\nStart\n");

    initConversionMaps();

    // set  the transistor pin as output:
    pinMode(TRANSISTOR_PIN, OUTPUT);

    acc.powerOn();
}

void loop()
{
//    byte MAX_BYTES = 280;
//    byte err;
//    byte idle;
//    static byte count = 0;
    byte msg[2];
//    long touchcount;

/*
    if ( iterace == 1000000000 ) {
        iterace = 0;
    }
    iterace += 1;
    Serial.print("-- ");
    Serial.print(iterace);
    Serial.println(" --");
*/
    if (acc.isConnected()) {
        int len = acc.read(msg, 2, 1);
//        int i;
//        byte b;
//        uint16_t val;
//        int x, y;
//        char c0;

        //
        // 1) READ COMMANDS
        //
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
                        currentCommand = COMMAND_PLAY;
                        Serial.print("PLAY [ ");
                        Serial.print(dataLen);
                        Serial.print(" ] : ");

                        for(int iii = 0; iii < dataLen-1; iii+=2) {
                            //TODO - cist po dvojicich a prevadet na pumpu a zvuk - zacit pumpou
                            if ( data[iii] < 16 ) {
                                Serial.print("0");
                            }
                            Serial.print(data[iii], HEX);
                            Serial.print("/");
//                            if ( data[iii+1] < 16 ) {
//                                Serial.print("0");
//                            }
                            Serial.print(data[iii+1]);
                            byte pump;
                            if ( '1' == data[iii+1] ) {
                                pump = pumpMap1[data[iii]];
                            } else {
                                pump = pumpMap2[data[iii]];
                            }
                            Serial.print(" [");
                            Serial.print(pump, DEC);
                            Serial.print("] ");
                        }
                        Serial.println(".");
                    } else if (command == COMMAND_SIMULATE) {
                        currentCommand = COMMAND_SIMULATE;
                        transistorValue = data[0];
                        if ( transistorValue == 0 ) {
                            currentCommand = COMMAND_NONE;
                        }
                        Serial.print("SIMULATE [ 1 ] : ");
                        if ( transistorValue < 16 ) {
                            Serial.print("0");
                        }
                        Serial.println(transistorValue, HEX);
                    }
                }
            }
        } else {
            if ( len > 0 ) {
                Serial.print("Wrong command META length: ");
                Serial.println(len);
            }
        }

        //
        // 2) PROCESS COMMAND
        //
        if (currentCommand == COMMAND_PLAY) {
            Serial.println("~~~ PLAY ~~~");

        } else if (currentCommand == COMMAND_SIMULATE) {
            Serial.print("### SIMULATE ### ");
            Serial.println(transistorValue);

//            analogWrite(TRANSISTOR_PIN, transistorValue);
        } else if (currentCommand == COMMAND_NONE) {
            Serial.println("--- NONE ---");

            resetOutputs();
        }

        //
        // 3) WRITE MESSAGE BACK
        //
    } else {
        // reset outputs to default values on disconnect
        Serial.println("Phone NOT connected!!!");
        resetOutputs();

        //TODO nebo bychom tady mohli delat nejake paradicky - s vodou, se zvukem, s diodami
    }

//    delay(10);
//    delay(50);
    delay(200);
}

void resetOutputs() {
//    analogWrite(TRANSISTOR_PIN, 0);

}

//
// ConversionMaps
//

void initConversionMaps()
{
    for(int iii = 0; iii < 255; iii++) {
        pumpMap1[iii] = 0;
        pumpMap2[iii] = 0;
    }
    //supported chars: cCdDefFgGabh
    //rest char: |
    pumpMap1['|'] = 1;
    pumpMap2['|'] = 2;
    pumpMap1['c'] = 10;
    pumpMap2['c'] = 130;
    pumpMap1['C'] = 20;
    pumpMap2['C'] = 140;
    pumpMap1['d'] = 30;
    pumpMap2['d'] = 150;
    pumpMap1['D'] = 40;
    pumpMap2['D'] = 160;
    pumpMap1['e'] = 50;
    pumpMap2['e'] = 170;
    pumpMap1['f'] = 60;
    pumpMap2['f'] = 180;
    pumpMap1['F'] = 70;
    pumpMap2['F'] = 190;
    pumpMap1['g'] = 80;
    pumpMap2['g'] = 200;
    pumpMap1['G'] = 90;
    pumpMap2['G'] = 210;
    pumpMap1['a'] = 100;
    pumpMap2['a'] = 220;
    pumpMap1['b'] = 110;
    pumpMap2['b'] = 230;
    pumpMap1['h'] = 120;
    pumpMap2['h'] = 240;
}


