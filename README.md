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



