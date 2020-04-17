import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.nio.Buffer;
import java.util.*;

import static java.lang.Math.cos;

public class ImageProcessor {
    // The BufferedImage class describes an Image with an accessible buffer of image data
    public static BufferedImage convert(Image img) {
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(img, 0, 0, null);
        bg.dispose();
        return bi;
    }

    // A method to clone a BufferedImage
    public static BufferedImage cloneImage(BufferedImage img) {
        BufferedImage resultImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableRaster WR1 = Raster.createWritableRaster(img.getSampleModel(), null);
        WritableRaster WR2 = img.copyData(WR1);
        resultImg.setData(WR2);
        return resultImg;
    }

    // Assignment A-1: Implement Translation
    public static BufferedImage translate(BufferedImage bi, int xDir, int yDir) {
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        // loop through every RESULTING IMAGE's pixel (point)
        for (int i = 0; i < bi.getWidth(); i++){
            for (int j = 0; j < bi.getHeight(); j++){
                // Hint: Translating input image == Translating resulting image's index (x & y) "reversely"
                // those pixels out of bounds should be in white color
                int xOld = i - xDir;
                int yOld = j - yDir;

                if (xOld < 0 || yOld < 0){
                    Color white = new Color(255,255,255);
                    result.setRGB(i, j, white.getRGB());
                }

                // set the color of the resulting image's corresponding pixel
                else {
                    Color transitionColor = new Color(bi.getRGB(xOld, yOld));
                    result.setRGB(i, j, transitionColor.getRGB());
                }
            }
        }
        // change null to the resulting bufferedImage you obtained
        return result;
    }


    // Assignment A-2: Implement Scaling with respect to image's ORIGIN
    public static BufferedImage scale(BufferedImage bi, float xDirScale, float yDirScale) {
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        // loop through every RESULTING IMAGE's pixel (point)
        for (int i = 0; i < bi.getWidth(); i++){
            for (int j = 0; j < bi.getHeight(); j++){
                // Hint: Scaling input image == Scaling resulting image's index (x & y) "reversely"
                // those pixels out of bounds should be in white color
                double xOldFloat = i / xDirScale;
                double yOldFloat = j / yDirScale;

                int xOld = (int)xOldFloat;
                int yOld = (int)yOldFloat;

                if (xOld < 0 || yOld < 0){
                    Color white = new Color (255,255,255);
                    result.setRGB(i, j, white.getRGB());
                }

                // set the color of the resulting image's corresponding pixel
                else {
                    Color scalingColor = new Color(bi.getRGB(xOld, yOld));
                    result.setRGB(i, j, scalingColor.getRGB());
                }

            }
        }
        // change null to the resulting bufferedImage you obtained
        return result;
    }


    // Assignment A-3: Implement Rotation (Counter-Clockwise) Around Image's CENTER POINT
    public static BufferedImage rotate(BufferedImage bi, float angle) {
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        // calculate image's center point coordinate (float)
        // please double check that your calculated center is correct
        // e.g. half of 7 is 3.5, not 3.0
        double xCenter =  bi.getWidth() / 2.0;
        double yCenter = bi.getHeight() / 2.0;

        // loop through every RESULTING IMAGE's pixel (point)
        for (int i = 0; i < bi.getWidth(); i++){
            for (int j = 0; j < bi.getHeight(); j++){
                // Rotating input image counter-clockwise == Rotating resulting image's index clockwise
                // 1. translate the point (x, y) with respect to the CENTER POINT
                // (float) x' = x - centerX
                // (float) y' = y - centerY
                double xA = i - xCenter;
                double yA = j - yCenter;

                // 2. rotate the point (x', y') using a formula (angle is in radians)
                // (float) x'' = x' * cos(angle) - y' * sin(angle)
                // (float) y'' = x' * sin(angle) + y' * cos(angle)
                double xB = xA * Math.cos(angle) - yA * Math.sin(angle);
                double yB = xA * Math.sin(angle) + yA * Math.cos(angle);

                // 3. translate the point (x'', y'') back with respect to origin
                // (float) x''' = x'' + centerX
                // (float) y''' = y'' + centerY
                double xC = xB + xCenter;
                double yC = yB + yCenter;

                // 4. cast the floats to integers so that they can be used as indices to obtain color
                int x = (int)xC;
                int y = (int)yC;

                // those pixels out of bounds should be in white color
                if (x < 0 || y < 0 || x >= bi.getWidth() || y >= bi.getHeight()){
                    Color white = new Color(255,255,255);
                    result.setRGB(i, j, white.getRGB());
                }

                // set the color of the resulting image's corresponding pixel
                else {
                    Color rotationColor = new Color(bi.getRGB(x, y));
                    result.setRGB(i, j, rotationColor.getRGB());
                }
            }
        }
        // change null to the resulting bufferedImage you obtained
        return result;
    }


    // Assignment B-1: Implement Blending Two Images with a Blending Weight
    public static BufferedImage blend(BufferedImage bi1, BufferedImage bi2, float image1Weight) {
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_INT_RGB);

        // loop through every RESULTING IMAGE's pixel (point)
        // you may assume that the two images always have the same width and height
        for (int i = 0; i < bi1.getWidth(); i++){
            for (int j = 0; j < bi1.getHeight(); j++){
                // 1. use a formula to obtain the blended color:
                // (float) newR = r1 * weight1 + r2 * (1-weight1), same with G and B
                Color color1 = new Color(bi1.getRGB(i, j));
                Color color2 = new Color(bi2.getRGB(i, j));
                double blendRed = color1.getRed() * image1Weight + color2.getRed() * (1 - image1Weight);
                double blendGreen = color1.getGreen() * image1Weight + color2.getGreen() * (1 - image1Weight);
                double blendBlue = color1.getBlue() * image1Weight + color2.getBlue() * (1 - image1Weight);

                // 2. the obtained rgb should be casted to integers so as to create a color
                int blendedRed = (int) blendRed;
                int blendedGreen = (int) blendGreen;
                int blendedBlue = (int) blendBlue;

                // set the color of the resulting image's corresponding pixel
                Color blendColor = new Color(blendedRed, blendedGreen, blendedBlue);
                result.setRGB(i, j, blendColor.getRGB());
            }
        }
        // change null to the resulting bufferedImage you obtained
        return result;
    }


    // Assignment B - Sample Code
    public static BufferedImage invert(BufferedImage bi) {
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        // loop through every RESULTING IMAGE's pixel (point)
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                // for each pixel, obtain its previous RGB values
                // its new RGB values are:
                // newR = 255 - oldR, same with R and B
                Color color = new Color(bi.getRGB(x, y));
                int ir = 255 - color.getRed();
                int ig = 255 - color.getGreen();
                int ib = 255 - color.getBlue();
                Color inversedColor = new Color(ir, ig, ib);
                // set the color of the resulting image's corresponding pixel
                result.setRGB(x, y, inversedColor.getRGB());
            }
        }
        return result;
    }


    // Assignment B-2: Implement Image Blurring Effect
    public static BufferedImage blur(BufferedImage bi, int blurRadius) {
        // Please visit the assignment page for more detailed explanations
        // create a new buffered image to be output as the result
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        // loop through every RESULTING IMAGE's pixel (point)
        for (int i = 0; i < bi.getWidth(); i++){
            for (int j = 0; j < bi.getHeight(); j++){
                int firstRow = i - blurRadius;
                int firstColumn = j - blurRadius;
                int lastRow = i + blurRadius;
                int lastColumn = j + blurRadius;
                int totalPixel = (2*blurRadius + 1)*(2*blurRadius + 1);

                int sumOfRed = 0;
                int sumOfGreen = 0;
                int sumOfBlue = 0;

                // for those pixels having not enough neighbors, their new colors should be the same as their old colors
                if (firstRow < 0 || firstColumn < 0 || lastColumn >= bi.getHeight() || lastRow >= bi.getWidth()){
                    result.setRGB(i, j, bi.getRGB(i, j));
                }

                // for those pixels having enough neighbors, for each pixel compute its R, G, B average values among its neighbors
                else{
                    for (int a = firstRow; a <= lastRow; a++) {
                        for (int b = firstColumn; b <= lastColumn; b++) {
                            Color color = new Color(bi.getRGB(a, b));
                            sumOfRed += color.getRed();
                            sumOfGreen += color.getGreen();
                            sumOfBlue += color.getBlue();
                        }
                    }

                    int averageRed = sumOfRed / totalPixel;
                    int averageGreen = sumOfGreen / totalPixel;
                    int averageBlue = sumOfBlue / totalPixel;

                    // set the color of the resulting image's corresponding pixel
                    Color blurredColor = new Color(averageRed, averageGreen, averageBlue);
                    result.setRGB(i, j, blurredColor.getRGB());
                }
            }
        }
        // change null to the resulting bufferedImage you obtained
        return result;
    }
}
