package com.github.youz.report.imports.listener;

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
public class CompositeImportHandler {

    private final Map<Integer, ImportBusinessListener> listenerMap;

    @Autowired
    public CompositeImportHandler(List<ImportBusinessListener> listeners) {
        // 初始化处理器
        listenerMap = listeners.stream()
                .collect(Collectors.toMap(handler -> handler.businessType().getCode(), Function.identity()));
    }

    /**
     * 获取处理器
     *
     * @param code 编号
     * @return 处理器
     */
    public ImportBusinessListener getListener(int code) {
        ImportBusinessListener handler = listenerMap.get(code);
        ExceptionCode.IMPORT_NO_MATCH_LISTENER.assertNotNull(handler);
        return handler;
    }

}

