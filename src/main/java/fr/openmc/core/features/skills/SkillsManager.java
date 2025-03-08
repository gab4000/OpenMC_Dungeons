package fr.openmc.core.features.skills;

import fr.openmc.core.utils.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SkillsManager {
	
	public static final Map<UUID, List<SKILLS>> playerSkills = new HashMap<>();
	
	public SkillsManager() {
		try {
			loadPlayerSkills(DatabaseManager.getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void init_db(Connection connection) throws SQLException {
		connection.prepareStatement("CREATE TABLE IF NOT EXISTS skills (" +
				"player_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
				"skill_id INT NOT NULL" +
				")").executeUpdate();
	}
	
	public void loadPlayerSkills(Connection connection) throws SQLException {
		String sql = "SELECT * FROM skills";
		ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
		while (resultSet.next()) {
			UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));
			int skillID = resultSet.getInt("skill_id");
			
			SKILLS skill = SKILLS.getSkillById(skillID);
			if (skill == null) continue;
			
			playerSkills.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(skill);
		}
	}
	
	public static void savePlayerSkills(Connection connection) {
		try {
			deletePlayerSkillsInDb(connection);
			String sql = "INSERT INTO skills (player_uuid, skill_id) VALUES (?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			connection.setAutoCommit(false);
			
			for (Map.Entry<UUID, List<SKILLS>> entry : playerSkills.entrySet()) {
				UUID playerUUID = entry.getKey();
				List<SKILLS> skills = entry.getValue();
				
				for (SKILLS skill : skills) {
					statement.setString(1, playerUUID.toString());
					statement.setInt(2, skill.getId());
					statement.addBatch();
				}
			}
			
			statement.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void deletePlayerSkillsInDb(Connection connection) throws SQLException {
		connection.prepareStatement("DELETE FROM skills").executeUpdate();
	}
}
