package com.demo.monitor.core.trace.util;

import java.util.*;

public class JsonUtil {

    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        build(sb, obj);
        return sb.toString();
    }

    private static void build(StringBuilder sb, Object obj) {

        if (obj == null) {
            sb.append("null");
            return;
        }

        if (obj instanceof String) {
            sb.append("\"").append(obj).append("\"");
        }

        else if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj);
        }

        else if (obj instanceof Map) {
            sb.append("{");
            Map<?, ?> map = (Map<?, ?>) obj;
            int i = 0;
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (i++ > 0) sb.append(",");
                sb.append("\"").append(e.getKey()).append("\":");
                build(sb, e.getValue());
            }
            sb.append("}");
        }

        else if (obj instanceof List) {
            sb.append("[");
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                build(sb, list.get(i));
            }
            sb.append("]");
        }

        else {
            sb.append("\"").append(obj.toString()).append("\"");
        }
    }
}