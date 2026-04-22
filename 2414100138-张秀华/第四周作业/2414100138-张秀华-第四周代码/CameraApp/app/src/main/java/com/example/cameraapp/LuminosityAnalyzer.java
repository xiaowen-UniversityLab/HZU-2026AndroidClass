package com.example.cameraapp;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

public class LuminosityAnalyzer implements ImageAnalysis.Analyzer {

    public interface LumaListener{
        void onLumaAvailable(double luma);
    }

    private final LumaListener listener;

    public LuminosityAnalyzer (LumaListener listener){
        this.listener=listener;
    }

    private byte[] byteBufferToByteArray(ByteBuffer buffer) {
        buffer.rewind();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = byteBufferToByteArray(buffer);

        double luma = 0;
        for (byte b : data) {
            luma += b & 0xFF;
        }
        luma = luma / data.length;

        listener.onLumaAvailable(luma);

        image.close();
    }
}
