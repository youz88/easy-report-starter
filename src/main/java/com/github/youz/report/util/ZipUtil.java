package com.github.youz.report.util;

import com.github.youz.report.constant.ReportConst;
import com.mybatisflex.core.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具类
 */
@Log4j2
public class ZipUtil {

    /**
     * 打包多个文件至zip
     *
     * @param filePaths 文件路径
     * @param fileName  文件名
     * @return zip文件路径
     */
    public static String zipFiles(List<String> filePaths, String fileName) {
        if (CollectionUtils.isEmpty(filePaths)) {
            return ReportConst.EMPTY;
        }

        // zip文件夹路径
        String directoryPath = new File(filePaths.get(ReportConst.ZER0)).getParent();

        // 打包至zip格式
        String zipPath = directoryPath + File.separator + fileName + ReportConst.ZIP_SUFFIX_NAME;
        toZip(filePaths, zipPath);

        // 删除临时文件
        filePaths.stream()
                .filter(StringUtil::isNotBlank)
                .forEach(path -> {
                    // 删除临时文件
                    new File(path).delete();
                });
        return zipPath;
    }

    /**
     * 打包多个文件至zip格式
     *
     * @param filePaths 需要打包的路径集合
     * @param zipPath   打包后zip所在的文件路径
     */
    private static void toZip(Collection<String> filePaths, String zipPath) {
        InputStream input;

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(Paths.get(zipPath)))) {
            for (String filePath : filePaths) {
                if (StringUtil.isBlank(filePath)) {
                    continue;
                }
                File file = new File(filePath);
                input = Files.newInputStream(file.toPath());
                zipOut.putNextEntry(new ZipEntry(file.getName()));

                int temp;
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }

                input.close();
            }
        } catch (Exception e) {
            log.error("生成压缩文件失败：", e);
        }
    }
}
