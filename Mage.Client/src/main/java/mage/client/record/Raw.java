package mage.client.record;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "raw")
public class Raw {

    @DatabaseField
    protected String deckType;
    @DatabaseField
    protected String players;
    @DatabaseField
    protected String gameType;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    protected byte[] result;
    @DatabaseField
    protected Date startTime;
    @DatabaseField
    protected Date endTime;

    public Raw() {
    }

    public Raw(String deckType, String players, String gameType, String result, Date startTime, Date endTime) {
        this.deckType = deckType;
        this.players = players;
        this.gameType = gameType;
        try {
            this.result = result.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDeckType() {
        return this.deckType;
    }

    public String getPlayers() {
        return this.players;
    }

    public String getGameType() {
        return this.gameType;
    }

    public String getResult() {
        try {
            return new String(this.result, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public byte[] getResultBytes() {
        return this.result;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }
}
