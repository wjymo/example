package com.wjy.xunwu.entity;

//import javax.persistence.*;

import lombok.Data;

/**
 * Created by 瓦力.
 */
//@Entity
//@Table(name = "subway")
@Data
public class Subway {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @Column(name = "city_en_name")
    private String cityEnName;

}
