# easy-report-starter

## 项目简介

**easy-report-starter**是一个基于easy-excel的开源项目, 旨在快速构建报表服务. 通过简化报表的导出与导入功能, 减少重复代码, 降低开发成本, 提高开发效率.

## 项目背景

在多数企业应用中, 报表服务是不可或缺的一环, 用于处理数据的导出与导入. 然而, 这些功能的初始构建往往伴随着大量重复代码, 后续的维护与业务耦合也增加了开发难度与时间成本. 因此, **easy-report-starter**应运而生, 旨在提供一个基础但强大的报表服务框架, 让开发者能够快速构建并维护报表功能.

## 特性

- 内置导出功能: 支持同步和异步导出.
- 云存储支持: 支持导出文件云存储上传.
- 可扩展性: 提供可扩展的导出处理器和导出模板.
- 字段选择与格式化: 支持导出字段选择和自定义格式化.
- 批量导入: 批量导入支持, 可限制最大行数.

## 快速开始

要将 **easy-report-starter** 集成到您的项目中, 请按照以下步骤操作:
1. 将本项目编译打包.
2. 通过 Maven 方式将以下依赖添加到您的业务项目中:

```xml
<dependency>
    <groupId>com.github.youz</groupId>
    <artifactId>easy-report</artifactId>
    <version>1.0.0</version>
</dependency>
```

- 示例项目: 您可以查看 [easy-report-server](https://github.com/youz88/easy-report-server) 以了解如何在实际项目中应用.

## 核心功能

### 默认配置

通过配置文件, 您可以灵活设置报表导出的各项参数, 如是否启用云存储上传、分页大小、任务切片最大值、异步任务执行条件等.

```yaml
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

**导出接口**

通过 POST 请求访问 `http://localhost:8080/report/export` 接口, 并传递以下 JSON 数据:

```json
{
    "userId": 1,              // 操作用户ID(用于列表显示时过滤筛选) 非必填
    "businessType": 1,        // 业务类型(根据不同的业务类型, 加载处理器) 必填
    "params": "{}",           // 查询参数(用于查询业务数据, 格式为json字符串, 与前端列表展示的查询参数一致) 必填
    "fieldNames": []          // 导出字段名称, 默认导出所有(需与前端约定, 用于控制哪些字段需要导出, 以及导出字段排序) 非必填
}
```

系统会根据查询数据的总量决定采用同步或异步方式导出报表, 详情见属性[ReportProperties#asyncTaskMaxSize](https://github.com/youz88/easy-report-starter/blob/dbeec8f9429f81546ee8039f6fa001e13cbaa73e/src/main/java/com/github/youz/report/config/ReportProperties.java#L73)

**业务导出处理器**

新增业务Handler, 继承[AbstractDataAssemblyExportHandler](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/handler/AbstractDataAssemblyExportHandler.java), 实现具体导出逻辑.

- 示例: [OrderExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportHandler.java), [GoodsExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportHandler.java)

**导出模版**

使用 [BasicExportTemplate](https://github.com/youz88/easy-report-starter/blob/main/src/main/java/com/github/youz/report/export/bo/BasicExportTemplate.java)类提供的方法实现表头与表体的初始化.

- 示例: [OrderExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportTemplate.java), [GoodsExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportTemplate.java)

**导出文件上传**

需将配置项`report.export.upload-cloud`设置为`true`, 并实现[UploadCloudData](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/data/UploadCloudData.java)接口, 即可完成文件上传到云存储.

### 报表导入

本地文件的表单提交方式. 使用 POST 请求访问 `http://localhost:8080/report/import-local` 接口, 并传递表单数据:

```
POST <http://localhost:8080/report/import-local?businessType=1&userId=1>
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="test.xlsx"
Content-Type: multipart/form-data

< /xx/xx/xx/test.xlsx
--WebAppBoundary--
```

引用云文件的方式. 使用 POST 请求访问 `http://localhost:8080/report/import-cloud` 接口，并传递以下 JSON 数据：

```json
{
    
    "userId": 1,                                  // 操作用户ID(用于列表显示时过滤筛选). 非必填
    "businessType": 1,                            // 业务类型(根据不同的业务类型, 加载处理器). 必填
    "loadFilePath": "http://aaa/bbb/ccc/dd.xlsx"  // 云端文件路径. 必填
}
```

**导入监听器**

只需要新增业务Listener, 继承 [AbstractPartSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractPartSuccessBusinessListener.java) 或 [AbstractAllSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractAllSuccessBusinessListener.java), 实现抽象方法即可处理导入业务.

- 示例: [DynamicImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/dynamic/DynamicImportListener.java), [GoodsImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/goods/GoodsImportListener.java), [OrderImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/order/OrderImportListener.java)

**导入注解与属性转换器**

使用 [@ImportCell](https://github.com/youz88/easy-report-starter/blob/main/src/main/java/com/github/youz/report/annotation/ImportCell.java) 等注解定义导入字段, 并通过属性转换器处理非字符串类型的数据.

- 示例注解: [@ImportLength](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportLength.java), [@ImportNull](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNull.java), [@ImportNumber](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNumber.java), [@ImportPhone](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportPhone.java)
- 示例转换器: [ImportBigDecimalConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportBigDecimalConverter.java), [ImportDateConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportDateConverter.java), [ImportEnumConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportEnumConverter.java)