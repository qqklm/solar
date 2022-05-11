package io.github.qqklm.common.component;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.text.CharSequenceUtil;
import io.github.qqklm.common.BusinessException;
import io.github.qqklm.common.BusinessStatus;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 国际化组件
 *
 * @author wb
 * @date 2022/3/25 17:31
 */
@Component
public class I18nComponent {
    /**
     * 默认语种
     */
    private static final Locale DEFAULT_LOCALE = new Locale(SupportedLanguage.ZH_CN.getLanguage(), SupportedLanguage.ZH_CN.getCountry());
    /**
     * 国际化资源
     */
    private final MessageSource messageSource;
    private final String NOT_FOUND = "I18n.Not.Found";

    public I18nComponent(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 获取请求头中的语种
     *
     * @param request 请求
     * @return 语种列表
     */
    public static List<Locale.LanguageRange> getParsedAcceptLanguage(HttpServletRequest request) {
        List<Locale.LanguageRange> defaultLanguageList = Collections.singletonList(new Locale.LanguageRange(Locale.getDefault().getLanguage()));
        try {
            String acceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            // zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6
            return CharSequenceUtil.isBlank(acceptLanguage) ? defaultLanguageList : Locale.LanguageRange.parse(acceptLanguage);
        } catch (Exception e) {
            return defaultLanguageList;
        }
    }

    /**
     * 获取请求头中的语种
     *
     * @param request 请求
     * @return 语种列表
     */
    public static List<Locale.LanguageRange> getParsedAcceptLanguage(ServletServerHttpRequest request) {
        Objects.requireNonNull(request);
        try {
            return request.getHeaders().getAcceptLanguage();
        } catch (Exception e) {
            return Collections.singletonList(new Locale.LanguageRange(Locale.getDefault().getLanguage()));
        }
    }

    /**
     * 获取request中Accept-Language的请求头
     *
     * @param request 请求
     * @return {@link Locale}
     */
    public Locale getLocale(ServletServerHttpRequest request) {
        List<Locale.LanguageRange> acceptLanguage = getParsedAcceptLanguage(request);
        return doGetLocale(acceptLanguage);
    }

    /**
     * 获取request中Accept-Language的请求头
     *
     * @param request 请求
     * @return {@link Locale}
     */
    public Locale getLocale(HttpServletRequest request) {
        List<Locale.LanguageRange> acceptLanguage = getParsedAcceptLanguage(request);
        return doGetLocale(acceptLanguage);
    }

    private Locale doGetLocale(List<Locale.LanguageRange> acceptLanguage) {
        Locale.LanguageRange languageRange = acceptLanguage.stream().sorted((o1, o2) -> (int) (o1.getWeight() - o2.getWeight())).findFirst().get();
        List<String> range = CharSequenceUtil.split(languageRange.getRange(), "-");
        SupportedLanguage instance = SupportedLanguage.getInstance(range);
        return new Locale(instance.getLanguage(), instance.getCountry());
    }

    /**
     * 默认获取request中Accept-Language的请求头
     *
     * @param request 请求
     * @return {@link Locale}
     */
    public Locale getLocale(HttpServletRequest request, String languageHeaderName) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(languageHeaderName);
        String languageCode = request.getHeader(languageHeaderName);
        // 未设置语种，使用简体中文
        if (CharSequenceUtil.isBlank(languageCode)) {
            return DEFAULT_LOCALE;
        }
        List<String> split = CharSequenceUtil.split(languageCode, "-");
        // 语种格式设置错误
        if (IterUtil.isEmpty(split) || split.size() != 2) {
            throw new BusinessException(BusinessStatus.I18N_LANGUAGE_FORMAT_ERROR, new Object[]{languageCode});
        }
        SupportedLanguage instance = SupportedLanguage.getInstance(split.get(0), split.get(1));
        return new Locale(instance.getLanguage(), instance.getCountry());
    }

    /**
     * 国际化
     *
     * @param code    国际化的key值
     * @param args    国际化的key值的参数
     * @param request 请求
     * @return 国际化后字符串
     */
    public String i18n(String code, Object[] args, ServletServerHttpRequest request) {
        try {
            return messageSource.getMessage(code, args, getLocale(request));
        } catch (Exception e) {
            return messageSource.getMessage(NOT_FOUND, new Object[]{code}, getLocale(request));
        }
    }

    /**
     * 国际化
     *
     * @param code   国际化的key值
     * @param args   国际化的key值的参数
     * @param locale 语种
     * @return 国际化后字符串
     */
    public String i18n(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            return messageSource.getMessage(NOT_FOUND, new Object[]{code}, locale);
        }
    }

    /**
     * 国际化支持的语种
     */
    @Getter
    private enum SupportedLanguage {
        /**
         * 汉语
         */
        ZH_CN("zh", "CN"),
        /**
         * 美式英语
         */
        EN_US("en", "US");
        private final String language;
        private final String country;

        SupportedLanguage(String language, String country) {
            this.language = language;
            this.country = country;
        }

        public static SupportedLanguage getInstance(String language, String country) {
            Objects.requireNonNull(language);
            Objects.requireNonNull(country);
            if (language.equalsIgnoreCase(ZH_CN.language) && country.equalsIgnoreCase(ZH_CN.country)) {
                return ZH_CN;
            }
            if (language.equalsIgnoreCase(EN_US.language) && country.equalsIgnoreCase(EN_US.country)) {
                return EN_US;
            }
            throw new BusinessException(BusinessStatus.I18N_LANGUAGE_NOT_SUPPORT);
        }

        public static SupportedLanguage getInstance(List<String> languageRange) {
            // 语种设置错误，使用中文
            if (IterUtil.isEmpty(languageRange) || languageRange.size() > 2) {
                return ZH_CN;
            }
            if (languageRange.get(0).equalsIgnoreCase(ZH_CN.language) || (languageRange.get(0).equalsIgnoreCase(ZH_CN.language) && languageRange.get(1).equalsIgnoreCase(ZH_CN.country))) {
                return ZH_CN;
            }
            if (languageRange.get(0).equalsIgnoreCase(EN_US.language) || (languageRange.get(0).equalsIgnoreCase(EN_US.language) || languageRange.get(1).equalsIgnoreCase(EN_US.country))) {
                return EN_US;
            }
            return ZH_CN;
        }

        public static SupportedLanguage getInstance(String language) {
            Objects.requireNonNull(language);
            if (language.equalsIgnoreCase(ZH_CN.language)) {
                return ZH_CN;
            }
            if (language.equalsIgnoreCase(EN_US.language)) {
                return EN_US;
            }
            throw new BusinessException(BusinessStatus.I18N_LANGUAGE_NOT_SUPPORT);
        }
    }
}
