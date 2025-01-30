# easy-report-starter

## [中文说明](README.md)

## Project Introduction

**easy-report-starter** is an open-source project based on easy-excel, aiming to quickly build report services. By simplifying the export and import functions of reports, it reduces redundant code, lowers development costs, and improves development efficiency.

## Project Background

In most enterprise applications, report services are an indispensable part, used for handling data exports and imports. However, the initial construction of these functions often involves a large amount of redundant code, and the subsequent maintenance and business coupling also increase the difficulty and time cost of development. Therefore, **easy-report-starter** was born, aiming to provide a basic but powerful report service framework that allows developers to quickly build and maintain report functions.

## Features

- Built-in Export Function: Supports synchronous and asynchronous exports.

- Cloud Storage Support: Supports uploading exported files to cloud storage.

- Extensibility: Provides extensible export processors and export templates.

- Field Selection and Formatting: Supports field selection for export and custom formatting.

- Batch Import: Supports batch import with the ability to limit the maximum number of rows.

## Quick Start

To integrate **easy-report-starter** into your project, please follow these steps:

1. Compile and package this project.

2. Add the following dependencies to your business project via Maven:

```xml
<dependency>
    <groupId>com.github.youz</groupId>
    <artifactId>easy-report</artifactId>
    <version>1.0.0</version>
</dependency>
```

- Example Project: You can view [easy-report-server](https://github.com/youz88/easy-report-server) to understand how to apply it in actual projects.

## Core Features

### Default Configuration

Through the configuration file, you can flexibly set various parameters for report export, such as whether to enable cloud storage upload, page size, maximum task slice value, and conditions for asynchronous task execution, etc.

```yaml
report:
  common:
    # Export file whether uploaded to cloud storage (after enabling, the com.github.youz.report.data.UploadCloudData interface must be implemented)
    upload-cloud: false
  export:
    # Default page size (used for paginated query of business data)
    page-size: 100
    # Maximum data value required for slicing task (if the query export total exceeds this value, the data will be split into multiple tasks)
    slices-task-max-size: 500000
    # Maximum data value required for asynchronous task execution (if the query export total exceeds this value, a scheduled task will be used for execution, otherwise the report file will be exported synchronously)
    async-task-max-size: 200
    # Interval time for asynchronous task execution, in milliseconds (to avoid continuous querying of a large number of asynchronous tasks causing pressure on the database, the query sleep interval time can be configured)
    async-task-sleep-time: 100
    # Scan for pending execution export tasks
    scan-wait-exec-cron: 0 0/2 * * * ?
    # Scan for pending upload export tasks (only for tasks with upload failed status)
    scan-wait-upload-cron: 0 0/3 * * * ?
  imports:
    # Number of rows processed in batches
    batch-row: 100
    # Maximum row limit
    limit-max-row: 20000
```

### Report Export

**Export Interface**

Access the `http://localhost:8080/report/export` interface via a POST request and pass the following JSON data:

```json
{
  "userId": 1,        // Operation user ID (used for filtering and sorting in lists) Optional
  "businessType": 1,  // Business type (load processors based on different business types) Required
  "params": "{}",     // Query parameters (used for querying business data, in the format of a JSON string, consistent with the query parameters displayed in the front-end list) Required
  "fieldNames": []    // Exported field names, default to export all (to be agreed upon with the front-end, used to control which fields need to be exported and the sorting of exported fields) Optional
}
```

The system will decide to export reports synchronously or asynchronously based on the total amount of query data, for details see property [ReportProperties#asyncTaskMaxSize](https://github.com/youz88/easy-report-starter/blob/dbeec8f9429f81546ee8039f6fa001e13cbaa73e/src/main/java/com/github/youz/report/config/ReportProperties.java#L73)

**Business Export Processor**

New business Handler, inherits [AbstractDataAssemblyExportHandler](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/export/handler/AbstractDataAssemblyExportHandler.java) implements specific export logic.

- Example: [OrderExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportHandler.java), [GoodsExportHandler](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportHandler.java)

**Export Template**

Initialize the header and body of the table using the methods provided by the [BasicExportTemplate](https://github.com/youz88/easy-report-starter/blob/main/src/main/java/com/github/youz/report/export/bo/BasicExportTemplate.java) class.

- Example: [OrderExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/order/OrderExportTemplate.java), [GoodsExportTemplate](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/export/goods/GoodsExportTemplate.java)

**Export file upload**

You need to set the configuration item `report.export.upload-cloud` to `true`, and implement the [UploadCloudData](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/data/UploadCloudData.java) interface to complete the file upload to cloud storage.

### Report Import

Local file form submission method. Use POST request to access the `http://localhost:8080/report/import-local` interface and pass form data:

```
POST <http://localhost:8080/report/import-local?businessType=1&userId=1>
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="test.xlsx"
Content-Type: multipart/form-data

< /xx/xx/xx/test.xlsx
--WebAppBoundary--
```

The method of referencing cloud files. Access the `http://localhost:8080/report/import-cloud` interface using a POST request and pass the following JSON data:

```json
{
  "userId": 1,                                  // Operation User ID (used for filtering and sorting in list display). Optional
  "businessType": 1,                            // Business Type (load processors based on different business types). Required
  "loadFilePath": "http://aaa/bbb/ccc/dd.xlsx"  // Cloud file path. Required
}
```

**Import Listener**

Only a new business Listener needs to be added, inheriting from [AbstractPartSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractPartSuccessBusinessListener.java) or [AbstractAllSuccessBusinessListener](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/imports/listener/AbstractAllSuccessBusinessListener.java), and implementing the abstract method to handle the import business.

- Example: [DynamicImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/dynamic/DynamicImportListener.java), [GoodsImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/goods/GoodsImportListener.java), [OrderImportListener](https://github.com/youz88/easy-report-server/blob/main/src/main/java/com/github/youz/server/business/imports/order/OrderImportListener.java)

**Import Annotations and Attribute Transformers**

Use the annotations such as [@ImportCell](https://github.com/youz88/easy-report-starter/blob/main/src/main/java/com/github/youz/report/annotation/ImportCell.java) to define import fields, and process non-string data through property converters.

- Sample Annotation: [@ImportLength](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportLength.java), [@ImportNull](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNull.java), [@ImportNumber](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportNumber.java), [@ImportPhone](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/annotation/ImportPhone.java)
- Sample Converter: [ImportBigDecimalConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportBigDecimalConverter.java), [ImportDateConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportDateConverter.java), [ImportEnumConverter](https://github.com/youz88/easy-report/blob/main/src/main/java/com/github/youz/report/converter/imports/ImportEnumConverter.java)