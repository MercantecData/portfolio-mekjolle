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

    public static void intToMsg (int i, byte [] convert, int offset)
    {
        byte[] intOut = ByteBuffer.allocate(4).putInt(i).array();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            convert[offset] = intOut[3];
            convert[offset + 1] = intOut[2];
            convert[offset + 2] = intOut[1];
            convert[offset + 3] = intOut[0];
        }
        else
        {
            convert[offset] = intOut[0];
            convert[offset + 1] = intOut[1];
            convert[offset + 2] = intOut[2];
            convert[offset + 3] = intOut[3];
        }

    }

    public static int msgToInt(byte [] msg, int offset)
    {
        byte[] int_1 = new byte[4];
        ByteBuffer.wrap(msg, offset, 4).get( int_1); //size of int
        byte[] int_1_s = new byte[4];
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            int_1_s[0] = int_1 [3];
            int_1_s[1] = int_1 [2];
            int_1_s[2] = int_1 [1];
            int_1_s[3] = int_1 [0];
        }
        else
        {
            int_1_s[0] = int_1 [0];
            int_1_s[1] = int_1 [1];
            int_1_s[2] = int_1 [2];
            int_1_s[3] = int_1 [3];
        }
        int int_1_a = ByteBuffer.wrap(int_1_s).getInt();
        return int_1_a;
    }

    public static void byteToMsg (byte b, byte [] convert, int offset) {
        byte[] intOut = ByteBuffer.allocate(1).put(b).array();
        convert[offset] = intOut[0];
    }

}

