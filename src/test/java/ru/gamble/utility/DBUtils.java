package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gamble.pages.RegistrationPages.PassportDataPage;
import ru.sbtqa.tag.datajack.exceptions.DataException;

import java.sql.*;

public class DBUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PassportDataPage.class);


    public static Connection getConnection(){
        Connection con = null;
        String url, user, password;
        //url = "jdbc:mysql://stage-bet-haproxy.tsed.orglot.office:3307/gamebet?characterEncoding=UTF8";
        url = "jdbc:mysql://stage-bet-haproxy.tsed.orglot.office:3307";
        user ="webdev_user";
        password ="dove8lower3Haunt";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

        }catch (ClassNotFoundException ex1){
            LOG.error("Ошибка! Не найден класс драйвера::" + ex1.getMessage());
            System.exit(1);
        } catch (SQLException ex2) {
            LOG.error("Ошибка! Не установлено соединение с базой::" + ex2.getMessage());
            System.exit(2);
        }
        return con;
    }

    public  static void closeAll(Connection con, PreparedStatement ps, ResultSet rs){
        try {
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            if(con != null) con.close();
        }catch (SQLException sqle){
            LOG.error("Ошибка! Соединение на закрылось", sqle.getMessage());
        }
    }

}
