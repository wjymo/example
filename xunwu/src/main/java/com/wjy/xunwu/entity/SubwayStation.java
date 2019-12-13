package com.wjy.xunwu.entity;

import lombok.Data;


/**
 * Created by 瓦力.
 */
@Data
//@Entity
//@Table(name = "subway_station")
public class SubwayStation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "subway_id")
    private Long subwayId;

    private String name;


}
