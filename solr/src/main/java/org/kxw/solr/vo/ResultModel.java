package org.kxw.solr.vo;

import java.util.List;

import org.kxw.solr.entity.Product;

/**
 * Created by kingsonwu on 18/1/4.
 */
public class ResultModel {

    //商品列表
    private List<Product> productList;
    //商品数
    private Long recordCount;
    //总页数
    private int pageCount;
    //当前页数
    private int curPage;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }
}
