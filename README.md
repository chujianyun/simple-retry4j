# simple-retry4j
一个简单的Java重试和分批执行工具类

maven配置
[中央仓库地址](https://mvnrepository.com/artifact/com.github.chujianyun/simple-retry4j)  

```xml
<!-- https://mvnrepository.com/artifact/com.github.chujianyun/simple-retry4j -->
<dependency>
    <groupId>com.github.chujianyun</groupId>
    <artifactId>simple-retry4j</artifactId>
    <version>1.1.1</version>
</dependency>
```

# 核心功能
提供重试和分批执行工具类，
支持传入操作、重试次数和延时时间。
支持定义不再重试的异常和条件。
支持Builder模式参数设置。

# 主要应用场景
## 1 重试工具类
SimpleRetryUtil

适用于对任务丢失要求不高的场景。
此工具类只适合单机版，因此任务丢失要求高的场景建议用中间件，如缓存中间件redis或者消息中间件。
 
 主要场景如下：
- 乐观锁重试
- 上游业务保证重试的场景且没有其他好的重试机制
- 需要轮询直到得到想要的结果的场景
- 其他需要控制重试时间间隔的场景

## 2 分批执行工具类
BatchExecUtil
适用于数据量较大时分批执行的场景。
支持每个分批指定数量，支持指定分批执行的间隔，支持重试策略。

# 关于测试
Junit测试中的所有都已跑通，大家如果改进代码，可以新增单元测试验证。