package br.com.battlebits.commons.tests;

import br.com.battlebits.commons.Commons;
import br.com.battlebits.commons.account.BattleAccount;
import br.com.battlebits.commons.backend.mongodb.MongoDatabase;
import br.com.battlebits.commons.backend.mongodb.MongoStorageDataAccount;

import java.util.UUID;

public class Backend {

    public static void main(String[] args) throws Exception {
        MongoDatabase db = new MongoDatabase("localhost", "test", "test", "test", 27017);
        db.connect();
        MongoStorageDataAccount dataAccount = new MongoStorageDataAccount(db);

        Commons.initialize(null, null, null, dataAccount, null, null, null, null);

        BattleAccount account = new BattleAccount(UUID.randomUUID(), "GustavoInacio") {
            @Override
            public void sendMessage(String tag, Object... objects) {

            }
        };
        Commons.getDataAccount().saveAccount(account);


        account.setJoinData("teste");

        System.out.println(account.getUniqueId());

        db.disconnect();
    }
}
