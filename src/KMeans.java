/*** Author :Vibhav Gogate
 The University of Texas at Dallas
 *****/


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;


public class KMeans {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try {
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k = Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k) {

        int originalSize = rgb.length * 24;
        ArrayList<Integer> centroids = new ArrayList<>();

        ArrayList<Integer>[] indexArray = new ArrayList[k];
        ArrayList<Integer>[] dataArray = new ArrayList[k];
        int iterations = 1;
        while (iterations < 50) {
            findCentroids(dataArray, indexArray, centroids, k, rgb);
            ArrayList<Integer> distances = new ArrayList<>();
            for (int i = 0; i < rgb.length; i++) {
                distances.clear();
                for (int j = 0; j < k; j++) {
                    distances.add(Math.abs(centroids.get(j) - rgb[i]));
                }
                int cluster = distances.indexOf(Collections.min(distances));
                indexArray[cluster].add(i);
                dataArray[cluster].add(rgb[i]);
            }
            iterations++;
        }

        // set pixel values
        for (int i = 0; i < k; i++) {
            int val = centroids.get(i);
            for (int idx : indexArray[i]) {
                rgb[idx] = val;
            }
        }
        System.out.println("k = " + k + " Compression Ratio: " + originalSize / ((24 * k) + (Math.log10(k) / Math.log10(2)) * rgb.length));
    }

    private static void findCentroids(ArrayList<Integer>[] dataArray, ArrayList<Integer>[] indexArray, ArrayList<Integer> centroids, int k, int[] rgb) {
        if (centroids.size() == 0)
            for (int i = 0; i < k; i++) {
                centroids.add(rgb[(int) (Math.random() * rgb.length)]);
                indexArray[i] = new ArrayList<>();
                dataArray[i] = new ArrayList<>();
            }
        else {
            for (int i = 0; i < k; i++) {
                Collections.sort(dataArray[i]);
                centroids.set(i, mean(dataArray[i]));
                indexArray[i] = new ArrayList<>();
                dataArray[i] = new ArrayList<>();
            }
        }
    }

    private static int mean(ArrayList<Integer> integers) {
        int avg = (integers.get(0) + integers.get(integers.size() - 1)) / 2;

        int low = 0, high = integers.size() - 1, mid = (high + low) / 2;
        while (low <= high) {
            mid = (high + low) / 2;
            int val = integers.get(mid);
            if (avg < val) {
                high = mid - 1;
            } else if (avg > val) {
                low = mid + 1;
            } else {
                break;
            }
        }
        return integers.get(mid);
    }
}
