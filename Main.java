package rle;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        compress("bmp.bmp", "compress.rle");
        decompress("compress.rle", "decompress2.bmp");

    }


    public static void compress(String source, String dis) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(dis)) {
            byte[] byteOfImage = in.readAllBytes();
            ByteArrayOutputStream rleByteList = new ByteArrayOutputStream();
            byte flag = -11;
            for (int i = 0; i < byteOfImage.length; i++) {
                int counter = 1;
                byte thisByte = byteOfImage[i];
                if (i == byteOfImage.length - 1) {
                    rleByteList.write(flag);
                    rleByteList.write((byte) 1);
                    rleByteList.write(thisByte);
                    break;
                }
                if (byteOfImage[i + 1] == thisByte) {
                    while ((i + 1 != byteOfImage.length) &&
                            (byteOfImage[i + 1] == thisByte)) {
                        counter++;
                        i++;
                    }
                    while (counter > 127) {
                        rleByteList.write((byte) 127);
                        rleByteList.write(thisByte);
                        counter -= 127;
                    }
                    if (counter > 0) {
                        rleByteList.write((byte) counter);
                        rleByteList.write(thisByte);
                    }
                } else {
                    int k = 0;
                    while ((i + k + 1 < byteOfImage.length) && (byteOfImage[i + k] != byteOfImage[i + k + 1])) {
                        k++;
                        if (i + k == byteOfImage.length - 1) {
                            if (byteOfImage[i + k] != byteOfImage[i + k - 1]) {
                                k++;
                            }
                        }
                    }
                    while (k > 127) {
                        rleByteList.write(flag);
                        rleByteList.write((byte) 127);
                        for (int j = 0; j < 127; j++) {
                            rleByteList.write(byteOfImage[i]);
                            i++;
                        }
                        k -= 127;
                    }
                    if (k > 0) {
                        rleByteList.write(flag);
                        rleByteList.write((byte) k);
                    }
                    while (k > 0) {
                        rleByteList.write(byteOfImage[i]);
                        k--;
                        i++;
                    }
                    i--;
                }
            }
            out.write(rleByteList.toByteArray());

        } catch (IOException e) {
        }
    }

    public static void decompress(String source, String dis) {

        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(dis)) {
            byte[] byteOfRle = in.readAllBytes();
            ByteArrayOutputStream originalByteList = new ByteArrayOutputStream();
            byte flag = -11;
            for (int i = 0; i < byteOfRle.length - 1; i++) {
                if (byteOfRle[i] == flag) {
                    int counter = byteOfRle[i + 1];
                    i += 2;
                    while (counter > 0) {
                        originalByteList.write(byteOfRle[i]);
                        i++;
                        counter--;
                    }
                    i--;
                } else {
                    int counter = byteOfRle[i];
                    while (counter > 0) {
                        originalByteList.write(byteOfRle[i + 1]);
                        counter--;
                    }
                    i++;
                }
            }
            out.write(originalByteList.toByteArray());
        } catch (IOException e) {
        }

    }
}

