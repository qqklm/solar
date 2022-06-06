package io.github.qqklm.faker.dto.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author wb
 * @date 2022/5/19 14:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionMsg {
    /**
     * 数据库实例名, MySQL没有这个概念
     */
    private String dbName;
    /**
     * 数据库名
     */
    @NotBlank
    private String schemaName;
    /**
     * 用户名
     */
    @NotBlank
    private String userName;
    /**
     * 密码
     */
    @NotBlank
    private String password;
    /**
     * 端口
     */
    @NotBlank
    private Integer port;
    /**
     * 数据库地址
     */
    @NotBlank
    private String host;

    /**
     * 数据库类型
     *
     * @see DbTypeEnum
     */
    @NotBlank
    private String dbType;


}
