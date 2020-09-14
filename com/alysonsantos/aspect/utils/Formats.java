package com.alysonsantos.aspect.utils;

import java.text.DecimalFormat;

public class Formats {

    private static String[] formats = {"-", "-", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "OC", "N", "D", "UN", "DD", "TR",
            "QT", "QN", "SD", "SSD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QVG", "QVN", "SEV", "SPV", "OVG", "NVG",
            "TG"};

    public static String apply(Object value) {
        try {
            String val = (new DecimalFormat("#,###")).format(value).replace(".", ",");
            int ii = val.indexOf(","), i = val.split(",").length;
            if (ii == -1)
                return val;
            return (val.substring(0, ii + 2) + formats[i]).replace(",0", "");
        } catch (Exception e) {
            String val = (new DecimalFormat("#,###")).format(value).replace(".", ",");
            int ii = val.indexOf(",");
            if (ii == -1)
                return val;
            String num = val.substring(0, 1);
            String finalVal = val.substring(1).replace(",", "");
            return num + "e" + finalVal.length();
        }
    }
}
