package io.github.qqklm.common;

import lombok.Data;

import java.util.List;

/**
 * 分页请求、响应
 *
 * @author wb
 * @date 2022/3/28 13:36
 */
@Data
public class PageInfo<E> {
    /**
     * 总条数
     */
    private Long total = 0L;
    /**
     * 总页数
     */
    private Long totalPage = 0L;
    /**
     * 每页条数
     */
    private Long pageSize = 20L;
    /**
     * 当前页码
     */
    private Long currentPage = 1L;
    /**
     * 当前页数据
     */
    private List<E> data;

    public PageInfo<E> genPageInfo(List<E> data, Long total) {
        PageInfo<E> pageInfo = new PageInfo<>();
        pageInfo.setData(data);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public PageInfo<E> genPageInfo(List<E> data, Long total, Long pageSize, Long currentPage) {
        if (total < 0) {
            throw new BusinessException(BusinessCode.ILLEGAL_ARGUMENT.getCode(), new Object[]{"total"});
        }
        if (pageSize < 0) {
            throw new BusinessException(BusinessCode.ILLEGAL_ARGUMENT.getCode(), new Object[]{"pageSize"});
        }
        if (currentPage < 0) {
            throw new BusinessException(BusinessCode.ILLEGAL_ARGUMENT.getCode(), new Object[]{"currentPage"});
        }
        PageInfo<E> pageInfo = new PageInfo<>();
        pageInfo.setData(data);
        pageInfo.setTotal(total);
        pageInfo.setCurrentPage(currentPage);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotalPage(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        return pageInfo;
    }
}
