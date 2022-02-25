package com.hao.mana;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2  //!!!!!!!!
@MapperScan("com.hao.mana.mapper")
@ComponentScan(basePackages = {"com.hao"})
@EnableTransactionManagement
//@EnableCaching
public class managementApplication {
    public static void main(String[] args) {
        SpringApplication.run(managementApplication.class,args);
    }
}
