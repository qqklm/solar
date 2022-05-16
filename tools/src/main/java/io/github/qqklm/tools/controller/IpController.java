package io.github.qqklm.tools.controller;

import io.github.qqklm.tools.dto.IpMessage;
import io.github.qqklm.tools.service.IpService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * IP相关控制器
 *
 * @author wb
 * @date 2022/5/14 22:08
 */
@RestController
@RequestMapping("/ip")
public class IpController {
    private final IpService ipService;

    public IpController(IpService ipService) {
        this.ipService = ipService;
    }

    /**
     * 根据IP地址获取相关信息
     *
     * @param ip ip地址，不能是域名
     * @return IP地址信息
     */
    @GetMapping("/loc")
    public IpMessage getLocation(@NotBlank String ip) {
        return this.ipService.getLoc(ip);
    }

    /**
     * 验证IP地址是否正确
     *
     * @param ip ip地址，不能是域名
     * @return true：正确，false：不正确
     */
    @GetMapping("/check")
    public boolean check(@NotBlank String ip) {
        return this.ipService.check(ip);
    }
}
