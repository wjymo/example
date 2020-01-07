package com.wjy.security;

import com.wjy.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.wjy.entity.User user = userDAO.getByUsername(username);
        SecurityUser securityUser =new SecurityUser(username,user.getPassword()
                , AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_USER"));
        securityUser.setId(user.getId());
        securityUser.setImg(user.getFaceImage());
        return securityUser;
    }

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("111");
        System.out.println(encode);
    }
}
