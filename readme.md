# 此案例为文件上传的一个DEMO

## commons-fileupload进行文件上传下载以及二维码分享

## 使用ftp进行文件上传下载
### 需要修改cn.rayfoo.utils.FtpUtil中的下述内容
    HOST 主机地址
    PORT 端口
    USERNAME 用户名
    PASSWORD 密码
    
### 需要引入FtpUtil和QRCodeUtil工具类
## 使用阿里云OSS进行文件上传下载以及二维码分享
    需要修改aliyun.properties中的属性
    aliyun.AccessKeyID=
    aliyun.AccessKeySecret=
    aliyun.Buckets=
    aliyun.EndPoint=https://oss-cn-beijing.aliyuncs.com
    aliyun.prefix=prefix/