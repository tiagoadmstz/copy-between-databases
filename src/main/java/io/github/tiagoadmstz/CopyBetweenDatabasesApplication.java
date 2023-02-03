package io.github.tiagoadmstz;

import io.github.tiagoadmstz.dal.SqlServerConnection;
import io.github.tiagoadmstz.services.CopyService;

public class CopyBetweenDatabasesApplication {

    public static void main(String[] args) {
        CopyService copyService = new CopyService (
                new SqlServerConnection(),
                new String[]{"", "", "", ""},
                new String[]{"VARVSQL1", "FICHACONTROLE", "PROD", ""}
        );
        copyService.copyDataBetweenBases();
        System.out.println("Copy successful");
    }
}
