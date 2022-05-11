package io.github.qqklm.tools.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Ip2locationIp4Entity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "ip2location_ip4",
    schema = "tools"
)
public class Ip2locationIp4Entity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   */
  @TableField("city_name")
  private String cityName;

  /**
   */
  @TableField("country_code")
  private String countryCode;

  /**
   */
  @TableField("country_name")
  private String countryName;

  /**
   */
  @TableField("ip_from")
  private Integer ipFrom;

  /**
   */
  @TableField("ip_to")
  private Integer ipTo;

  /**
   */
  @TableField("latitude")
  private Double latitude;

  /**
   */
  @TableField("longitude")
  private Double longitude;

  /**
   */
  @TableField("region_name")
  private String regionName;

  /**
   */
  @TableField("time_zone")
  private String timeZone;

  /**
   */
  @TableField("zip_code")
  private String zipCode;

  @Override
  public final Class entityClass() {
    return Ip2locationIp4Entity.class;
  }
}
