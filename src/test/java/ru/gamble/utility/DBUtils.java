package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.qautils.errors.AutotestError;

import java.sql.*;

public class DBUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);

    public static Connection getConnection(){
        Connection con = null;
        String url, user, password;
//        Регистрационная база на stage
//        url = "jdbc:mysql://stage-bet-haproxy.tsed.orglot.office:3307";
//        user ="webdev_user";
//        password ="dove8lower3Haunt";
        try{
            url = JsonLoader.getData().get("db-registration").get("url").getValue();
            user = JsonLoader.getData().get("db-registration").get("user").getValue();
            password = JsonLoader.getData().get("db-registration").get("password").getValue();

            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

        } catch (DataException e){
          throw new AutotestError("Не найден файл::" + e.getMessage());
        } catch (ClassNotFoundException ex1){
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
