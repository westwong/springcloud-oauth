package com.k2future.oauth2server.service;

import com.k2future.oauth2server.common.constant.UsernameType;
import com.k2future.oauth2server.dao.UserDao;
import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.util.BeanUtils;
import com.k2future.oauth2server.util.IdGenerator;
import com.k2future.oauth2server.entity.User;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author West
 * @date create in 2019/9/2
 */
@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User save(User params) {
        Assert.hasText(params.getPassword(), "password is blank");
        Assert.hasText(params.getUsername(), "username is blank");

        boolean exists = userDao.existsByUsername(params.getUsername());
        Assert.notTrue(exists, "用户名已经存在。");

        params.setPassword(passwordEncoder.encode(params.getPassword()));
        if (params.getId() == null) {
            params.setId(IdGenerator.nextId());
        }
        if (userDao.findById(params.getId()).isPresent()) {
            Assert.throwE("user exist ");
        }
        if (StringUtils.isBlank(params.getUsernameType())){
            params.setUsernameType(UsernameType.TEXT);
        }
        return userDao.save(params);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public User update(User params) {
        Assert.notNull(params.getId(), "id is null?");
        if (StringUtils.isNotBlank(params.getPassword())) {
            params.setPassword(passwordEncoder.encode(params.getPassword()));
        }
        params.setUsername(null); //不修改username

        User user = userDao.findById(params.getId()).get();

        BeanUtils.copyBeanIgnoreNull(params, user);

        userDao.save(user);
        return user;
    }

    @Override
    @Transactional
    public User saveOrUpdate(User params) {

        if (params.getId() == null) {
            return this.save(params);
        } else {
            return this.update(params);
        }
    }

    @Override
    public boolean verifyPassword(Long id, String password) {
        if (id == null || StringUtils.isBlank(password)) {
            return false;
        }
        Optional<User> user = userDao.findById(id);
        if (user.isEmpty()){
            return false;
        }
        return passwordEncoder.matches(password, user.get().getPassword());
    }

    @Override
    public User findByIdOrUsername(Long id, String username) {
        Assert.isTrue(id != null || StringUtils.isNotBlank(username), "id  or  username  null ?");
        User user = null;
        if (StringUtils.isNotBlank(username)) {
            user = userDao.findByUsername(username);
        }
        if (id != null) {
            Optional<User> userOptional = userDao.findById(id);
            Assert.isTrue(userOptional.isPresent(), "id invalid !");
            user = userOptional.get();
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }
        userDao.deleteById(id);
    }

    @Override
    public User findByUsernameAndCreateIfAbsent(String username, String type) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            user = new User().setUsername(username).setPassword(username.substring(username.length() - 6)).setUsernameType(type);
            this.save(user);
        }
        return user;
    }
}
