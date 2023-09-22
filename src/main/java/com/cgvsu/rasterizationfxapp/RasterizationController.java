package com.cgvsu.rasterizationfxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;

import com.cgvsu.rasterization.*;
import javafx.scene.paint.Color;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    private Rasterization rasterization;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            updateRasterizationAndDraw();
        });

        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            updateRasterizationAndDraw();
        });

        updateRasterizationAndDraw();
    }

    private void updateRasterizationAndDraw() {
        int width = (int)canvas.getWidth();
        int height = (int)canvas.getHeight();
        int[] frameBuffer = new int[width * height];
        rasterization = new Rasterization(width, height, frameBuffer);

        float[][] vertices = {
                {100, 100},
                {300, 50},
                {200, 400}
        };
        int[] colors = {
                0xFF0000, // Red
                0x00FF00, // Green
                0x0000FF // Blue
        };

        rasterization.fillTriangle(vertices, colors);

        renderToGraphicsContext(canvas.getGraphicsContext2D(), frameBuffer, width, height);
    }

    private void renderToGraphicsContext(GraphicsContext graphicsContext, int[] frameBuffer, int width, int height) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int colorInt = frameBuffer[y * width + x];
                Color color = Color.rgb((colorInt >> 16) & 0xFF, (colorInt >> 8) & 0xFF, colorInt & 0xFF);
                pixelWriter.setColor(x, y, color);
            }
        }
    }
}