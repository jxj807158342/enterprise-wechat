package com.ajie.enterprisewechat;


import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class GenerateDbMigration {
    public static void main(String[] args) throws IOException {
        //删除用
//        System.setProperty("ddl.migration.pendingDropsFor","1.4");
        DbMigration dbMigration = DbMigration.create();
        dbMigration.setPlatform(Platform.MYSQL);
        dbMigration.setStrictMode(false);
//        dbMigration.setStrictMode(false);
        dbMigration.generateMigration();
    }
}
