package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author yuelong.liang
 */
@Slf4j
public class DBUtils {

    private static final DruidDataSource dataSource;

    static {
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("jdbc.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
    }

    public static DataSource getDataSource() throws IOException {
        return dataSource;
    }

    public static void createTable(String sql) throws SQLException {
        DruidPooledConnection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        log.info("执行 sql: " + sql);
        try {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        connection.close();
        log.info("sql 执行完成");
    }

}
