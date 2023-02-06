package com.heima.freemarker;

import com.heima.freemaker.FreeMarkerDemoApplication;
import com.heima.freemaker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 13:50
 */

@SpringBootTest(classes = FreeMarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class FreeMarkerTest {


    @Autowired
    private Configuration configuration;

    @Test
    public void test01() throws IOException, TemplateException {
        Template template = configuration.getTemplate("01-basic.ftl");
        template.process(getData(), new FileWriter("d:/list.html"));

    }

    public Map getData(){
        HashMap<String, Object> map = new HashMap<>();
        // name
        map.put("name", "freemarker");

        // stu
        Student stu = new Student();
        stu.setName("tom");
        stu.setAge(21);
        stu.setMoney(100.0f);
        stu.setBirthday(new Date());
        map.put("stu", stu);

        return  map;
    }
}
