# easy-report

## 项目简介
这是一个基于easy-excel的开源项目, 用于快速构建报表.

## 项目背景
大多公司都需要报表服务用来处理导出、导出功能, 实际上这些代码初始构建时大多都是重复的, 后续的维护又需与业务耦合, 需要大量的时间才能完成. 所以当前项目的目标是构建一个基础报表服务, 可以快速构建报表, 减少重复代码, 降低开发成本.

## 核心功能
- 报表导出
    - 配置文件
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
      ```text
      POST http://localhost:8080/report/export
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
  
      示例可参考[OrderExportHandler](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/handler/order/OrderExportHandler.java)
      ```java
      public class A extends AbstractDataAssemblyExportHandler{
           @Override
           protected long queryTotal(String queryParam) {
               // 查询导出总数, 业务服务需提供接口查询导出总数
               return 0;
           }
        
           @Override
           protected ExportHead handleHead(ExportContext context) {
               // 导出表头
               return null;
           }
        
           @Override
           protected ExportData handleData(ExportContext context) {
               // 导表体数据
               return null;
           }
        
           @Override
           public BusinessType businessType() {
               // 业务类型
               return null;
           }
      
           @Override
           protected String generateSheetName(ExportContext context) {
               // 自定义导出sheet名称
               return super.generateSheetName(context);
           }
        
           @Override
           protected int getPageSize(ExportProperties exportProperties) {
               // 自定义查询分页大小
               return super.getPageSize(exportProperties);
           }
        
           @Override
           protected ExportData handleEndData(ExportContext context) {
               // 导出表尾数据
               return super.handleEndData(context);
           }
      } 
      ```
    - 导出模版
        
        参考示例[OrderBO](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/bo/order/OrderBO.java),
        通过注解@ExcelProperty, 指定导出字段表头名称, 其中内置了部份自定义注解, 用于处理导出字段格式化.

        格式化时间: [@DateTimeFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DateTimeFormat.java)       
        默认值设置: [@DefaultValueFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/DefaultValueFormat.java)       
        枚举值转换: [@EnumFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/EnumFormat.java)       
        金额格式化: [@MoneyFormat](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/MoneyFormat.java)       
        ```java
        public static class ExportTemplate {
    
            @ExcelProperty("序号")
            private String index;
    
            @ExcelProperty("订单号")
            private String orderNo;
    
            @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
            @ExcelProperty("下单时间")
            private Integer orderTime;
    
            @DefaultValueFormat("未知")
            @ExcelProperty("订单状态")
            private String statusName;
    
            @MoneyFormat("%.2f")
            @ExcelProperty("订单金额")
            private Long amount;
        }
        ```
    
- 报表任务查询
