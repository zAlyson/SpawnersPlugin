package com.alysonsantos.aspect.database.mysql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The credentials of mysql
 *
 * @author zkingboos_
 */
@Getter
@RequiredArgsConstructor
public class UniversalCredentials {
    private final String hostname, database, user, password;
}