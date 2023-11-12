package zoz.cool.javis.common.api;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 通用分页数据封装 Created by zhayongchun on 2023/11/16.
 */

@Data
public class CommonPage<T> {
    private Integer pageNum;// 当前页码
    private Integer pageSize;// 每页数量
    private Integer totalPage;// 总页数
    private Long total;// 总条数
    private List<T> list;// 分页数据

    // 将PageHelper分页后的list转为分页信息
    public static <T> CommonPage<T> restPage(List<T> list) {
        CommonPage<T> result = new CommonPage<>();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        result.setTotalPage(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return result;
    }

    // 将mybatis3分页后的list转为分页信息
    public static <T> CommonPage<T> restPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> pageInfo) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotalPage((int) pageInfo.getPages());
        result.setPageNum((int) pageInfo.getCurrent());
        result.setPageSize((int) pageInfo.getSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getRecords());
        return result;
    }

    // 将SpringData分页后的list转为分页信息
    public static <T> CommonPage<T> restPage(Page<T> pageInfo) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotalPage(pageInfo.getTotalPages());
        result.setPageNum(pageInfo.getNumber());
        result.setPageSize(pageInfo.getSize());
        result.setTotal(pageInfo.getTotalElements());
        result.setList(pageInfo.getContent());
        return result;
    }
}
