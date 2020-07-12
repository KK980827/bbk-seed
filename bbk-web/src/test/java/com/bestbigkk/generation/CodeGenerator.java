package com.bestbigkk.generation;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.Test;

import java.util.List;

import java.io.File;
import java.util.LinkedList;

public class CodeGenerator {

    public static final String AUTHOR = "xugongkai";

    private static final String TEMPLATE_DIR = "/templates";
    private static final String OUT_PUT_DIR = "/src/test/java/dist";
    private static final String XML_DIR = "/src/test/java/dist/mapper";
    private static final String BASE_PACKAGE = "com.bestbigkk";

    private static final String DB_URL = "jdbc:mysql://cdb-41wrstjc.bj.tencentcdb.com:10187/xugongkai_test?useSSL=true&autoReconnect=true&serverTimezone=Asia/Shanghai";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_USER_NAME = "bestbigkk";
    private static final String DB_PASSWORD = "KK131421!!!";

    private static final String DB_SCHEMA_NAME = "sp_business";
    private static final String[] TABLE_PREFIX = {"t_"};
    private static final String[] TABLE_NAMES = {"t_user"};

    @Test
    public void main() {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + OUT_PUT_DIR);
        gc.setAuthor(AUTHOR);
        gc.setOpen(false);
        //开启Swagger注释
        gc.setSwagger2(true);
        //是否覆盖文件
        gc.setFileOverride(true);
        //自定义文件名
        gc.setMapperName("%sDao");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        ///gc.setEntityName("%sPO"); //实体名称，%s为占位符， 2020.04.26:目前已经集成了字段控制，不用再刻意的区分出PO, BO, DTO等领域对象
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setUrl(DB_URL);
        dsc.setTypeConvert(new MySqlTypeConvert());
        dsc.setDriverName(DB_DRIVER);
        dsc.setUsername(DB_USER_NAME);
        dsc.setPassword(DB_PASSWORD);
        if (DB_DRIVER.contains("postgresql")) {
            dsc.setSchemaName(DB_SCHEMA_NAME);
        }
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(BASE_PACKAGE);
        pc.setController("web.controller");
        pc.setEntity("persistence.entity");
        pc.setMapper("persistence.dao");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() { }
        };

        // 如果模板引擎是 freemarker
        String templatePath = TEMPLATE_DIR + "/mapper.xml.ftl";

        // 自定义输出配置
        List<FileOutConfig> focList = new LinkedList<>();
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + XML_DIR + File.separator  + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setController(TEMPLATE_DIR + "/controller.java.k"); //指定Controller的模板
        templateConfig.setEntity(TEMPLATE_DIR + "/entity.java.k"); //指定Entity模板
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setTablePrefix(TABLE_PREFIX);
        strategy.setInclude(TABLE_NAMES);
        strategy.setControllerMappingHyphenStyle(true);

        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();

    }

}