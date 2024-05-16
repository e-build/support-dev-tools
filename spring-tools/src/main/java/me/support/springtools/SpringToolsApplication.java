package me.support.springtools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class SpringToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringToolsApplication.class, args);
    }

}
