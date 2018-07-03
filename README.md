## ebay分布式爬虫
主要是对ebay指定关键词产品爬取所开发的分布式ebay爬虫，主要使用到的技术是akka 分布式框架集，有akka cluster
，akka ator与akka http
接口请求方式: post localhost:9001/crawl/keyword kd = keyword 使用自己的关键词代替即可

## 性能测试
存储：分表 一张为搜索页面数据，一张为详情页面的数据，数据库为mysql，jsoup异步抓取源码，可写方法随机更改useragent。
速度：每分钟 单表5k-7k左右的数据量 带宽3.5m/s 两个节点运行

## web界面
整合了一个web界面，使用了python3 aiohttp开发 展示抓取数据


## 运行方式
sbt 'runMain sample.crawl.ebay.EbayCrawlBackend 2551' # 集群节点
sbt 'runMain sample.crawl.ebay.EbayCrawlBackend 2552' 
sbt 'runMain sample.crawl.ebay.EbayCrawlBackend 2553'  
sbt run  # 启动http接口
