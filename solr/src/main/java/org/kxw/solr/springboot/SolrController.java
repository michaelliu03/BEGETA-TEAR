package org.kxw.solr.springboot;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.kxw.solr.entity.Product;
import org.kxw.solr.service.ProductService;
import org.kxw.solr.vo.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kingsonwu on 18/1/4.
 */
@RestController
@RequestMapping("/springboot/solr")
public class SolrController {

    @Autowired
    private SolrClient client;

    @Autowired
    private ProductService productService;

    @RequestMapping("/testSolr")
    public String testSolr() throws IOException, SolrServerException {
        SolrDocument document = client.getById("test", "fe7a5124-d75b-40b2-93fe-5555512ea6d2");
        System.out.println(document);
        return document.toString();
    }

    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public ResultModel listProducts() throws IOException, SolrServerException {
        ResultModel rm = productService.queryProductListBySolr("kxw", "", "", "1", 1);
        return rm;
    }

    @RequestMapping(value ="/product/save", method = RequestMethod.GET)
    public String saveProduct(Product product) throws IOException, SolrServerException, SQLException {

        product = new Product();
        product.setName("kxwkxwkxw");
        product.setCatalogName("kxw");
        product.setDescription("====");
        product.setPid(UUID.randomUUID().toString().replace("-", ""));
        product.setPrice("5.34");
        //product.setPicture("");

        return productService.addProduct(product);
    }

}
