package com.github.youz.report.constant;


public class ReportConst {

    public static final String EMPTY = "";

    public static final int ZER0 = 0;

    public static final int ONE = 1;

    public static final String MINUS_SYMBOL = "-";

    public static final String UNDER_LINE_SYMBOL = "_";

    public static final String ZIP_SUFFIX_NAME = ".zip";

    /**
     * 导出临时文件根目录
     */
    public static final String EXPORT_ROOT_DIRECTORY = System.getProperty("export.root.directory",
            ReportConst.class.getResource("/").getPath() + "export");

}
