package com.learn.service.impl;

import com.learn.dao.RecordDao;
import com.learn.entity.RecordEntity;
import com.learn.entity.SysUserEntity;
import com.learn.service.RecordService;
import com.learn.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("recordService")
public class RecordServiceImpl implements RecordService {
    @Autowired
    private RecordDao recordDao;

    @Autowired
    private SysUserService sysUserService;


    @Override
    public RecordEntity queryObject(Long id) {
        RecordEntity entity = recordDao.queryObject(id);

        if (this.sysUserService.queryObject(entity.getUser()) != null)
            entity.setSysUserEntity(this.sysUserService.queryObject(entity.getUser()));

        return entity;
    }

    @Override
    public List<RecordEntity> queryList(Map<String, Object> map) {
        List<RecordEntity> list = recordDao.queryList(map);
        for (RecordEntity entity : list) {
            if (this.sysUserService.queryObject(entity.getUser()) != null)
                entity.setSysUserEntity(this.sysUserService.queryObject(entity.getUser()));
        }
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return recordDao.queryTotal(map);
    }

    @Override
    public void save(RecordEntity record) {
        if (record.getUser() == 0) {
            for (SysUserEntity user : this.sysUserService.queryList(new HashMap<String, Object>())) {
                RecordEntity entity = new RecordEntity();
                entity.setName(record.getName());
                entity.setPrice(record.getPrice());
                entity.setUser(user.getUserId());
                entity.setTime(record.getTime());
                entity.setRemark(record.getRemark());
                recordDao.save(entity);
            }
        } else {
            recordDao.save(record);
        }


    }

    @Override
    public void update(RecordEntity record) {
        recordDao.update(record);
    }

    @Override
    public void delete(Long id) {
        recordDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        recordDao.deleteBatch(ids);
    }

}
