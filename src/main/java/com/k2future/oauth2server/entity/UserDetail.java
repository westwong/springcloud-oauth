package com.k2future.oauth2server.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author West
 * @date create in 2020/1/7
 */
@Data
@Accessors(chain = true)
@Entity
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @NotEmpty(message = "name is blank?")

    @Size(max = 15)
    @Column(length = 15)
    private String name;

    @Column(length = 5)
    private String age;

    @Column(length = 15)
    private String phone;
    @Column(length = 5)
    private String gender;
    @Column(length = 64)
    private String addr;

    private LocalDateTime createTime;
    /**
     *  民族
     */
    @Column(length = 5)
    private String nation;

    private String instruction;

    private String images;

    private LocalDate birthday;

}
