package com.github.xingren.datasource;

/**
 * @author HuZhenSha
 * @since 2021/10/26
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>() {
        /**
         * 将 master 数据源的 key作为默认数据源的 key
         */
        @Override
        protected String initialValue() {
            return "master";
        }
    };

    /**
     * 切换数据源
     * @param key eky
     */
    public static void setTenantKey(String key) {
        CONTEXT_HOLDER.set(key);
    }
    /**
     * 获取数据源
     * @return tenant
     */
    public static String getTenantKey() {
        return CONTEXT_HOLDER.get();
    }


}
