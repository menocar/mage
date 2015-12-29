package mage.client.record;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player")
public class Player {

    @DatabaseField
    protected String name;
    @DatabaseField
    protected String token;

    static private SecureRandom random = new SecureRandom();

    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.token = this.token = new BigInteger(25, random).toString(32);
    }

    public String getName() {
        return this.name;
    }

    public String getToken() {
        return this.token;
    }
}
