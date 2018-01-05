package org.kxw.solr.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.kxw.solr.db.MysqlDB;
import org.kxw.solr.entity.Product;
import org.springframework.stereotype.Repository;

/**
 * Created by kingsonwu on 18/1/5.
 */
@Repository
public class ProductDao {

    public void addProduct(Product product) throws SQLException {
        StringBuffer fieldBuffer = new StringBuffer();
        StringBuffer valueBuffer = new StringBuffer();

        if (product.getPid() != null) {
            fieldBuffer.append("`pid`,");
            valueBuffer.append("'" + product.getPid() + "',");
        }
        if (product.getName() != null) {
            fieldBuffer.append("`name`,");
            valueBuffer.append("'" + product.getName() + "',");
        }
        if (product.getCatalog() != null) {
            fieldBuffer.append("`catalog`,");
            valueBuffer.append("'" + product.getCatalog() + "',");
        }
        if (product.getPrice() != null) {
            fieldBuffer.append("`price`,");
            valueBuffer.append("'" + product.getPrice() + "',");
        }
        if (product.getDescription() != null) {
            fieldBuffer.append("`description`,");
            valueBuffer.append("'" + product.getDescription() + "',");
        }
        if (product.getPicture() != null) {
            fieldBuffer.append("`picture`,");
            valueBuffer.append("'" + product.getPicture() + "',");
        }

        String insertSql = "INSERT INTO `product`(" + fieldBuffer.substring(0, fieldBuffer.length() - 1) + ") VALUES ("
            + valueBuffer.substring(0, valueBuffer.length() - 1) + ")";

        Connection connection = MysqlDB.getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate(insertSql);
        stmt.close();

    }

}
