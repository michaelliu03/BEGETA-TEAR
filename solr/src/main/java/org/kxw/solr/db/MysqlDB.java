package org.kxw.solr.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MysqlDB {

    private static Connection conn;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/solr_test?user=root&password=123456");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 产生表达式
            stmt = conn.createStatement();
            // 指定sql语句
            String sql = "select * from dept";
            // 执行查询，结果存储在结果集对象rs中
            rs = stmt.executeQuery(sql);
            // 遍历结果集
            while (rs.next()) {
                // 将结果集对象rs中的每条数据取出，进行相应处理
                System.out.println(rs.getString("dname"));
            }

        }catch (SQLException e) {
            System.out.println("执行SQL语句过程中出现了错误。。。");
            e.printStackTrace();
        } finally {
            // 最后关闭资源（后打开的先关闭）
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                System.out.println("关闭资源时出现了错误。。。");
                e.printStackTrace();
            }
        }
    }

}
  