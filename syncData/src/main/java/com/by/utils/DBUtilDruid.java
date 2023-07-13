package com.by.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.by.generator.bean.SheinSyncData;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
 
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
@Component
public class DBUtilDruid {
    @Autowired
    private Environment environment;
 
    @Bean(name = "getDataSource")
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        String surl = environment.getProperty("spring.datasource.url");
        System.out.println(surl);
        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        return dataSource;
    }
 
    //关闭资源
    public void close(Statement ps, Connection conn){
        try {
            if (ps != null && !ps.isClosed()) {
                ps.isClosed();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }catch (SQLException ex){
            System.out.println("[Error]" + ex.getMessage());
        }
    }
 
    public List<SheinSyncData> executeSelect(String p_sql){
        String result="";
        Connection conn = null;
        Statement stmt = null;
        List<SheinSyncData> SheinSyncList = new ArrayList<SheinSyncData>();
        try {
            DataSource ds = (DataSource)SpringContextUtil.getBean("getDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(p_sql);
            while (rs.next()){
            	String materialCode = rs.getString("materialcode");
            	System.out.println(materialCode);
            	if(StringUtils.isAllEmpty(materialCode)) {
            		continue;
            	}
            	SheinSyncData SheinSyncData = new SheinSyncData();
            	SheinSyncData.setRfidcode("");
            	SheinSyncData.setUnit("KG");//unit单位  写死KG 
            	SheinSyncData.setRepertorynum(1);//库存数量 写死1
            	SheinSyncData.setDyelotnumber(rs.getString("gh"));//色号
            	SheinSyncData.setMaterialcolorcode(rs.getString("ysbh"));//色号
            	SheinSyncData.setNumbercode(rs.getString("Tm"));//条编码
            	SheinSyncData.setWarehousename(rs.getString("cg"));//仓库名称
            	SheinSyncData.setWarehouseaddr(rs.getString("cw"));//仓库地址
            	SheinSyncData.setWidth(rs.getString("width"));
            	SheinSyncData.setSuppliermaterialname(rs.getString("suppliermaterialname"));
            	SheinSyncData.setMaterialcolorname(rs.getString("materialcolorname"));
            	SheinSyncData.setMaterialcode(materialCode);
                SheinSyncList.add(SheinSyncData);
            }
            stmt.close();
            return SheinSyncList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(stmt, conn);
        }
        return SheinSyncList;
    }
    
    public String executeScalar(String p_sql){
        String result="";
        Connection conn = null;
        Statement stmt = null;
        try {
            DataSource ds = (DataSource)SpringContextUtil.getBean("getDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(p_sql);
            if (set.next()){
                result = set.getString(1);
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(stmt, conn);
        }
        return "";
    }
 
    public boolean executeSQL(String sql){
        Connection conn = null;
        Statement stmt = null;
        try {
            DataSource ds = (DataSource)SpringContextUtil.getBean("getDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
            return true;
        }
        catch (Exception e){
            return  false;
        }
        finally {
            close(stmt, conn);
        }
    }
}