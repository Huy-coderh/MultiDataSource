package com.naic.datasource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author HuZhenSha
 * @since 2021/10/28
 */
@Data
@TableName("mes_tenant")
public class Tenant {

    private Long id;

    private Long tenantId;

    private String tenantName;

    private String host;

    private String port;

    private String driver;

    private String username;

    private String password;

    private Boolean status;

}
