package com.okr.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Mixin {

    private static final String[] SIGNED_ARR = new String[]{"à", "á", "ạ", "ả", "ã", "â", "ầ", "ấ", "ậ", "ẩ", "ẫ",
            "ă", "ằ", "ắ", "ặ", "ẳ", "ẵ", "è", "é", "ẹ", "ẻ", "ẽ", "ê", "ề", "ế", "ệ", "ể", "ễ", "ì", "í", "ị", "ỉ",
            "ĩ", "ò", "ó", "ọ", "ỏ", "õ", "ô", "ồ", "ố", "ộ", "ổ", "ỗ", "ơ", "ờ", "ớ", "ợ", "ở", "ỡ", "ù", "ú", "ụ",
            "ủ", "ũ", "ư", "ừ", "ứ", "ự", "ử", "ữ", "ỳ", "ý", "ỵ", "ỷ", "ỹ", "đ", "À", "Á", "Ạ", "Ả", "Ã", "Â", "Ầ",
            "Ấ", "Ậ", "Ẩ", "Ẫ", "Ă", "Ằ", "Ắ", "Ặ", "Ẳ", "Ẵ", "È", "É", "Ẹ", "Ẻ", "Ẽ", "Ê", "Ề", "Ế", "Ệ", "Ể", "Ễ",
            "Ì", "Í", "Ị", "Ỉ", "Ĩ", "Ò", "Ó", "Ọ", "Ỏ", "Õ", "Ô", "Ồ", "Ố", "Ộ", "Ổ", "Ỗ", "Ơ", "Ờ", "Ớ", "Ợ", "Ở",
            "Ỡ", "Ù", "Ú", "Ụ", "Ủ", "Ũ", "Ư", "Ừ", "Ứ", "Ự", "Ử", "Ữ", "Ỳ", "Ý", "Ỵ", "Ỷ", "Ỹ", "Đ"};
    private static final String[] UNSIGNED_ARR = new String[]{"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "i", "i", "i", "i",
            "i", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u",
            "u", "u", "u", "u", "u", "u", "u", "u", "y", "y", "y", "y", "y", "d", "A", "A", "A", "A", "A", "A", "A",
            "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E",
            "I", "I", "I", "I", "I", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O",
            "O", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "Y", "Y", "Y", "Y", "Y", "D"};


    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            if (((String) object).trim().length() == 0) {
                return true;
            }
        } else if (object instanceof Collection) {
            return isCollectionEmpty((Collection<?>) object);
        }
        return false;
    }

    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static Object NVL(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Double NVL(Double value) {

        return NVL(value, new Double(0));
    }

    public static Integer NVL(Integer value) {
        return value == null ? new Integer(0) : value;
    }

    public static Integer NVL(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static BigDecimal NVL(BigDecimal value) {
        return value == null ? new BigDecimal(0) : value;
    }

    public static Double NVL(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Long NVL(Long value, Long defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String NVL(String value, String nullValue, String notNullValue) {

        return value == null ? nullValue : notNullValue;
    }

    public static String NVL(String value, String defaultValue) {

        return NVL(value, defaultValue, value);
    }

    public static String NVL(String value) {

        return NVL(value, "");
    }

    public static Long NVL(Long value) {

        return NVL(value, 0L);
    }

    /**
     * Check string is null.
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }

    /**
     * Check list object is null.
     *
     * @param data
     * @return
     */
    public static boolean isNullOrEmpty(List data) {
        return (data == null || data.isEmpty());
    }

    public static void filter(String str, StringBuilder queryString, List<Object> paramList, String field) {
        if ((str != null) && !"".equals(str.trim())) {
            queryString.append(" AND LOWER(").append(field).append(") LIKE ? ");
            str = str.replace("  ", " ");
            str = "%" + str.trim().toLowerCase().replace("/", "//").replace("_", "/_").replace("%", "/%") + "%";
            paramList.add(str);
        }
    }

    public static void filter(Long n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0L)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 so rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Double n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0L)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 so rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Boolean n, StringBuilder queryString, List<Object> paramList, String field) {
        if (n != null) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 Integer rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Integer n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 xau rong hay null khong
     *
     * @param date        So
     * @param queryString
     * @param field
     * @param paramList
     */
    public static void filter(Date date, StringBuilder queryString, List<Object> paramList, String field) {
        if ((date != null)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(date);
        }
    }

    /**
     * Chuyen doi tuong String thanh doi tuong Date.
     *
     * @param date Xau ngay, co dinh dang duoc quy trinh trong file Constants
     * @return Doi tuong Date
     * @throws Exception Exception
     */
    public static Date convertStringToDate(String date) throws Exception {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            return dateFormat.parse(date);
        }
    }


    /**
     * Chuyen doi tuong Date thanh doi tuong String.
     *
     * @param date Doi tuong Date
     * @return Xau ngay, co dang dd/MM/yyyy
     */
    public static String convertDateToString(Date date) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format(date);
        }
    }

    /**
     * Chuyen doi tuong Date thanh doi tuong String.
     *
     * @param date Doi tuong Date
     * @return Xau ngay, co dang dd/MM/yyyy
     */
    public static String convertDateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        }
    }

    /**
     * Conver tu string sang date theo dinh dang mong muon
     *
     * @param date
     * @param pattern : kieu dinh dang vd: "dd/MM/yyyy hh:MM"
     * @return
     * @throws Exception
     */
    public static Date convertStringToDateTime(String date, String pattern) throws Exception {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            try {
                return dateFormat.parse(date);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * removeSignAndMultiSpace
     *
     * @param s
     * @return
     */
    public static String removeSignAndMultiSpace(String s) {
        s = removeSign(s);
        s = s.trim().replaceAll("\\s+", " ");
        return s.replaceAll(" ", "_");
    }

    /**
     * Loai bo cac dau, ten file chi chua cac ky tu ASCII.
     *
     * @param originalName
     * @return String : xau sau khi bo dau
     */
    public static String removeSign(String originalName) {
        if (originalName == null) {
            return "";
        }
        String result = originalName;
        for (int i = 0; i < SIGNED_ARR.length; i++) {
            result = result.replaceAll(SIGNED_ARR[i], UNSIGNED_ARR[i]);
        }
        return result;
    }

    /**
     * Upload dữ liệu ảnh lên hệ thống imgur => return về link
     *
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public static String getImgurContent(MultipartFile multipartFile) throws Exception {
        URL url;
        url = new URL("https://api.imgur.com/3/image");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // encode base64 file
        String encodedString = Base64.getEncoder().encodeToString(multipartFile.getBytes());
        String imageBase64 = URLEncoder.encode("image", "UTF-8") + "="
                + URLEncoder.encode(encodedString, "UTF-8");

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "Client-ID 9716f147db58d44");

        conn.connect();
        StringBuilder stb = new StringBuilder();
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(imageBase64);
        wr.flush();

        // Get the response
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            stb.append(line).append("\n");
        }
        wr.close();
        rd.close();
        String res = stb.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(res);
        JsonNode data = node.get("data");
        String link = data.get("link").asText();
        return link;

    }
}
