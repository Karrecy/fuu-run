package com.karrecy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuuAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuuAdminApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Fuu-Run启动成功   ლ(´ڡ`ლ)ﾞ");

    }

}
