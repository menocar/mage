package mage.client.record;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import mage.cards.repository.RepositoryUtil;
import org.apache.log4j.Logger;

public enum RawRepository {

    instance;

    private static final String JDBC_URL = "jdbc:h2:file:./db/raw.h2;AUTO_SERVER=TRUE";
    private static final String VERSION_ENTITY_NAME = "raw";
    // raise this if db structure was changed
    private static final long RAW_DB_VERSION = 0;
    // raise this if new cards were added to the server
    private static final long RAW_CONTENT_VERSION = 0;

    private Dao<Raw, Object> cardDao;

    private RawRepository() {
        File file = new File("db");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(JDBC_URL);
            boolean obsolete = RepositoryUtil.isDatabaseObsolete(connectionSource, VERSION_ENTITY_NAME, RAW_DB_VERSION);

            if (obsolete) {
                TableUtils.dropTable(connectionSource, Raw.class, true);
            }

            TableUtils.createTableIfNotExists(connectionSource, Raw.class);
            cardDao = DaoManager.createDao(connectionSource, Raw.class);
        } catch (SQLException ex) {
            Logger.getLogger(RawRepository.class).error("Error creating raw repository - ", ex);
        }
    }

    public void addRaw(final Raw raw) {
        try {
            cardDao.create(raw);
        } catch (SQLException ex) {
            Logger.getLogger(RawRepository.class).error("Error adding raw to DB - ", ex);
        }
    }

    public boolean rawExists(Raw raw) {
        try {
            QueryBuilder<Raw, Object> queryBuilder = cardDao.queryBuilder();
            queryBuilder.where().eq("deckType", new SelectArg(raw.getDeckType())).
                and().eq("players", raw.getPlayers()).
                and().eq("gameType", raw.getGameType()).
                and().eq("result", raw.getResultBytes()).
                and().eq("startTime", raw.getStartTime()).
                and().eq("endTime", raw.getEndTime());
            List<Raw> result = cardDao.query(queryBuilder.prepare());
            if (!result.isEmpty()) {
                return true;
            }
        } catch (SQLException ex) {
        }
        return false;
    }

    public List<Raw> getAll() {
        try {
            QueryBuilder<Raw, Object> queryBuilder = cardDao.queryBuilder();
            queryBuilder.orderBy("endTime", true).orderBy("players", true);
            List<Raw> result = cardDao.query(queryBuilder.prepare());
            return result;
        } catch (SQLException ex) {
        }
        return null;
    }

    public void closeDB() {
        try {
            if (cardDao != null && cardDao.getConnectionSource() != null) {
                DatabaseConnection conn = cardDao.getConnectionSource().getReadWriteConnection();
                conn.executeStatement("shutdown compact", 0);
            }
        } catch (SQLException ex) {
        }
    }
}
