package io.github.qqklm.tools.controller;

import io.github.qqklm.common.component.I18nComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * @author wb
 * @date 2022/5/11 13:50
 */
@RestController
@RequestMapping
public class TestController {
    private final I18nComponent i18nComponent;

    public TestController(I18nComponent i18nComponent) {
        this.i18nComponent = i18nComponent;
    }

    @GetMapping("")
    public void test() {
        System.out.println(i18nComponent.i18n(12, null, Locale.CHINA));
        throw new RuntimeException();
    }
}
