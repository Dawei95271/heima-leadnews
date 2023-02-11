package com.heima.minIO;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 14:19
 */

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;


    @Test
    public void testStarter(){

        try {
            for (int i = 1; i < 9; i++) {
                InputStream is = new FileInputStream("C:\\Users\\16420\\Pictures\\Saved Pictures\\" + i + ".jpg");
                String filename = UUID.randomUUID().toString().replace("-", "") + ".jpg";
                String path = fileStorageService.uploadImgFile("", filename, is);
                System.out.println(path);
            }
//            InputStream is = new FileInputStream("C:\\Users\\16420\\Pictures\\Saved Pictures\\1.jpg");
//            String path = fileStorageService.uploadImgFile("", "test.jpg", is);
//            System.out.println(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test01() {

        try {
            InputStream is = new FileInputStream("d:/tmp/plugins/js/index.js");

            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/index.js")
                    .contentType("text/css")
                    .bucket("leadnews")
                    .stream(is, is.available(), -1)
                    .build();

            minioClient.putObject(putObjectArgs);

//            System.out.println("http://192.168.200.130:9000/leadnews/list.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
