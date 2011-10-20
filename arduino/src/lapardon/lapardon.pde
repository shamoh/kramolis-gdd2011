#include <Usb.h>
#include <AndroidAccessory.h>
#include "pitches.h"


#define COMMAND_NONE      0
#define COMMAND_PLAY      1
#define COMMAND_SIMULATE  2

#define MESSAGE_NONE      0
#define MESSAGE_ERROR     1
#define MESSAGE_KNOCK     2
#define MESSAGE_MIC       3
#define MESSAGE_MISSION_COMPLETED  4
#define MESSAGE_PONG      5

#define TONE_DELAY        700

#define PUMP_START_VALUE  222
#define PUMP_START_DELAY  1000

#define KNOCK_THRESHOLD   300

// connected to the base of the transistor
#define TRANSISTOR_PIN 12
#define OK_LED_PIN     13
#define ERROR_LED_PIN  2
#define KNOCK_PIN      A15
#define TONE_PIN       8

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
int  toneMap1[255];

byte currentCommand = COMMAND_NONE;
byte message = MESSAGE_NONE;

int transistorValue = 0; // value written to the transistor : 0-255
//long iterace = 0;
//unsigned long statusTime = 0;
int okLed = 0;
int errorLed = 1;

int knockReading = 0;

void setup()
{
    Serial.begin(115200);
    Serial.println("\r\n***** Start *****\n*****************\n");

    initPumpMaps();
    initToneMaps();

    // set  the transistor pin as output:
    pinMode(TRANSISTOR_PIN, OUTPUT);
    // led
    pinMode(OK_LED_PIN, OUTPUT);
    pinMode(ERROR_LED_PIN, OUTPUT);

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
        okLedOn();
        errorLedOff();

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

                        // prepare watter
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
                            // pong message
                            msg[0] = MESSAGE_PONG;
                            msg[1] = iii;
                            acc.write(msg, 2);
                            // do the business
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

                            analogWrite(TRANSISTOR_PIN, pump);
                            if ( toneMap1[data[iii]] > 0 ) {
                                tone(TONE_PIN, toneMap1[data[iii]]);
                            } else {
                                noTone(TONE_PIN);
                            }

                            delay(TONE_DELAY);
                            switchOkLed();
                        }
                        analogWrite(TRANSISTOR_PIN, 0);
                        noTone(TONE_PIN);
                        okLedOff();

                        Serial.println(".");

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
//if (DEBUG) Serial.print(".............. ");
//if (DEBUG) Serial.println(transistorValue);
//if (DEBUG) Serial.print("message: ");
//if (DEBUG) Serial.println(message, DEC);

        //
        // 3) WRITE MESSAGE BACK
        //
        /*
        if (transistorValue == 255) {
            message = MESSAGE_KNOCK;
            currentCommand = COMMAND_NONE;
        }
        */
        if (message != MESSAGE_NONE) {
            if (TRACE) Serial.print("> message: ");
            if (TRACE) Serial.println(message, DEC);
        }
        if ( message == MESSAGE_MISSION_COMPLETED ) {
            msg[0] = message;
            msg[1] = 0;
            acc.write(msg, 2);
            message = MESSAGE_NONE;
        } else {
            //
            // 3) READ SENSORS
            //
            {
                knockReading = analogRead(KNOCK_PIN);
                if (knockReading >= KNOCK_THRESHOLD) {
                    if (DEBUG) Serial.print("=== KNOCK === ");
                    if (DEBUG) Serial.println(knockReading);
                    message = MESSAGE_KNOCK;
                }
            }

            if ( message == MESSAGE_KNOCK ) {
                switchOkLed(8, 80);

                if (TRACE) Serial.println("=== BEFORE ===");

                msg[0] = message;
                msg[1] = knockReading/4;
                acc.write(msg, 2);
                message = MESSAGE_NONE;

                if (TRACE) Serial.println("=== AFTER ===");

                switchOkLed(8, 80);
            }
        }
    } else {
        okLedOff();
        errorLedOn();

        // reset outputs to default values on disconnect
//        if (DEBUG) Serial.println("Phone NOT connected!!!");
        if (DEBUG) Serial.print(".");
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

//
// PumpMaps
//

void initPumpMaps()
{
    for(int iii = 0; iii < 255; iii++) {
        pumpMap1[iii] = 0;
        pumpMap2[iii] = 0;
    }
    //supported chars: cCdDefFgGabh
    //rest char: |
    pumpMap1['|'] = PUMP_START_VALUE;
    pumpMap2['|'] = PUMP_START_VALUE;
    pumpMap1['c'] = 156;
    pumpMap2['c'] = 156;
    pumpMap1['C'] = 165;
    pumpMap2['C'] = 165;
    pumpMap1['d'] = 174;
    pumpMap2['d'] = 174;
    pumpMap1['D'] = 183;
    pumpMap2['D'] = 183;
    pumpMap1['e'] = 192;
    pumpMap2['e'] = 192;
    pumpMap1['f'] = 201;
    pumpMap2['f'] = 201;
    pumpMap1['F'] = 210;
    pumpMap2['F'] = 210;
    pumpMap1['g'] = 219;
    pumpMap2['g'] = 219;
    pumpMap1['G'] = 228;
    pumpMap2['G'] = 228;
    pumpMap1['a'] = 237;
    pumpMap2['a'] = 237;
    pumpMap1['b'] = 246;
    pumpMap2['b'] = 246;
    pumpMap1['h'] = 255;
    pumpMap2['h'] = 255;
}

//
// ToneMaps
//

void initToneMaps()
{
    for(int iii = 0; iii < 255; iii++) {
        toneMap1[iii] = 0;
    }
    //supported chars: cCdDefFgGabh
    //rest char: |
    toneMap1['|'] = 0;
    toneMap1['c'] = NOTE_C3;
    toneMap1['C'] = NOTE_CS3;
    toneMap1['d'] = NOTE_D3;
    toneMap1['D'] = NOTE_DS3;
    toneMap1['e'] = NOTE_E3;
    toneMap1['f'] = NOTE_F3;
    toneMap1['F'] = NOTE_FS3;
    toneMap1['g'] = NOTE_G3;
    toneMap1['G'] = NOTE_GS3;
    toneMap1['a'] = NOTE_A3;
    toneMap1['b'] = NOTE_AS3;
    toneMap1['h'] = NOTE_B3;
}


void okLedOn()
{
    okLed = 1;

    digitalWrite(OK_LED_PIN, HIGH);

    if (TRACE) Serial.println("* [led] [OK] * ON *");
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

    digitalWrite(OK_LED_PIN, LOW);

    if (TRACE) Serial.println("* [led] [OK] - off -");
}

void switchOkLed(int times, int millis)
{
    for(int iii = 0; iii < times; iii++) {
        switchOkLed();
        delay(millis);
    }
}

void errorLedOn()
{
    errorLed = 1;

    digitalWrite(ERROR_LED_PIN, HIGH);

    if (TRACE) Serial.println("* [led] [ERROR] * ON *");
}

void switchErrorLed()
{
    if ( errorLed == 0 ) {
        errorLedOn();
    } else {
        errorLedOff();
    }
}

void errorLedOff()
{
    errorLed = 0;

    digitalWrite(ERROR_LED_PIN, LOW);

    if (TRACE) Serial.println("* [led] [ERROR] - off -");
}

/*
Data packet error: 5
Device addressed... Requesting device descriptor.
Setup packet error: D
Device descriptor cannot be retrieved. Trying again
*/

