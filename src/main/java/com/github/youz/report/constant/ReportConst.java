package com.github.youz.report.constant;


public class ReportConst {

    public static final String EMPTY = "";

    public static final int ZER0 = 0;

    public static final long ZER0_L = 0L;

    public static final int ONE = 1;

    public static final String MINUS_SYMBOL = "-";

    public static final String COMMA_SYMBOL = ",";

    public static final String FULL_STOP_SYMBOL = ".";

    public static final String SLASH_SYMBOL = "/";


    public static final String UNDER_LINE_SYMBOL = "_";

    public static final String ZIP_SUFFIX_NAME = ".zip";

    public static final String EXPORT_DIRECTORY_NAME = "export";

    public static final String IMPORT_DIRECTORY_NAME = "import";

    /**
     * 导出临时文件根目录
     */
    public static final String EXPORT_ROOT_PATH = System.getProperty("export.root.path", getResourcePath() + EXPORT_DIRECTORY_NAME);

    /**
     * 导入临时文件根目录
     */
    public static final String IMPORT_ROOT_PATH = System.getProperty("import.root.path", getResourcePath() + IMPORT_DIRECTORY_NAME);

    /**
     * 获取资源路径
     *
     * @return 返回资源路径字符串
     */
    private static String getResourcePath() {
        return ReportConst.class.getResource(SLASH_SYMBOL).getPath();
    }

}
