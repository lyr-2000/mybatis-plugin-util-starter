# 工程简介



# 延伸阅读


```java
@SpringBootApplication
@EnableMybatisXssPlugin
@EnableMybatisOptimisticPlugin
@EnableFieldFillMybatisPlugin
public class DemoApplication {


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}


```
```xml

<dependency>
            <groupId>com.github.lyr-2000</groupId>
            <artifactId>mybatis-plugin-util-starter</artifactId>
            <version>1.3</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api -->
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>javax.persistence-api</artifactId>
            <version>2.2</version>
        </dependency>
```


maven仓库
## 1. 乐观锁插件
注解部分是使用 JPA 的， 感觉用一个统一的接口会比较方便管理
比如乐观锁 ，加个 @Version 注解
注意，必须是动态 sql 才会生效，在更新的时候 自动 set version = originValue +1 where xx and  version = originValue
如果 数据库version字段 和 属性字段不一样，要设置 @Column 注解，不然默认就是 属性作为字段

这个功能有点鸡肋，主要是看 mybatis plus 有这个功能，自己也简单实现一下





## 2. 更新时间和插入时间自动填充


```java

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Advertisement implements Serializable {
    /**
     * 广告id
     */
    private Integer advertisementId;

    private String companyName;

    private String phone;

    private String banner;

    /**
     * 广告投放地区
     */
    private Integer advertisementPutting;

    @CreateTime
    private Date createTime;

    @UpdateTime
    private Date updateTime;

    private Date beginTime;

    private Date endTime;

    /**
     * 逻辑删除状态
     */
    @JsonIgnore
    private Integer deleteStatus;

    private String createBy;

    private String updateBy;

    /**
     * 审核状态 -1 不通过，0待审核，1通过
     */
    private Integer permitStatus;

    private String contactPersonName;

    private static final long serialVersionUID = 1L;
}
```

这个也必须是动态 sql ，并且 mapper 方法的参数只有一个，并且是 pojo 的时候，才会反射扫描类注解信息

判断是 insert还是 update ，自动对 时间进行填充 ，不需要数据库那里设置默认值




##  3. 防止XSS注入

```java

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Advertisement implements Serializable {
    /**
     * 广告id
     */
    private Integer advertisementId;

    private String companyName;
    @UnEscape
    private String phone;

```

开启防止XSS 注入插件后，自动将 String 全部转义，除非 加上 @UnEscape 注解




##  4. 代码生成器

```java

@Test
    void xml_make2() {
        new MybatisCodeGenerator(){
            @Override
            public String getAbsolutePathGeneratorConfig_xml() {
                return ("D:\\ASUS\\Desktop\\个人项目\\测试demo\\src\\main\\resources\\generatorConfig.xml");
            }
        }.xml_make();
    }
```
重写 mybatisCodeGenerator 接口，并且返回 mybatis配置文件的路径，就可以了


生成 带有 lombok注解的代码


#### 配置如下

```xml






<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

    <!--    &lt;!&ndash; 引入配置文件 &ndash;&gt;-->
<!--        <properties resource="project_path.properties">-->
<!--        </properties>-->

    <properties resource="project_path.properties"/>
    <!-- 放到resources目录下，点击 plugin -> mybatis:generate    -->
    <!--
        select table_schema, table_name,column_name from  INFORMATION_SCHEMA.KEY_COLUMN_USAGE  t where t.table_schema='blog_v4'


       -->
    <!-- 记得修改默认的密码    -->
    <context id="mysqlTable" targetRuntime="MyBatis3"     defaultModelType="flat">
        <plugin type="com.lyr.mybatisjpaplugin.util.LombokPlugin"/>
        <plugin type="org.mybatis.generator.plugins.FluentBuilderMethodsPlugin" />
        <!--        <plugin type="org.mybatis.generator.plugins.ToStringPlugin" />-->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin" />
        <!--        <plugin type="org.mybatis.generator.plugins.RowBoundsPlugin" />-->

        <commentGenerator type="com.lyr.mybatisjpaplugin.util.MyCommentGenerator" >
            <!--            &lt;!&ndash; 是否去除自动生成的注释 true：是 ： false:否 &ndash;&gt;-->
            <!--            <property name="suppressAllComments" value="true" />-->
            <!--            &lt;!&ndash; 阻止注释中包含时间戳 true：是 ： false:否 &ndash;&gt;-->
            <!--            <property name="suppressDate" value="true" />-->
            <!--            &lt;!&ndash;  注释是否包含数据库表的注释信息  true：是 ： false:否 &ndash;&gt;-->
            <!--            <property name="addRemarkComments" value="true" />-->
        </commentGenerator>


        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://127.0.0.1:3306/blog_v4?useUnicode=true&amp;characterEncoding=UTF-8&amp;useSSL=false"
                        userId="root"
                        password="422525">
            <property name="nullCatalogMeansCurrent" value="true"/>
        </jdbcConnection>

        <javaModelGenerator targetPackage="${domain}"
                            targetProject="./src/main/java">
            <property name="enableSubPackages" value="true" />
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

        <!--    sql文件    -->
        <sqlMapGenerator targetPackage="mapper/"
                         targetProject="src/main/resources/">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>

        <javaClientGenerator type="XMLMAPPER"
                             targetPackage="${mapper}"
                             targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>




        <table tableName="t_blog" domainObjectName="Blog"
               enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false"
               enableDeleteByPrimaryKey="false"
        >

            <generatedKey column="blog_id"  sqlStatement="Mysql"  identity="true" />
        </table>
      

    </context>
</generatorConfiguration>



```







##  5. 枚举字段自动填充默认值

用法： 比如数据库逻辑删除，小项目 自己 数据的表 老是忘记填充默认值，那 可以在应用层 进行填充【也是鸡肋功能】

```java

package com.example.demo.common;


import com.lyr.mybatisjpaplugin.annotation.InsertValue;
import com.lyr.mybatisjpaplugin.common.BaseIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑删除， insert的时候 默认为 0 ，not deleted
 * @Author lyr
 * @create 2020/12/20 17:44
 */
@AllArgsConstructor
@Getter
@InsertValue(whenFieldAbsent = 0)
public enum DeleteStatus implements BaseIntEnum<DeleteStatus> {
    /**
     * 没有被删除
     */
    not_deleted(0),
    /**
     * 被删除了
     */
    deleted(1);

    public final int code;

    /**
     * 存入数据库的值
     *
     * @return
     */
    @Override
    public Integer getValue() {
        return code;
    }


}

```



@InsertValue(whenFieldAbsent = 0)  当 单表 insert 的时候，检查到 继承了  BaseIntEnum 接口的枚举后，会自动 填充默认值，比如  is_deleted =0 表示没有填充，那么 设置默认值为0就可以了



