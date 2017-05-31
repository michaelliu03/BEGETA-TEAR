lucene,solr,elasticsearch,ansj,sphix


---

### elk (mac)
<https://my.oschina.net/itblog/blog/547250>

#### logstash
+ `wget https://download.elastic.co/logstash/logstash/logstash-2.4.0.tar.gz`



    
#### elasticsearch
+ `bin/elasticsearch`
+ `http://localhost:9200/`启动Elasticsearch
+ 安装Marvel(Elasticsearch的可视化管理和监控工具) <https://www.elastic.co/downloads/marvel>
+ `curl http://127.0.0.1:9200/_nodes/_local/plugins`  查看节点上的插件列表，检查列表中是否含有 marvel
+ `/bin/plugin install mobz/elasticsearch-head`
+ `http://localhost:9200/_plugin/head/`

#### kibana


---
ES完全能满足10亿数据量，5k吞吐量的常见搜索业务需求


