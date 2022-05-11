package io.github.qqklm.common.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * 序列化相关组件
 *
 * @author wb
 * @date 2022/3/25 17:51
 */
@Component
public class JacksonComponent {

    @Getter
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonComponent() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Map的JavaType
     *
     * @param keyClass   键class
     * @param valueClass 值class
     * @param <K>        K
     * @param <V>        V
     * @return Map的JavaType
     */
    public <K, V> JavaType getMapType(Class<K> keyClass, Class<V> valueClass) {
        Objects.requireNonNull(keyClass);
        Objects.requireNonNull(valueClass);
        return objectMapper.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
    }

    /**
     * List的JavaType
     *
     * @param elementClass 元素class
     * @param <T>          T
     * @return List的JavaType
     */
    public <T> JavaType getListType(Class<T> elementClass) {
        Objects.requireNonNull(elementClass);
        return objectMapper.getTypeFactory().constructParametricType(ArrayList.class, elementClass);
    }
}
