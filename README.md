## 背景

需要在编辑文章的时候，能很好的管理相关素材，如图片和附件等。一直以来都是用编辑器的上传功能，造成很多素材的冗余，比如上传错了图片，
错误图片无法便捷的删除，所以需要一个能够方便管理的素材库。现在不少存储都放在云端了，比如阿里云的OSS，腾云云存储等等。
所以就在spring-elfinder项目的基础上，升级了springboot的版本，增加了阿里云OSS特性，并解决部分小问题，比如目录树
显示不完整问题。

## 介绍 

基于SpringBoot+elFinder搭建文件管理器，支持本地磁盘和阿里云，提供TinyMCE5集成范例。

## 环境搭建

| 软件 | 版本  | 功能|   地址|
| ---- | ----- |----- |----- |
|   SpringBoot|  2.1.0.RELEASE |  全能框架   | https://spring.io/projects/spring-boot/  |
|   elFinder| 2.1.53 |  Web文件管理器|  https://studio-42.github.io/elFinder/ |
|   TinyMCE5| 5.2.0 |  在线编辑器|  https://www.tiny.cloud/ |

## 感谢项目

在创建过程中，主要参考以下开源项目

| 软件 |   地址|
| ---- | ----- |
|   elfinder-java-connector|  https://github.com/trustsystems/elfinder-java-connector  |
|   spring-elfinder|  https://github.com/konglinghai123/spring-elfinder  |

## 演示Demo
[elFinder Demo](http://fm.moorelife.cn/fm.html)
<br>
[TinyMCE5 Demo](http://fm.moorelife.cn/tinymce.html)

## 项目截图

### 默认主题
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/1.png" width="80%">
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/2.png" width="80%">
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/3.png" width="80%">

### 深色主题
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/4.png" width="80%">
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/5.png" width="80%">
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/6.png" width="80%">
 
#### 功能
- 文件以及文件夹新增，删除，移动，重名
- 在线打包文件
- 文件下载、上传 
- 在线预览文件，图片
- 在线处理图片，文件
- 支持目录上传
- 支持zip tar Gzip 的在线解压和压缩文件夹
- 支持多种文本格式的高亮显示和在线编辑
- 支持在线文件预览
- 支持文件夹权限设置
- 支持国际化
- 支持阿里云OSS
- 支持本地磁盘与阿里云OSS之间的拖拽
- 集成至TinyMCE5

#### 配置 application.yml
```
file-manager:
     thumbnail:
        width: 80 # 缩略图宽
     volumes:
        - Node: # 可配置多个节点
          source: fileSystem # 暂时只支持本地文件系统
          alias: 测试目录 # 目录别名
          path: /Users/Van/Desktop/test # 映射目录
          isDefault: true # 是否默认打开
          locale:
          constraint:
            locked: false # 文件夹是否锁定
            readable: true # 是否可读
            writable: true # 是否可写
        - Node:
          source: aliyunoss # 阿里云
          alias: 阿里云 # 目录别名
          path: moore-test1 #bucketName
          isDefault: false # 是否默认打开
          locale:
          constraint:
            locked: false # 文件夹是否锁定
            readable: true # 是否可读
            writable: true # 是否可写
          aliyunDriverConfig:
            endpoint: "oss-cn-beijing.aliyuncs.com" #改成阿里云的Endpoint地址
            #bindedDomain: "***.yourdomain.com"  #如果阿里云OSS绑定了域名，可以在这里设置
            accessKeyId: ENC(*******) #阿里云的accessKeyId 配置文件敏感字段采用jasypt加密
            accessKeySecret: ENC(******) #阿里云的accessKeySecret 配置文件敏感字段采用jasypt加密
```

#### 加密问题
配置文件中敏感字段的的加密，可以用web项目中PwdUitl.java工具来创建。如下图：
<img src="https://moore-files.oss-cn-beijing.aliyuncs.com/common/7.png" width="50%">

#### 访问
http://127.0.0.1:8080