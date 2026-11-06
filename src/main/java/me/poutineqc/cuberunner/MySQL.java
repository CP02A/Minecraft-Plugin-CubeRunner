package me.poutineqc.cuberunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * MySQL storage. Every query is a {@link PreparedStatement}: values are always bound as parameters, never
 * concatenated into the SQL.
 *
 * <p>Table names can't be parameters, so they are built from the configured table prefix, which
 * {@link Configuration} restricts to letters, digits and underscores.
 */
public class MySQL {

	/** Handles one row of a query result. */
	@FunctionalInterface
	public interface RowHandler {
		void handle(ResultSet row) throws SQLException;
	}

	public static final String SIGNS = "SIGNS";
	public static final String ARENAS = "ARENAS";
	public static final String PLAYERS = "PLAYERS";

	private CubeRunner plugin;
	private String prefix;

	private String host;
	private int port;
	private String database;
	private String user;
	private String password;

	private Connection connection;

	public MySQL(CubeRunner plugin) {
		this.plugin = plugin;
		connection = null;
	}

	public MySQL(CubeRunner plugin, String host, int port, String database, String user, String password,
			String prefix) {
		this.plugin = plugin;
		this.prefix = prefix;

		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;

		connect();

		if (hasConnection())
			initializeDatabases();
	}

	public MySQL() {
		connection = null;
	}

	private void initializeDatabases() {
		update("CREATE TABLE IF NOT EXISTS " + table(SIGNS) + " ("
				+ "uuid varchar(64), type varchar(32),"
				+ "locationWorld varchar(32), locationX INT DEFAULT 0, locationY INT DEFAULT 0, locationZ INT DEFAULT 0)");
		update("ALTER TABLE " + table(SIGNS) + " CONVERT TO CHARACTER SET utf8");

		update("CREATE TABLE IF NOT EXISTS " + table(ARENAS) + " (name varchar(32),world varchar(32),"
				+ "minAmountPlayer INT DEFAULT 1, maxAmountPlayer INT DEFAULT 8, highestScore INT DEFAULT 0,"
				+ "colorIndice LONG, highestPlayer varchar(32) DEFAULT 'null',"
				+ "minPointX INT DEFAULT 0,minPointY INT DEFAULT 0,minPointZ INT DEFAULT 0,"
				+ "maxPointX INT DEFAULT 0, maxPointY INT DEFAULT 0,maxPointZ INT DEFAULT 0,"
				+ "lobbyX DOUBLE DEFAULT 0,lobbyY DOUBLE DEFAULT 0,lobbyZ DOUBLE DEFAULT 0,"
				+ "lobbyPitch FLOAT DEFAULT 0,lobbyYaw FLOAT DEFAULT 0,"
				+ "startPointX DOUBLE DEFAULT 0,startPointY DOUBLE DEFAULT 0,startPointZ DOUBLE DEFAULT 0,"
				+ "startPointPitch FLOAT DEFAULT 0,startPointYaw FLOAT DEFAULT 0)");
		update("ALTER TABLE " + table(ARENAS) + " CONVERT TO CHARACTER SET utf8");

		update("CREATE TABLE IF NOT EXISTS " + table(PLAYERS)
				+ " (UUID varchar(64), name varchar(64), language varchar(32), timePlayed INT DEFAULT 0,"
				+ "money DOUBLE DEFAULT 0, averageDistancePerGame DOUBLE DEFAULT 0, totalDistance DOUBLE DEFAULT 0,"
				+ "games INT DEFAULT 0, totalScore INT DEFAULT 0, kills INT DEFAULT 0, multiplayerWon INT DEFAULT 0,"
				+ "survive5Minutes BOOLEAN DEFAULT FALSE, reachHeight10 BOOLEAN DEFAULT FALSE,"
				+ "fillTheArena BOOLEAN DEFAULT FALSE, theAnswerToLife BOOLEAN DEFAULT FALSE,"
				+ "theRageQuit BOOLEAN DEFAULT FALSE, theKillerBunny BOOLEAN DEFAULT FALSE)");
		update("ALTER TABLE " + table(PLAYERS) + " CONVERT TO CHARACTER SET utf8");
	}

	/** The full name of one of CubeRunner's tables ({@link #SIGNS}, {@link #ARENAS}, {@link #PLAYERS}). */
	public String table(String name) {
		return prefix + name;
	}

	public void connect() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
		} catch (SQLException e) {
			plugin.getLogger().info("[MySQL] The connection to MySQL couldn't be made! reason: " + e.getMessage());
		}
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			plugin.getLogger().info("[MySQL] The connection couldn't be closed! reason: " + e.getMessage());
		}
	}

	public boolean hasConnection() {
		return connection != null;
	}

	/** Runs an INSERT, UPDATE, DELETE or DDL statement with the given parameters bound to its {@code ?}s. */
	public void update(String sql, Object... params) {
		try (PreparedStatement statement = prepare(sql, params)) {
			statement.executeUpdate();
		} catch (SQLException e) {
			fail(sql, e);
		}
	}

	/** Runs a SELECT with the given parameters bound to its {@code ?}s and passes each row to the handler. */
	public void query(String sql, RowHandler handler, Object... params) {
		try (PreparedStatement statement = prepare(sql, params); ResultSet rows = statement.executeQuery()) {
			while (rows.next())
				handler.handle(rows);
		} catch (SQLException e) {
			fail(sql, e);
		}
	}

	private PreparedStatement prepare(String sql, Object... params) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		try {
			for (int i = 0; i < params.length; i++)
				statement.setObject(i + 1, params[i]);
		} catch (SQLException e) {
			statement.close();
			throw e;
		}
		return statement;
	}

	private void fail(String sql, SQLException e) {
		plugin.getLogger().log(Level.WARNING, "[MySQL] Query failed: " + sql, e);
		connect();
	}

	public void newPlayer(UUID uuid, String name) {
		update("INSERT INTO " + table(PLAYERS) + " (UUID, name) VALUES (?, ?)", uuid.toString(), name);
	}

	/** Sets one stat of a player. The column comes from the {@link CRStats} enum, never from user input. */
	public void updatePlayer(UUID uuid, CRStats key, Object value) {
		update("UPDATE " + table(PLAYERS) + " SET " + key.getNameMySQL() + " = ? WHERE UUID = ?", value,
				uuid.toString());
	}

	public void queryAllPlayers(RowHandler handler) {
		query("SELECT * FROM " + table(PLAYERS), handler);
	}
}
