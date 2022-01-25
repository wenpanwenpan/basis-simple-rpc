package org.simple.rpc.inf.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 手机entity
 *
 * @author Mr_wenpan@163.com 2022/01/24 17:28
 */
public class Phone implements Serializable {
    /**
     * 手机类型
     */
    private String type;
    /**
     * 手机品牌
     */
    private String brand;
    /**
     * 收集尺寸
     */
    private Integer size;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone phone = (Phone) o;
        return Objects.equals(type, phone.type) &&
                Objects.equals(brand, phone.brand) &&
                Objects.equals(size, phone.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, brand, size);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", size=" + size +
                '}';
    }
}
