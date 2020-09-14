package com.alysonsantos.aspect.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SpawnerLocation {

    private double x;

    private double y;

    private double z;

    private String world;
}
