package com.alysonsantos.aspect.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.helper.serialize.Position;

import java.util.Set;

@Getter
@AllArgsConstructor
public class User {

    private String name;
    private Set<Position> positions;

}
