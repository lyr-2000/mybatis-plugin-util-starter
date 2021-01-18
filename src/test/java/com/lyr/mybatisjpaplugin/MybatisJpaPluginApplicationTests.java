package com.lyr.mybatisjpaplugin;

import com.lyr.mybatisjpaplugin.annotation.CreateTime;
import com.lyr.mybatisjpaplugin.annotation.InsertValue;
import com.lyr.mybatisjpaplugin.annotation.UnEscape;
import com.lyr.mybatisjpaplugin.annotation.UpdateTime;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.*;
import java.util.Date;

@SpringBootTest
class MybatisJpaPluginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Accessors(chain = true)
    @InsertValue(whenFieldAbsent = 0)
    static class Pojo{
        @Id
                @UpdateTime
                @UnEscape
                @CreateTime
        String name;
        @Column(name = "hello")
                @Version
        Date dateee;
    }
}
