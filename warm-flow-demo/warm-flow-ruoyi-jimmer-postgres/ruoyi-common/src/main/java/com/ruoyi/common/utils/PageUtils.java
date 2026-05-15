package com.ruoyi.common.utils;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.jimmer.JimmerPage;
import com.ruoyi.common.utils.sql.SqlUtil;

/**
 * Jimmer-backed pagination utility. It stores request pagination metadata in a
 * local context; repositories apply limit/offset and publish total rows.
 *
 * @author ruoyi
 */
public class PageUtils
{
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        JimmerPage.start(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize, orderBy);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        JimmerPage.clear();
    }
}
