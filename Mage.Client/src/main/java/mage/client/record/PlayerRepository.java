package mage.client.record;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
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

public enum PlayerRepository {

    instance;

    private static final String JDBC_URL = "jdbc:h2:file:./db/player.h2;AUTO_SERVER=TRUE";
    private static final String VERSION_ENTITY_NAME = "player";
    // raise this if db structure was changed
    private static final long PLAYER_DB_VERSION = 0;
    // raise this if new cards were added to the server
    private static final long PLAYER_CONTENT_VERSION = 0;

    private Dao<Player, Object> cardDao;

    private PlayerRepository() {
        File file = new File("db");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(JDBC_URL);
            boolean obsolete = RepositoryUtil.isDatabaseObsolete(connectionSource, VERSION_ENTITY_NAME, PLAYER_DB_VERSION);

            if (obsolete) {
                TableUtils.dropTable(connectionSource, Player.class, true);
            }

            TableUtils.createTableIfNotExists(connectionSource, Player.class);
            cardDao = DaoManager.createDao(connectionSource, Player.class);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerRepository.class).error("Error creating player repository - ", ex);
        }
    }

    public void addPlayer(final Player player) {
        try {
            cardDao.create(player);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerRepository.class).error("Error adding player to DB - ", ex);
        }
    }

    public void deletePlayer(final Player player) {
        try {
            DeleteBuilder<Player, Object> deleteBuilder = cardDao.deleteBuilder();
            deleteBuilder.where().eq("name", player.getName());
            cardDao.delete(deleteBuilder.prepare());
        } catch (SQLException ex) {
            Logger.getLogger(PlayerRepository.class).error("Error deleting player from DB - ", ex);
        }
        
    }
    
    public Player getPlayer(String name) {
        try {
            QueryBuilder<Player, Object> queryBuilder = cardDao.queryBuilder();
            queryBuilder.where().eq("name", new SelectArg(name));
            List<Player> result = cardDao.query(queryBuilder.prepare());
            return result.size() == 1 ? result.get(0) : null;
        } catch (SQLException ex) {
            Logger.getLogger(PlayerRepository.class).error("Error getting a player from DB - ", ex);
        }
        return null;
    }

    public List<Player> getAll() {
        try {
            QueryBuilder<Player, Object> queryBuilder = cardDao.queryBuilder();
            queryBuilder.orderBy("name", true);
            List<Player> result = cardDao.query(queryBuilder.prepare());
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(PlayerRepository.class).error("Error getting all players from DB - ", ex);
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
