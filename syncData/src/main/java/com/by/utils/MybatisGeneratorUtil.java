package com.by.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.stereotype.Component;

/**
 * 执行完记得在bean添加注解@Component，不然启动失败
 * @author Administrator
 *
 */
public class MybatisGeneratorUtil {
	public static void main(String[] args) throws Exception {
		List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        
        File file = new File("");
//        String filePath = file.getCanonicalPath()+"\\src\\main\\resources\\generator\\generatorConfig.xml";
        String filePath = file.getCanonicalPath()+"\\src\\main\\resources\\generator\\generatorConfigSqlServer.xml";
        System.out.println(filePath);
        File configFile = new File(filePath);//路径自行选择
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);
	}
}
