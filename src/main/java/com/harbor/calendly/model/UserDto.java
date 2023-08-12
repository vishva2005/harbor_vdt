package com.harbor.calendly.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {

    private Integer id;
    private String name;
    private String email;
}
