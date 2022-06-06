package io.github.qqklm.faker.service;

import cn.hutool.core.date.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.qqklm.faker.meta.SimpleAddress;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author wb
 * @date 2022/5/19 10:49
 */
public class FakeService {
    public static final List<String> USER_NAME_PREFIX = new ArrayList<>(510);
    public static final List<String> USER_NAME_SUFFIX = new ArrayList<>(1000);
    public static final List<Integer> ID_CODE = new ArrayList<>(3500);
    public static final Map<String, Integer> ID_AREA_CODE = new HashMap<>(3500);
    public static final List<SimpleAddress> ADDRESS = new ArrayList<>(50000);
    public static String BASE_STR_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String BASE_STR_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    public static String BASE_STR_NUMBER = "0123456789";
    public static String BASE_STR_SPECIAL = "!@#$%^&*";
    public static String BASE_STR = BASE_STR_UPPER_CASE + BASE_STR_LOWER_CASE + BASE_STR_NUMBER + BASE_STR_SPECIAL;
    public static Snowflake SNOW_FLAKE = IdUtil.getSnowflake();

    static {
        File addressFile = new ClassPathResource("address.json").getFile();
        if (addressFile.exists()) {
            JSONArray json = JSONUtil.readJSONArray(addressFile, StandardCharsets.UTF_8);
            if (!json.isEmpty()) {
                json.forEach(row -> ADDRESS.add(new SimpleAddress((String) ((JSONObject) row).get("province"), (String) ((JSONObject) row).get("city"), (String) ((JSONObject) row).get("county"), (String) ((JSONObject) row).get("town"))));
            }
        }
        File idAreaCodeFile = new ClassPathResource("id_area_code.txt").getFile();
        if (addressFile.exists()) {

            FileUtil.readLines(idAreaCodeFile, StandardCharsets.UTF_8).forEach(str -> {
                List<String> split = StrUtil.split(str, StrPool.TAB);
                ID_AREA_CODE.put(split.get(1), Integer.parseInt(split.get(0)));
                if (split.get(0).length() == 6) {
                    ID_CODE.add(Integer.parseInt(split.get(0)));
                }
            });
        }
        File namePrefixFile = new ClassPathResource("name_prefix.txt").getFile();
        if (namePrefixFile.exists()) {
            USER_NAME_PREFIX.addAll(FileUtil.readLines(namePrefixFile, StandardCharsets.UTF_8));
        }
        File nameSuffixFile = new ClassPathResource("name_suffix.txt").getFile();
        if (nameSuffixFile.exists()) {
            USER_NAME_SUFFIX.addAll(FileUtil.readLines(nameSuffixFile, StandardCharsets.UTF_8));
        }
    }

    public static String fakeSimpleAddress() {
        SimpleAddress simpleAddress = ADDRESS.get(fakeUnsignedInt(ADDRESS.size()));
        return simpleAddress.getProvince() + "-" + simpleAddress.getCity() + "-" + simpleAddress.getCountry() + "-" + simpleAddress.getTown();
    }

    public static String fakeProvince() {
        return ADDRESS.get(fakeUnsignedInt(ADDRESS.size())).getProvince();
    }

    public static String fakeCity() {
        return ADDRESS.get(fakeUnsignedInt(ADDRESS.size())).getCity();
    }

    public static String fakeCountry() {
        return ADDRESS.get(fakeUnsignedInt(ADDRESS.size())).getCountry();
    }

    public static String fakeTown() {
        return ADDRESS.get(fakeUnsignedInt(ADDRESS.size())).getTown();
    }

    public static String fakeStr(String baseStr, int length) {
        return RandomUtil.randomString(baseStr, length);
    }

    public static String fakeStr(int length) {
        return RandomUtil.randomString(BASE_STR, length);
    }

    public static String fakeOrderStr() {
        return SNOW_FLAKE.nextIdStr();
    }

    public static Long fakeOrder() {
        return SNOW_FLAKE.nextId();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime fakeLocalDateTime(LocalDateTime start, LocalDateTime end) {
        DateTime dateTime = RandomUtil.randomDate(localDateTime2Date(start), DateField.SECOND, 0, (int) LocalDateTimeUtil.between(end, start).getSeconds());
        return dateTime.toLocalDateTime();
    }

    public static LocalDateTime fakeLocalDateTime() {
        return fakeLocalDateTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0), LocalDateTime.now());
    }

    public static Date fakeDate(Date start, Date end) {
        return RandomUtil.randomDate(start, DateField.SECOND, 0, (int) DateUtil.between(start, end, DateUnit.SECOND)).toJdkDate();
    }

    public static Date fakeDate() {
        return fakeDate(new Date(0), new Date());
    }

    public static boolean fakeBoolean() {
        return RandomUtil.randomBoolean();
    }

    public static Integer fakeInt(Integer start, Integer end) {
        return RandomUtil.randomInt(start, end);
    }

    public static Integer fakeInt() {
        return fakeInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static Integer fakeUnsignedInt(Integer end) {
        return fakeInt(0, end);
    }

    public static Integer fakeUnsignedIntLength(Integer length) {
        StringBuilder strInt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            strInt.append("1");
        }
        return fakeInt(1, Integer.parseInt(strInt.toString()) * 9);
    }

    public static Integer fakeUnsignedInt() {
        return fakeUnsignedInt(Integer.MAX_VALUE);
    }

    public static Double fakeDouble(Double start, Double end) {
        return RandomUtil.randomDouble(start, end);
    }

    public static Double fakeDouble() {
        return RandomUtil.randomDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static Double fakeUnsignedDouble(Double end) {
        return RandomUtil.randomDouble(1.0, end);
    }

    public static Double fakeUnsignedDouble() {
        return fakeUnsignedDouble(Double.MAX_VALUE);
    }

    public static String fakePhoneNumber() {
        // 1[3-9]\d{9}
        return "1" + fakeStr("3456789", 1) + fakeStr(BASE_STR_NUMBER, 9);
    }

    public static String fakeBirthDay() {
        return LocalDateTimeUtil.format(fakeLocalDateTime(LocalDateTime.now().minusYears(100), LocalDateTime.now()), DatePattern.PURE_DATE_PATTERN);
    }

    public static String fakeIdCard() {
        StringBuilder sb = new StringBuilder();
        // 地区代码
        sb.append(ID_CODE.get(fakeUnsignedInt(ID_CODE.size())));
        // 出生年月
        sb.append(fakeBirthDay());
        // 后缀
        int suffix = fakeUnsignedInt(1000);
        int sex = fakeUnsignedInt(2);
        if (sex == 0 && suffix % 2 == 0) {
            suffix++;
        }
        if (sex != 0 && suffix % 2 == 1) {
            suffix++;
        }
        if (suffix >= 100) {
            sb.append(suffix);
        } else if (suffix >= 10) {
            sb.append("0").append(suffix);
        } else {
            sb.append("00").append(suffix);
        }

        // 处理最后一个字符
        final int[] calcC = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        final char[] calcR = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int[] n = new int[17];
        int result = 0;
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(String.valueOf(sb.charAt(i)));
        }
        for (int i = 0; i < n.length; i++) {
            result += calcC[i] * n[i];
        }

        sb.append(calcR[result % 11]);
        return sb.toString();
    }

    public static String fakeName() {
        final String prefix = USER_NAME_PREFIX.get(fakeUnsignedInt(USER_NAME_PREFIX.size()));
        StringBuilder suffix = new StringBuilder();
        for (int i = 1; i <= fakeInt(1, 3); i++) {
            suffix.append(USER_NAME_SUFFIX.get(fakeUnsignedInt(USER_NAME_SUFFIX.size())));
        }
        return prefix + suffix;
    }
}