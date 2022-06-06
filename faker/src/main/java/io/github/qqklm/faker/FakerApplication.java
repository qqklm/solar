package io.github.qqklm.faker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author wb
 * @date 2022/5/19 10:46
 */
@ServletComponentScan("io.github.qqklm")
@ComponentScan("io.github.qqklm")
@SpringBootApplication
public class FakerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FakerApplication.class);
    }
}
