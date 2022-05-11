package io.github.qqklm.common.config;

import cn.hutool.core.collection.ListUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 国际化配置
 *
 * @author wb
 * @date 2022/3/25 18:17
 */
@Configuration
public class I18nConfig {
    /**
     * 其他模块指定的国际化
     */
    @Value("${i18n.base.name:i18n/messages}")
    private String[] i18nBaseName;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        Locale.setDefault(Locale.CHINESE);
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        // 配置文件所在目录为 i18n，文件前缀为 messages
        // 通用模块下的国际化
        String defaultBaseName = "i18n/messages-common";
        ArrayList<String> baseNames = ListUtil.toList(i18nBaseName);
        baseNames.add(0, defaultBaseName);
        source.setBasenames(baseNames.toArray(new String[0]));

        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
