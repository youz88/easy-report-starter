# easy-report

## 项目简介

这是一个基于easy-excel的开源项目, 用于快速构建报表.

## 项目背景

大多公司都需要报表服务用来处理导出、导出功能, 实际上这些代码初始构建时大多都是重复的, 后续的维护又需与业务耦合, 需要大量的时间才能完成. 所以当前项目的目标是构建一个基础报表服务, 可以快速构建报表, 减少重复代码, 降低开发成本.

## 核心功能

### 默认配置

```docker
report:
  common:
    # 导出文件是否上传到云存储(开启后需实现com.github.youz.report.data.UploadCloudData接口)
    upload-cloud: false
  export:
    # 默认分页大小(用于分页查询业务数据)
    page-size: 100
    # 切片子任务所需数据最大值(如果查询导出总数超过该值, 则将数据切分成多个任务)
    slices-task-max-size: 500000
    # 异步任务执行所需数据最大值(如果查询导出总数超过该值, 将会使用定时任务执行, 否则同步导出报表文件)
    async-task-max-size: 200
    # 异步任务执行间隔时间, 单位毫秒(为避免大量异步任务持续查询对数据库造成压力, 所以可配置查询睡眠间隔时间)
    async-task-sleep-time: 100
    # 扫描待执行导出任务
    scan-wait-exec-cron: 0 0/2 * * * ?
    # 扫描待上传导出任务(仅限状态为上传失败)
    scan-wait-upload-cron: 0 0/3 * * * ?
  imports:
    # 批量处理行数
    batch-row: 100
    # 限制最大行数
    limit-max-row: 20000
```

### 报表导出

- 导出接口

  POST http://localhost:8080/report/export

    ```
    {
        # 操作用户ID(用于列表显示时过滤筛选) 非必填
        "userId": 1,
        # 业务类型(根据不同的业务类型, 加载处理器) 必填
        "businessType": 1,
        # 查询参数(用于查询业务数据, 格式为json字符串, 与前端列表展示的查询参数一致) 必填
        "params": "{}",
        # 导出字段名称, 默认导出所有(需与前端约定, 用于控制哪些字段需要导出, 以及导出字段排序) 非必填
        "fieldNames": []
    }
    ```

  返回结果根据属性`com.github.youz.report.config.ReportProperties.ExportProperties#asyncTaskMaxSize`区分为两种方式, 查询**总计条数**小于该值则判定为同步导出, 通过`HttpServletResponse` 组装返回参数, 否则为异步导出, 通过定时任务的方式执行

- 业务导出处理器

  当我们处理具体的导出业务时, 只需要新增业务Handler, 继承[AbstractDataAssemblyExportHandler](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/handler/AbstractDataAssemblyExportHandler.java), 实现抽象方法即可

  示例可参考: [OrderExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportHandler.java), [GoodsExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportHandler.java)

- 导出模版

  调用`BasicExportTemplate.assemblyFixHead`方法实现固定表头初始化.

  调用`BasicExportTemplate.assemblyDynamicHead`方法实现动态表头初始化, 其中动态字段需使用[DynamicColumn](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/bo/DynamicColumn.java)类当做属性字段.

    ```java
    /**
     * 组装固定表头并返回导出模型对象
     *
     * @param tplClass 导出模板类对象
     * @param context  导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyFixHead(Class<?> tplClass, ExportContext context)
    
    /**
     * 组装动态表头并返回导出模型对象
     *
     * @param tplInstance 模板实例对象
     * @param context     导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyDynamicHead(Object tplInstance, ExportContext context)
    ```

  调用`BasicExportTemplate.assemblyBody`方法实现表体初始化

    ```java
    
    /**
     * 组装导出表体并返回导出模型对象
     *
     * @param tplDataList 模板数据列表
     * @param context     导出上下文对象
     * @return 组装好的导出模型对象
     */
    public static BasicExportTemplate assemblyBody(List<?> tplDataList, ExportContext context)
    ```

  示例可参考: [OrderExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportTemplate.java), [GoodsExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportTemplate.java)

  关于导出模版对象, 其中内置了部份自定义注解, 用于更方便的处理导出字段格式化. (格式化时间: [@DateTimeFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DateTimeFormat.java), 默认值设置: [@DefaultValueFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DefaultValueFormat.java), 枚举值转换: [@EnumFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/EnumFormat.java),

  金额格式化: [@MoneyFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/MoneyFormat.java)), 当然也可以自定义实现, 通过方法`com.github.youz.report.converter.ReportConverterLoader#addExportConverter`导入即可

- 导出文件上传

  需将配置项`report.export.upload-cloud`设置为true, 并实现接口[UploadCloudData](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/data/UploadCloudData.java), 即可完成文件上传到云存储.


### 报表导入

- 通过提交本地文件的表单提交方式导入

    ```
    POST http://localhost:8080/report/import-local?businessType=1&userId=1
    Content-Type: multipart/form-data; boundary=WebAppBoundary
    
    --WebAppBoundary
    Content-Disposition: form-data; name="file"; filename="test.xlsx"
    Content-Type: multipart/form-data
    
    < /xx/xx/xx/test.xlsx
    --WebAppBoundary--
    ```

- 通过引用云端文件的方式导入

    ```
    POST http://localhost:8080/report/import-cloud
    Content-Type: application/json
    
    {
        # 操作用户ID(用于列表显示时过滤筛选) 非必填
        "userId": 1,
        # 业务类型(根据不同的业务类型, 加载处理器) 必填
        "businessType": 1,
        # 云端文件路径 必填
        loadFilePath": "http://aaa/bbb/ccc/dd.xlsx"
    }
    ```

- 导入监听器

  当我们处理具体的导入业务时, 只需要新增业务Listener, 实现抽象方法即可

  继承[AbstractPartSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractPartSuccessBusinessListener.java)(部份成功才算导入成功)

  继承[AbstractAllSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractAllSuccessBusinessListener.java)(全部成功才算导入成功)

    ```java
    /**
     * 调用业务方参数校验方法
     *
     * @param data 源数据
     * @return 校验结果
     */
    protected abstract List<ImportInvokeResult.DataStatus> invokeCheckMethod(List<T> data)
    
    /**
     * 调用业务方导入方法
     *
     * @param data 源数据
     */
    protected abstract void invokeImportMethod(List<T> data)
    ```

  示例可参考: [DynamicImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/dynamic/DynamicImportListener.java), [GoodsImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/goods/GoodsImportListener.java), [OrderImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/order/OrderImportListener.java)

- 导入注解

  核心是使用`@ImportCell`定义需要导入扫描的表头, 除了导入表头的定义, 还提供了部份基础信息校验的注解(字段长度校验: [@ImportLength](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportLength.java), 字段非空校验: [@ImportNull](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNull.java), 数字校验: [@ImportNumber](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNumber.java), 手机号校验: [@ImportPhone](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportPhone.java)), 当然也可以自定义注解, 实现接口[ImportCheck](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/check/ImportCheck.java)即可

    ```java
    /**
     * Excel单元格
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface ImportCell {
    
        /**
         * 表头名称
         *
         * @return 表头名称
         */
        String[] value() default {""};
    
        /**
         * 当前列下标
         *
         * @return 下标
         */
        int index() default -1;
    
        /**
         * 是否动态列
         *
         * @return 返回boolean类型，表示是否为动态列，默认为false
         */
        boolean dynamicColumn() default false;
    
        /**
         * 属性值转换器
         *
         * @return 转换器，用于将属性值从一种类型转换为另一种类型，默认为AutoConverter类
         */
        Class<? extends Converter<?>> converter() default AutoConverter.class;
    
        /**
         * 枚举值映射
         *
         * @return 返回 ImportEnum 的子类类型，表示枚举值的映射
         */
        Class<? extends ImportEnum> linkEnum() default ImportEnum.class;
    }
    ```

- 导入属性转换器

  关于导入模版属性定义, 如果我们定义为非string类型, 则需要用导入属性转化器`@ImportCell#converter`定义, 其中内置的转化器有(数字转换器: [ImportBigDecimalConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportBigDecimalConverter.java), 时间戳转换器(秒): [ImportDateConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportDateConverter.java), 枚举值转换器: [ImportEnumConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportEnumConverter.java)), 当然也可以自定义实现, 通过方法`com.github.youz.report.converter.ReportConverterLoader#putImportConverter`导入即可

- 导入模版

  需继承[BasicImportTemplate](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/bo/BasicImportTemplate.java), 示例可参考: [DynamicImportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/dynamic/DynamicImportTemplate.java), [GoodsImportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/goods/GoodsImportTemplate.java), [OrderImportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/order/OrderImportTemplate.java)


### 报表查询

GET http://localhost:8080/report/list?userId=1&businessType=1&pageNum=1&pageSize=10

```
{
    # 操作用户ID(用于列表显示时过滤筛选) 非必填
    "userId": 1,
    # 业务类型(根据不同的业务类型, 加载处理器) 非必填
    "businessType": 1
}
```

## **依赖**

将本项目编译打包, 通过maven方式引入到业务项目中, 示例: [**easy-report-server**](https://github.com/youz88/easy-report-server)

```java
<dependency>
    <groupId>com.github.youz</groupId>
    <artifactId>easy-report</artifactId>
    <version>1.0.0</version>
</dependency>
```