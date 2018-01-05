package org.kxw.solr.entity;

/**
 * Created by kingsonwu on 18/1/4.
 */
public class Product {

    //商品编号
    private String pid;
    //商品名称
    private String name;
    //商品分类名称
    private String catalog;
    //商品价格
    private String price;

    //商品描述
    private String description;
    //图片
    private String picture;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalogName(String catalogName) {
        this.catalog = catalogName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
