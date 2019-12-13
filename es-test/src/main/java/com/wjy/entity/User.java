package com.wjy.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class User {
    private String name;
    private Integer age;
    private Double faceScore;
    private String job;
    private Address address;
}
