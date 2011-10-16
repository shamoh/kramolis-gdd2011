#include <Usb.h>
#include <AndroidAccessory.h>

#define COMMAND_NONE      0
#define COMMAND_PLAY      1
#define COMMAND_SIMULATE  2

#define MESSAGE_NONE      0
#define MESSAGE_ERROR     1
#define MESSAGE_KNOCK     2
#define MESSAGE_MIC       3
#define MESSAGE_MISSION_COMPLETED  4

#define TONE_DELAY        1000

#define PUMP_START_VALUE  150
#define PUMP_START_DELAY  1000

// connected to the base of the transistor
#define TRANSISTOR_PIN    12

#define TRACE  false
#define DEBUG  true
#define INFO   true
#define WARN   true
#define ERROR  true

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
byte message = MESSAGE_NONE;

int transistorValue = 0; // value written to the transistor : 0-255
//long iterace = 0;
//unsigned long statusTime = 0;
int okLed = 0;


void setup()
{
    Serial.begin(115200);
    Serial.println("\r\n***** Start *****\n*****************\n");

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

                        okLedOn();
                        analogWrite(TRANSISTOR_PIN, PUMP_START_VALUE);
                        {
                            int max = 8;
                            for (int iii = 0; iii < max; iii++) {
                                delay(PUMP_START_DELAY/max);
                                switchOkLed();
                            }
                        }
                        okLedOn();
                        for(int iii = 0; iii < dataLen-1; iii+=2) {
                            if ( data[iii] < 16 ) {
                                Serial.print("0");
                            }
                            Serial.print(data[iii], HEX);
                            Serial.print("/");
//                            if ( data[iii+1] < 16 ) {
//                                Serial.print("0");
//                            }
                            Serial.print(data[iii+1]);
                            int pump;
                            if ( '1' == data[iii+1] ) {
                                pump = pumpMap1[data[iii]];
                            } else {
                                pump = pumpMap2[data[iii]];
                            }
                            Serial.print(" [");
                            Serial.print(pump, DEC);
                            Serial.print("] ");

                            analogWrite(TRANSISTOR_PIN, transistorValue);
                            delay(TONE_DELAY);
                            switchOkLed();
                        }
                        Serial.println(".");
                        analogWrite(TRANSISTOR_PIN, 0);
                        okLedOff();

                        message = MESSAGE_MISSION_COMPLETED;
                        currentCommand = COMMAND_NONE;
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
            if (DEBUG) Serial.println("~~~ PLAY ~~~");

        } else if (currentCommand == COMMAND_SIMULATE) {
            if (DEBUG) Serial.print("### SIMULATE ### ");
            if (DEBUG) Serial.println(transistorValue);

            analogWrite(TRANSISTOR_PIN, transistorValue);
        } else if (currentCommand == COMMAND_NONE) {
            if (TRACE) Serial.println("--- NONE ---");

            resetOutputs();
        }

        //
        // 3) WRITE MESSAGE BACK
        //
        if (transistorValue == 255) {
            message = MESSAGE_MISSION_COMPLETED;
        }
        if (message != MESSAGE_NONE) {
            Serial.print("> message: ");
            Serial.println(message);
        }
        if ( message == MESSAGE_MISSION_COMPLETED ) {
            msg[0] = message;
            msg[1] = 0;
            acc.write(msg, 2);
            message = MESSAGE_NONE;
        }

    } else {
        // reset outputs to default values on disconnect
        if (INFO) Serial.println("Phone NOT connected!!!");
        resetOutputs();

        //TODO nebo bychom tady mohli delat nejake paradicky - s vodou, se zvukem, s diodami
    }

    if (TRACE) Serial.print(currentCommand, DEC);
    if (TRACE) Serial.print(".");
    delay(10);
//    delay(50);
//    delay(200);
}

void resetOutputs() {
    transistorValue = 0;
    analogWrite(TRANSISTOR_PIN, 0);

}

//int printSome (char text[], unsigned long lastCall) {
//    unsigned long current = millis();
//
//    if ( (lastCall + 1000) < current ) {
//        Serial.println(text);
//        lastCall = current;
//    }
//    return lastCall;
//}
//
//int printSome (int value, unsigned long lastCall) {
//    unsigned long current = millis();
//
//    if ( (lastCall + 1000) < current ) {
//        Serial.println(value);
//        lastCall = current;
//    }
//    return lastCall;
//}

void play() {
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
    pumpMap1['|'] = PUMP_START_VALUE;
    pumpMap2['|'] = PUMP_START_VALUE;
    pumpMap1['c'] = 167;
    pumpMap2['c'] = 167;
    pumpMap1['C'] = 175;
    pumpMap2['C'] = 175;
    pumpMap1['d'] = 183;
    pumpMap2['d'] = 183;
    pumpMap1['D'] = 191;
    pumpMap2['D'] = 191;
    pumpMap1['e'] = 199;
    pumpMap2['e'] = 199;
    pumpMap1['f'] = 207;
    pumpMap2['f'] = 207;
    pumpMap1['F'] = 215;
    pumpMap2['F'] = 215;
    pumpMap1['g'] = 223;
    pumpMap2['g'] = 223;
    pumpMap1['G'] = 231;
    pumpMap2['G'] = 231;
    pumpMap1['a'] = 239;
    pumpMap2['a'] = 239;
    pumpMap1['b'] = 247;
    pumpMap2['b'] = 247;
    pumpMap1['h'] = 255;
    pumpMap2['h'] = 255;
}


void okLedOn()
{
    okLed = 1;

    //TODO Write do OK led pinu
    Serial.println("* [led] [OK] * ON *");
}

void switchOkLed()
{
    if ( okLed == 0 ) {
        okLedOn();
    } else {
        okLedOff();
    }
}

void okLedOff()
{
    okLed = 0;

    //TODO Write do OK led pinu
    Serial.println("* [led] [OK] - off -");
}

/*
Data packet error: 5
Device addressed... Requesting device descriptor.
Setup packet error: D
Device descriptor cannot be retrieved. Trying again
*/

