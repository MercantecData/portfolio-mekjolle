package com.example.app;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Dataconverter {

    public static void floatToMsg(float f, byte [] convert, int offset)
    {
        byte[] bOut = ByteBuffer.allocate(4).putFloat(f).array();

        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            convert[offset] = bOut[3];
            convert[offset + 1] = bOut[2];
            convert[offset + 2] = bOut[1];
            convert[offset + 3] = bOut[0];
        }
        else
        {
            convert[offset] = bOut[0];
            convert[offset + 1] = bOut[1];
            convert[offset + 2] = bOut[2];
            convert[offset + 3] = bOut[3];
        }

    }

    public static float msgToFloat(byte [] msg, int offset)
    {
        byte[] float_1 = new byte[4];
        ByteBuffer.wrap(msg, offset, 4).get( float_1);
        byte[] float_1_s = new byte[4];
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            float_1_s[0] = float_1[3];
            float_1_s[1] = float_1[2];
            float_1_s[2] = float_1[1];
            float_1_s[3] = float_1[0];
        }
        else
        {
            float_1_s[0] = float_1[0];
            float_1_s[1] = float_1[1];
            float_1_s[2] = float_1[2];
            float_1_s[3] = float_1[3];
        }

        float float_1_a = ByteBuffer.wrap(float_1_s).getFloat();
        return float_1_a;

    }
}

