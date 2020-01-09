package com.hrm.syytem;

import com.hrm.common.utils.IdWorker;
import com.hrm.common.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

//1.配置springboot的包扫描
@SpringBootApplication(scanBasePackages = "com.hrm")
//2.配置jpa注解的扫描
@EntityScan(value="com.hrm.domain.system")
public class SystemApplication {
    /**
     * 启动方法
     */
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil();
    }
}
