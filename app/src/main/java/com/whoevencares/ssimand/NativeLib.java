package com.whoevencares.ssimand;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import kotlin.UByteArray;

public class NativeLib {
    public static native float newSsimBuilder(byte[] buf_a, byte[] buf_b);

    static {
        System.loadLibrary("ssim");
    }
}
