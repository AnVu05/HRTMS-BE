package com.swp.hrtms.hrtmsbe.dto.response;

import com.swp.hrtms.hrtmsbe.entity.HorseStatus;

public class HorseResponse {

    private Integer id;
    private Integer ownerId;
    private String ownerName;
    private String name;
    private Integer age;
    private String breed;
    private HorseStatus status;

    public HorseResponse() {
    }

    public HorseResponse(Integer id, Integer ownerId, String ownerName, String name, Integer age, String breed, HorseStatus status) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
