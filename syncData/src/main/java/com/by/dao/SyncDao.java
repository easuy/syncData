package com.by.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.by.generator.bean.SheinSyncData;
import com.by.service.SheInSyncDataService;
import com.by.utils.DBUtilDruid;

@Component
public class SyncDao {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncDao.class);
	
	@Autowired
    DBUtilDruid dbUtilDruid;
	
	public List<SheinSyncData> selectAll() {
		String sql = "select a.*,b.kfbh as materialcode,b.ysmc as materialcolorname,c.mc as suppliermaterialname,c.hk as width from CpJcdxmd as a " + 
				"left outer join(select bh,ys,ysmc,kfbh from Cpmx) as b on a.bpbh = b.bh and a.ysbh = b.ys " + 
				"left outer join(select bh,mc,hk from cp) as c on a.bpbh = c.bh  ";
		logger.info(sql);
        return dbUtilDruid.executeSelect(sql);
	}

}
