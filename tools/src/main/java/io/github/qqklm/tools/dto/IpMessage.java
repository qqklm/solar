package io.github.qqklm.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wb
 * @date 2022/5/15 10:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpMessage {

    /**
     * 市名
     */
    private String cityName;

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 国家名
     */
    private String countryName;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 省名
     */
    private String regionName;

    /**
     * 时区
     */
    private String timeZone;

    /**
     * 邮编
     */
    private String zipCode;
}
