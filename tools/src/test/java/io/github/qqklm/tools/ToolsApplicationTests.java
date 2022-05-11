package io.github.qqklm.tools;

import cn.org.atool.fluent.mybatis.metadata.DbType;
import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ToolsApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * 生成dao相关代码
     */
    @Test
    public void generate() {
        // 引用配置类，build方法允许有多个配置类，生成多个数据源
        FileGenerator.build(MySqlEmpty.class);
    }

    @Tables(
            // 设置数据库连接信息
            url = "jdbc:mysql://localhost:3306/tools?serverTimezone=UTC",
            username = "root",
            password = "wb_239500",
            dbType = DbType.MYSQL,
            // entity和dao的package
            basePack = "io.github.qqklm.tools",
            // 设置哪些表要生成Entity文件
            tables = {@Table(value = "ip2location_ip6"), @Table(value = "ip2location_ip4")},
//                relations = {
//                        @Relation(source = "crawler_video_select", target = "crawler_source", type = RelationType.TwoWay_1_1, where = "SOURCE_ID = ID")
//                },
            // 设置entity类生成src目录, 相对于 user.dir
            srcDir = "src/main/java",
            // 设置dao接口和实现的src目录, 相对于 user.dir
            daoDir = "src/main/java",
            // 此处设置是针对所有表
            gmtCreated = "CREATED_TIME",
            gmtModified = "",
            logicDeleted = ""
    )
    static class MySqlEmpty { //类名随便取, 只是配置定义的一个载体
    }

}
