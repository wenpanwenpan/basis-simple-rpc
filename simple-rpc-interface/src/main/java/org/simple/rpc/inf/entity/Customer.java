package org.simple.rpc.inf.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 会员entity(注意：由于需要网络传输，所以customer类需要实现Serializable接口)
 *
 * @author Mr_wenpan@163.com 2022/01/24 17:26
 */
@Data
public class Customer implements Serializable {
    /**
     * 会员姓名
     */
    private String customerName;
    /**
     * 年龄
     */
    private Integer customerAge;
    /**
     * 生日
     */
    private Date birthDay;
    /**
     * 爱好
     */
    private String[] hobby;

    /**
     * 手机对象
     */
    private Phone phone;

    /**
     * 手机对象集合
     */
    private List<Phone> phoneList;

}
