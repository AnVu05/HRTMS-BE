package com.swp.hrtms.hrtmsbe.dto.request;

import com.swp.hrtms.hrtmsbe.entity.HorseStatus;

public class HorseRequest {

    private Integer ownerId;
    private String name;
    private Integer age;
    private String breed;
    private HorseStatus status;

    public HorseRequest() {
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public HorseStatus getStatus() {
        return status;
    }

    public void setStatus(HorseStatus status) {
        this.status = status;
    }
}
