package org.kxw.solr.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.kxw.solr.dao.ProductDao;
import org.kxw.solr.entity.Product;
import org.kxw.solr.vo.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kingsonwu on 18/1/4.
 */
@Service
public class ProductService {

    @Autowired
    private SolrClient client;

    @Autowired
    private ProductDao productDao;

    /**
     * http://blog.sina.com.cn/s/blog_12f2b61090102x05f.html
     */
    public ResultModel queryProductListBySolr(String queryString,
                                              String catalogName, String price, String sort, Integer page) throws
        SolrServerException, IOException {
        //创建SolrQuery
        SolrQuery query = new SolrQuery();
        //设置查询条件
        if (StringUtils.isNotEmpty(queryString)) {
            query.setQuery(queryString);
        } else {
            query.setQuery("*:*");
        }
        //设置过滤条件
        //分类名称
        if (StringUtils.isNotEmpty(catalogName)) {
            query.addFilterQuery("product_catalog:" + catalogName);
        }
        //价格区间
        if (StringUtils.isNotEmpty(price)) {
            String[] ss = price.split("-");
            if (ss.length == 2) {
                query.addFilterQuery("product_price:[" + ss[0] + " TO " + ss[1] + "]");
            }
        }
        //设置排序 solr can not sort on multivalued field:
       /* if ("1".equals(sort)) {
            query.setSort("product_price", ORDER.desc);
        } else {
            query.setSort("product_price", ORDER.asc);
        }*/
        //设置分页信息,每页20个商品
        if (page == null) { page = 1; }
        query.setStart((page - 1) * 20);
        query.setRows(20);
        //设置默认域
        //query.set("df", "product_keywords");
        //设置高亮
        query.setHighlight(true);
        query.addHighlightField("product_name");
        query.setHighlightSimplePre("<br>");
        query.setHighlightSimplePost("</br>");
        //通过server查询，并返回结果
        QueryResponse response = client.query("gettingstarted", query);
        //获得查询结果
        SolrDocumentList results = response.getResults();
        //匹配出的所有商品记录
        long count = results.getNumFound();
        //获取高亮信息
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        List productList = new ArrayList<>();
        Product pro;
        for (SolrDocument solrDocument : results) {
            pro = new Product();
            //商品ID
            pro.setPid(solrDocument.get("id").toString());
            //商品名称
            List list = highlighting.get(solrDocument.get("id")).get("product_name");
            if (list != null) {
                pro.setName((String)list.get(0));
            } else {
                pro.setName(solrDocument.get("product_name").toString());
            }
            //商品分类名称
            pro.setCatalogName(solrDocument.get("product_catalog").toString());
            //商品价格
            pro.setPrice(solrDocument.get("product_price").toString());
            pro.setDescription(solrDocument.get("product_description").toString());

            /** 其他信息如果需要,通过pid从mysql取 */

            productList.add(pro);
        }
        ResultModel rm = new ResultModel();
        rm.setProductList(productList);
        rm.setRecordCount(count);
        rm.setCurPage(page);
        //总页面
        int pageCount = (int)(count / 20);
        if (count > 0) { pageCount++; }
        rm.setPageCount(pageCount);
        return rm;
    }

    /**
     * 当我们在给商品上架的时候, 将商品信息update 到mysql数据库中的bbs_product表中, 然后同样的将相应的信息 添加到Solr库中.
     */
    public String addProduct(Product p) throws SQLException {

        productDao.addProduct(p);

        // 保存商品信息到Solr服务器
        SolrInputDocument doc = new SolrInputDocument();
        //ID
        doc.setField("id", p.getPid());
        doc.setField("product_name", p.getName());
        doc.setField("product_description", p.getDescription());
        doc.setField("product_catalog",p.getCatalog());
        doc.setField("product_price", p.getPrice());

        /**
         * 根据Solr源码发现，solr对排序段Field是有要求的，主要有两点：
         1  field必须是索引的field。
         2 field不能是multivalued 多个值的。

         TODO 怎么更新solr的schema文件,通过zookeeper?
         */

        try {
            client.add(doc);
            client.commit("gettingstarted");
            //client.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return p.getPid();
    }
}
