package com.ruoyi.common.jimmer;

/**
 * Thread-local pagination metadata used by Jimmer repositories to preserve
 * RuoYi controller contracts without PageHelper/MyBatis.
 */
public final class JimmerPage
{
    private static final ThreadLocal<PageState> STATE = new ThreadLocal<>();

    private JimmerPage()
    {
    }

    public static void start(int pageNum, int pageSize, String orderBy)
    {
        STATE.set(new PageState(pageNum <= 0 ? 1 : pageNum, pageSize <= 0 ? 10 : pageSize, orderBy));
    }

    public static PageState current()
    {
        return STATE.get();
    }

    public static boolean isPaged()
    {
        return STATE.get() != null;
    }

    public static void total(long total)
    {
        PageState state = STATE.get();
        if (state != null)
        {
            state.setTotal(total);
        }
    }

    public static long totalOrSize(int size)
    {
        PageState state = STATE.get();
        return state == null || state.getTotal() < 0 ? size : state.getTotal();
    }

    public static void clear()
    {
        STATE.remove();
    }

    public static final class PageState
    {
        private final int pageNum;
        private final int pageSize;
        private final String orderBy;
        private long total = -1L;

        private PageState(int pageNum, int pageSize, String orderBy)
        {
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.orderBy = orderBy;
        }

        public int getPageNum()
        {
            return pageNum;
        }

        public int getPageSize()
        {
            return pageSize;
        }

        public String getOrderBy()
        {
            return orderBy;
        }

        public int getOffset()
        {
            return (pageNum - 1) * pageSize;
        }

        public long getTotal()
        {
            return total;
        }

        private void setTotal(long total)
        {
            this.total = total;
        }
    }
}
