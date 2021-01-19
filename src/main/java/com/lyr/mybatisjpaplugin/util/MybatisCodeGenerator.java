package com.lyr.mybatisjpaplugin.util;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 继承这个类，返回 xml配置文件接口，可以直接生成代码
 */
public abstract class MybatisCodeGenerator {
    public abstract String getAbsolutePathGeneratorConfig_xml(/*String generatorConfig_xml*/);

    public void xml_make() {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //如果这里出现空指针，直接写绝对路径即可。
        // String genCfg = "/generatorConfig.xml";
        File configFile = new File(/*"D:\\ASUS\\Desktop\\个人项目\\测试demo\\src\\main\\resources\\generatorConfig.xml"*/getAbsolutePathGeneratorConfig_xml());
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = null;
        try {
            config = cp.parseConfiguration(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = null;
        try {
            myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            myBatisGenerator.generate(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
