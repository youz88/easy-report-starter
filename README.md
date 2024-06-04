# easy-report

## 项目简介

这是一个基于easy-excel的开源项目, 用于快速构建报表.

## 项目背景

大多公司都需要报表服务用来处理导出、导出功能, 实际上这些代码初始构建时大多都是重复的, 后续的维护又需与业务耦合, 需要大量的时间才能完成. 所以当前项目的目标是构建一个基础报表服务, 可以快速构建报表, 减少重复代码, 降低开发成本.

## 核心功能

### 报表导出

- 确认配置文件是否需要修改(默认配置)

    ```text
    report:
        export:
        # 默认分页大小(用于分页查询业务数据)
        page-size: 100
        # 切片子任务所需数据最大值(如果查询导出总数超过该值, 则将数据切分成多个任务)
        slices-task-max-size: 500000
        # 异步任务执行所需数据最大值(如果查询导出总数超过该值, 将会使用定时任务执行, 否则同步导出报表文件)
        async-task-max-size: 200
        # 异步任务执行间隔时间, 单位毫秒(为避免大量异步任务持续查询对数据库造成压力, 所以可配置查询睡眠间隔时间)
        async-task-sleep-time: 100
        # 导出文件是否上传到云存储(开启后需实现com.github.youz.report.data.UploadCloudData接口)
        upload-cloud: false
    
    ```

- 导出接口

  POST http://localhost:8080/report/export

    ```text
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

- 业务导出处理器

  当我们处理具体的导出业务时, 只需要新增业务Handler, 继承[AbstractDataAssemblyExportHandler](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/handler/AbstractDataAssemblyExportHandler.java), 实现抽象方法即可.

  示例可参考: [OrderExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/order/OrderExportHandler.java)

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

  示例可参考: [OrderTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/order/OrderTemplate.java), [GoodsTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/goods/GoodsTemplate.java)

  关于导出模版对象, 其中内置了部份自定义注解, 用于更方便的处理导出字段格式化.

  格式化时间: [@DateTimeFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DateTimeFormat.java)

  默认值设置: [@DefaultValueFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DefaultValueFormat.java)

  枚举值转换: [@EnumFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/EnumFormat.java)

  金额格式化: [@MoneyFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/MoneyFormat.java)


- 导出文件上传

  需将配置项`report.export.upload-cloud`设置为true, 并实现接口[UploadCloudData](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/data/UploadCloudData.java), 即可完成文件上传到云存储.


### 报表查询

GET http://localhost:8080/report/list?userId=1&businessType=1&pageNum=1&pageSize=10

```text
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