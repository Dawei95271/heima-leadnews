package com.heima.freemaker.controller;

import com.heima.freemaker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/10 13:14
 */

@Controller
public class HelloController {

    @GetMapping("basic")
    public String test(Model model){

        // name
        model.addAttribute("name", "freemaker");
        // stu
        Student stu = new Student();
        stu.setName("tom");
        stu.setAge(21);
        stu.setMoney(100.0f);
        stu.setBirthday(new Date());
        model.addAttribute("stu", stu);
        // 返回视图
        return "01-basic";


    }

}
