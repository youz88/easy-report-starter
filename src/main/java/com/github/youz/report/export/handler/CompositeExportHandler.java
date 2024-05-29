package com.github.youz.report.export.handler;

import com.github.youz.report.enums.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@DependsOn("applicationContextUtil")
public class CompositeExportHandler {

    private final Map<Integer, ExportBusinessHandler> handlerMap;

    @Autowired
    public CompositeExportHandler(List<ExportBusinessHandler> handlers) {
        ExceptionCode.EXPORT_HANDLER_EMPTY.assertNotEmpty(handlers);

        // 初始化处理器
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(handler -> handler.businessType().getCode(), Function.identity()));
    }

    /**
     * 获取处理器
     *
     * @param code 编号
     * @return 处理器
     */
    public ExportBusinessHandler getHandler(int code) {
        ExportBusinessHandler handler = handlerMap.get(code);
        ExceptionCode.EXPORT_NO_MATCH_BUSINESS_HANDLER.assertNotNull(handler);
        return handler;
    }

}

