package io.github.qqklm.tools.service;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.net.Ipv4Util;
import io.github.qqklm.common.BusinessException;
import io.github.qqklm.tools.common.BusinessCode;
import io.github.qqklm.tools.dao.base.Ip2locationIp4BaseDao;
import io.github.qqklm.tools.dao.base.Ip2locationIp6BaseDao;
import io.github.qqklm.tools.dto.IpMessage;
import io.github.qqklm.tools.entity.Ip2locationIp4Entity;
import io.github.qqklm.tools.entity.Ip2locationIp6Entity;
import io.github.qqklm.tools.wrapper.Ip2locationIp4Query;
import io.github.qqklm.tools.wrapper.Ip2locationIp6Query;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author wb
 * @date 2022/5/14 22:10
 */
@Service
public class IpService {
    private final Ip2locationIp4BaseDao ip4BaseDao;
    private final Ip2locationIp6BaseDao ip6BaseDao;

    public IpService(Ip2locationIp4BaseDao ip4BaseDao, Ip2locationIp6BaseDao ip6BaseDao) {
        this.ip4BaseDao = ip4BaseDao;
        this.ip6BaseDao = ip6BaseDao;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String unknown = "unknown";
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIp = "127.0.0.1";
            String localIpv6 = "0:0:0:0:0:0:0:1";
            if (ipAddress.equals(localIp) || ipAddress.equals(localIpv6)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        String ipSeparate = ",";
        int ipLength = 15;
        if (ipAddress != null && ipAddress.length() > ipLength) {
            if (ipAddress.indexOf(ipSeparate) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(ipSeparate));
            }
        }
        return ipAddress;
    }

    public IpMessage getLoc(String ip) {
        IpMessage ipMessage = new IpMessage();
        if (PatternPool.IPV4.matcher(ip).matches()) {
            long l = Ipv4Util.ipv4ToLong(ip);
            Ip2locationIp4Query query = Ip2locationIp4Query
                    .query(() -> "`ip2location_ip4` force index (idx_ip_from_to) ")
                    .where
                    .ipFrom().le(l)
                    .ipTo().ge(l)
                    .end();
            Optional<Ip2locationIp4Entity> ip4EntityOptional = ip4BaseDao.findOne(query);
            if (ip4EntityOptional.isPresent()) {
                Ip2locationIp4Entity ip2locationIp4Entity = ip4EntityOptional.get();
                ipMessage = new IpMessage(
                        ip2locationIp4Entity.getCityName(),
                        ip2locationIp4Entity.getCountryCode(),
                        ip2locationIp4Entity.getCountryName(),
                        ip2locationIp4Entity.getLatitude(),
                        ip2locationIp4Entity.getLongitude(),
                        ip2locationIp4Entity.getRegionName(),
                        ip2locationIp4Entity.getTimeZone(),
                        ip2locationIp4Entity.getZipCode()
                );
            }
            return ipMessage;
        }
        if (PatternPool.IPV6.matcher(ip).matches()) {
            InetAddress ia;
            try {
                ia = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                // 不会发生异常
                return ipMessage;
            }
            byte[] bytes = ia.getAddress();
            BigDecimal bd = new BigDecimal(new BigInteger(1, bytes));
            final Ip2locationIp6Query query = Ip2locationIp6Query
                    .query(() -> "`ip2location_ip6` force index (idx_ip_from_to) ")
                    .where
                    .ipFrom().le(bd)
                    .ipTo().ge(bd)
                    .end();
            Optional<Ip2locationIp6Entity> ip6EntityOptional = ip6BaseDao.findOne(query);
            if (ip6EntityOptional.isPresent()) {
                Ip2locationIp6Entity ip2locationIp6Entity = ip6EntityOptional.get();
                ipMessage = new IpMessage(
                        ip2locationIp6Entity.getCityName(),
                        ip2locationIp6Entity.getCountryCode(),
                        ip2locationIp6Entity.getCountryName(),
                        ip2locationIp6Entity.getLatitude(),
                        ip2locationIp6Entity.getLongitude(),
                        ip2locationIp6Entity.getRegionName(),
                        ip2locationIp6Entity.getTimeZone(),
                        ip2locationIp6Entity.getZipCode()
                );
            }
            return ipMessage;
        }
        throw new BusinessException(BusinessCode.IP_PATTERN_ERROR.getCode(), new Object[]{ip});
    }

    public boolean check(String ip) {
        return PatternPool.IPV4.matcher(ip).matches() || PatternPool.IPV6.matcher(ip).matches();
    }
}
