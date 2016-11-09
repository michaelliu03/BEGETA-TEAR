+ `bin/solr start -e cloud -noprompt`
+ <http://localhost:8983/solr/#/>

##### Indexing Data
+ `bin/post -c gettingstarted docs/`
+ `bin/post -c gettingstarted example/exampledocs/*.xml`
+ `bin/post -c gettingstarted example/exampledocs/books.json`
+ `bin/post -c gettingstarted example/exampledocs/books.csv`

+ <http://localhost:8983/solr/#/gettingstarted/query>
+ `curl "http://localhost:8983/solr/gettingstarted/select?indent=on&q=*:*&wt=json"`
+ `curl "http://localhost:8983/solr/gettingstarted/select?wt=json&indent=true&q=foundation"`

##### Cleanup
`bin/solr stop -all ; rm -Rf example/cloud/`


---

<https://cwiki.apache.org/confluence/display/solr/Apache+Solr+Reference+Guide>

---

solr 从零学习开始(1)--整体了解solr
<http://www.aboutyun.com/thread-7017-1-1.html>

#### 项目使用实例

<http://www.cnblogs.com/wang-meng/p/5819792.html>

当我们在给商品上架的时候, 将商品信息update 到mysql数据库中的bbs_product表中, 然后同样的将相应的信息 添加到Solr库中

<http://blog.sina.com.cn/s/blog_12f2b61090102x05f.html>

##### 架构分析
+ 一般搜索功能开发最少需要三台服务器
1. Web应用服务器：
表现层：接收搜索条件，并返回渲染的视图
业务层：使用solrj调用solr服务器的服务
如果数据库数据发生变更，要更新数据库并更新索引库
持久层：查询数据
2. 数据库服务 
3. Solr服务器

使用Solr索引MySQL数据
<http://www.cnblogs.com/luxiaoxun/p/4442770.html>


