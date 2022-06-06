package io.github.qqklm.faker.controller;

import io.github.qqklm.faker.dto.db.ConnectionMsg;
import io.github.qqklm.faker.dto.db.GenTable;
import io.github.qqklm.faker.meta.Table;
import io.github.qqklm.faker.service.FakeDbService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据库相关操作
 *
 * @author wb
 * @date 2022/5/23 15:38
 */
@RestController
@RequestMapping("db")
public class FakeDbController {

    private final FakeDbService fakeDbService;

    public FakeDbController(FakeDbService fakeDbService) {
        this.fakeDbService = fakeDbService;
    }

    /**
     * 获取数据库结构
     *
     * @param connectionMsg 数据库连接信息
     * @return 以表为单位的数据库结构
     */
    @PostMapping("schema")
    public List<Table> dbSchema(@RequestBody ConnectionMsg connectionMsg) {
        return fakeDbService.schema(connectionMsg);
    }

    /**
     * mock数据
     *
     * @param genTable mock数据所需参数
     */
    @PostMapping("gen")
    public void gen(@RequestBody GenTable genTable) {
        fakeDbService.fake(genTable);
    }
}
