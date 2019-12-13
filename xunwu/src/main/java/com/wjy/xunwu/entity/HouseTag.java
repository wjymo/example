package com.wjy.xunwu.entity;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by 瓦力.
 */
@Data
//@Entity
//@Table(name = "house_tag")
@Accessors(chain = true)
public class HouseTag {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "house_id")
    private Long houseId;

    private String name;

    public HouseTag() {
    }

    public HouseTag(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }

}
