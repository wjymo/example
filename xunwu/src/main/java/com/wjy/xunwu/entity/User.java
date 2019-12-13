package com.wjy.xunwu.entity;

import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by 瓦力.
 */
@Data
//@Entity
//@Table(name = "user")
public class User /*implements UserDetails*/ {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String email;

//    @Column(name = "phone_number")
    private String phoneNumber;

    private int status;

//    @Column(name = "create_time")
    private Date createTime;

//    @Column(name = "last_login_time")
    private Date lastLoginTime;

//    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    private String avatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @Transient
//    private List<GrantedAuthority> authorityList;


}
