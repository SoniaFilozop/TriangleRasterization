package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {
    private final int screenWidth;
    private final int screenHeight;
    private final int[] frameBuffer;

    public Rasterization(int screenWidth, int screenHeight, int[] frameBuffer) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.frameBuffer = frameBuffer;
    }

    public void fillTriangle(float[][] vertices, int[] colors) {
        float minX = Math.min(Math.min(vertices[0][0], vertices[1][0]), vertices[2][0]);
        float maxX = Math.max(Math.max(vertices[0][0], vertices[1][0]), vertices[2][0]);
        float minY = Math.min(Math.min(vertices[0][1], vertices[1][1]), vertices[2][1]);
        float maxY = Math.max(Math.max(vertices[0][1], vertices[1][1]), vertices[2][1]);

        for (float y = minY; y <= maxY; y++) {
            for (float x = minX; x <= maxX; x++) {
                float[] bCoords = barycentricCoords(vertices, x, y);

                if (isInsideTriangle(bCoords)) {
                    int color = interpolateColor(colors, bCoords);
                    setPixel((int)x, (int)y, color);
                }
            }
        }
    }

    private float[] barycentricCoords(float[][] vertices, float x, float y) {
        float[] bCoords = new float[3];

        float area = 0.5f * (-vertices[1][1] * vertices[2][0] + vertices[0][1] * (-vertices[1][0] + vertices[2][0]) + vertices[0][0] * (vertices[1][1] - vertices[2][1]) + vertices[1][0] * vertices[2][1]);
        bCoords[0] = 1.0f / (2.0f * area) * (vertices[0][1] * vertices[2][0] - vertices[0][0] * vertices[2][1] + (vertices[2][1] - vertices[0][1]) * x + (vertices[0][0] - vertices[2][0]) * y);
        bCoords[1] = 1.0f / (2.0f * area) * (vertices[0][1] * vertices[1][0] - vertices[0][0] * vertices[1][1] + (vertices[1][1] - vertices[0][1]) * x + (vertices[0][0] - vertices[1][0]) * y);
        bCoords[2] = 1.0f - bCoords[0] - bCoords[1];

        return bCoords;
    }

    private boolean isInsideTriangle(float[] bCoords) {
        return bCoords[0] >= 0 && bCoords[1] >= 0 && bCoords[2] >= 0;
    }

    private int interpolateColor(int[] colors, float[] bCoords) {
        int r = (int)((colors[0] >> 16 & 0xFF) * bCoords[0] + (colors[1] >> 16 & 0xFF) * bCoords[1] + (colors[2] >> 16 & 0xFF) * bCoords[2]);
        int g = (int)((colors[0] >> 8 & 0xFF) * bCoords[0] + (colors[1] >> 8 & 0xFF) * bCoords[1] + (colors[2] >> 8 & 0xFF) * bCoords[2]);
        int b = (int)((colors[0] & 0xFF) * bCoords[0] + (colors[1] & 0xFF) * bCoords[1] + (colors[2] & 0xFF) * bCoords[2]);

        return (r << 16) | (g << 8) | b;
    }


    private void setPixel(int x, int y, int color) {
        if (x >= 0 && x < screenWidth && y >= 0 && y < screenHeight) {
            frameBuffer[y * screenWidth + x] = color;
        }
    }
}
