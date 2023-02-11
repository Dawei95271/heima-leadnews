package com.heima.material.test;

import com.heima.wemedia.WemediaApplication;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 8:56
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class MpTest {

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Test
    public void testDel() {
        System.out.println(wmSensitiveMapper.deleteById(3201));
    }
}
