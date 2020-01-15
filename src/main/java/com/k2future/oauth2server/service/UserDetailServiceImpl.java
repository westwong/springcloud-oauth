package com.k2future.oauth2server.service;

import com.k2future.oauth2server.dao.UserDetailDao;
import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.entity.UserDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author West
 * @date create in 2020/1/7
 */
@Service("userDetailService")
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public UserDetail getByIdAndCreateIfAbsent(Long id) {
        Optional<UserDetail> optional = userDetailDao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        UserDetail userDetail = new UserDetail().setId(id).setName("你的名字").setCreateTime(LocalDateTime.now());
        userDetailDao.save(userDetail);
        return userDetail;
    }

    @Override
    public UserDetail getById(Long id) {
        Assert.notNull(id, "id null ?");
        Optional<UserDetail> optional = userDetailDao.findById(id);
        return optional.orElse(null);
    }

    @Override
    public void updateById(UserDetail params) {
        Assert.notNull(params.getId(), "id null ?");
        userDetailDao.save(params);
    }
}
