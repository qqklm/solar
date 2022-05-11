package io.github.qqklm.common;

import lombok.Data;

/**
 * 分页请求,请求参数实体应该继承该类
 *
 * @author wb
 * @date 2022/3/29 16:40
 */
@Data
public class PageReq {
    private Long pageSize = 10L;
    private Long page = 1L;
}
