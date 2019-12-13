package com.wjy.xunwu.entity;

import lombok.Data;


/**
 * Created by 瓦力.
 */
@Data
//@Entity
//@Table(name = "role")
public class Role {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(name = "user_id")
    private Long userId;
    private String name;

}
