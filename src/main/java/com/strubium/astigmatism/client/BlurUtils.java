package com.strubium.astigmatism.client;

import net.minecraft.client.texture.NativeImage;
import java.util.concurrent.*;

public class BlurUtils {

    // Method to apply a simple box blur to an image
    public static void applyWeightedBoxBlur(NativeImage image, float radius) {
        int width = image.getWidth();
        int height = image.getHeight();
        int intRadius = (int)Math.ceil(radius);
        float radiusSq = radius * radius;

        if (radius <= 0) return;

        NativeImage copy = new NativeImage(width, height, false);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(height);

        for (int y = 0; y < height; y++) {
            final int row = y;
            executor.submit(() -> {
                for (int x = 0; x < width; x++) {
                    float accR = 0, accG = 0, accB = 0, accA = 0;
                    float weightSum = 0;

                    for (int dx = -intRadius; dx <= intRadius; dx++) {
                        for (int dy = -intRadius; dy <= intRadius; dy++) {
                            int nx = x + dx;
                            int ny = row + dy;

                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                float distSq = dx * dx + dy * dy;
                                if (distSq > radiusSq) continue;

                                float weight = (float)Math.exp(-distSq / (2 * radiusSq));
                                int color = image.getColor(nx, ny);

                                float alpha = NativeImage.getAlpha(color) / 255f;
                                float red = NativeImage.getRed(color) / 255f;
                                float green = NativeImage.getGreen(color) / 255f;
                                float blue = NativeImage.getBlue(color) / 255f;

                                accR += red * alpha * weight;
                                accG += green * alpha * weight;
                                accB += blue * alpha * weight;
                                accA += alpha * weight;

                                weightSum += weight;
                            }
                        }
                    }

                    if (weightSum > 0) {
                        float finalAlpha = accA / weightSum;
                        float finalR = finalAlpha > 0 ? accR / accA : 0;
                        float finalG = finalAlpha > 0 ? accG / accA : 0;
                        float finalB = finalAlpha > 0 ? accB / accA : 0;

                        int outA = Math.round(finalAlpha * 255f);
                        int outR = Math.round(finalR * 255f);
                        int outG = Math.round(finalG * 255f);
                        int outB = Math.round(finalB * 255f);

                        int outColor = (outA << 24) | (outB << 16) | (outG << 8) | outR;
                        copy.setColor(x, row, outColor);
                    }
                }
                latch.countDown();
            });
        }

        try {
            latch.await(); // Wait for all threads
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        // Write final blurred pixels back
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setColor(x, y, copy.getColor(x, y));
            }
        }

        copy.close();
    }

}
