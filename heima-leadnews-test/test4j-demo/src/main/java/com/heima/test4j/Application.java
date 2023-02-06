package com.heima.test4j;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/3 16:20
 */
public class Application {


    /**
     * 识别图片中的文字
     * @param args
     */

    public static void main(String[] args) throws TesseractException {

        Tesseract tesseract = new Tesseract();

        tesseract.setDatapath("C:\\Users\\16420\\Documents\\learnCode\\toutiao\\tessdata");

        tesseract.setLanguage("chi_sim");

        File file = new File("C:\\Users\\16420\\Pictures\\Saved Pictures\\143.jpg");

        String result = tesseract.doOCR(file);

        System.out.println("识别结果：" + result);

    }
}
