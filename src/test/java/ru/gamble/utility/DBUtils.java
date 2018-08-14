package ru.gamble.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbtqa.tag.datajack.exceptions.DataException;
import ru.sbtqa.tag.qautils.errors.AutotestError;
import ru.sbtqa.tag.qautils.properties.Props;

import java.sql.*;

import static ru.gamble.utility.Constants.STARTING_URL;

public class DBUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);

    public static Connection getConnection(){
        Connection con = null;
        String url, user, password;
        try{
            url = JsonLoader.getData().get(STARTING_URL).get("DB_REGISTRATION").get("DB_URL").getValue();
            user = JsonLoader.getData().get(STARTING_URL).get("DB_REGISTRATION").get("DB_USER").getValue();
            password = JsonLoader.getData().get(STARTING_URL).get("DB_REGISTRATION").get("DB_PASSWORD").getValue();
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
