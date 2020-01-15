package com.k2future.oauth2server.service;

import com.k2future.oauth2server.dao.RoleDao;
import com.k2future.oauth2server.dao.UserDao;
import com.k2future.oauth2server.entity.Role;
import com.k2future.oauth2server.entity.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author West
 * @date create in 2019/9/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Autowired
    RoleDao roleDao;
    @Autowired
    UserDao userDao;


    @Test
    public void loadUserByUsername() {
    }

    @Test
    public void save() {
        User admin = new User().setPassword("123456").setUsername("admin1");
        Optional<Role> role = roleDao.findById(1L);
//        admin.setRoleList(List.of(new Role().setId(1L)));
        userService.save(admin);
    }

    @Test
    public void test01() {
        Role role = new Role().setId(1L).setRoleName("admin");
        roleDao.save(role);
    }

    @Test
    public void test02() {
        if (userDao.existsByUsername("admin")) {
            System.out.println("!!!!!!!!!!1");
        }
    }

    @Test
    public void test03() {
        User user = userDao.findByUsername("admin");
        userDao.save(user);

    }
}