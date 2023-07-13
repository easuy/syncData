package com.by;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.by.utils.SpringContextUtil;


@EnableTransactionManagement //开启事务管理
@SpringBootApplication
@MapperScan("com.by.generator.*")
@CrossOrigin
@EnableScheduling
@EnableAsync
public class SyncDataApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SyncDataApplication.class, args);
		SpringContextUtil.setApplicationContext(context);
	}

}
