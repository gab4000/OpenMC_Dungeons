package fr.openmc.core.features.skills.utils;

import java.util.HashSet;
import java.util.UUID;

public class SkillStateManager {
	
	/// Map to store the players in skill mode.
	private final HashSet<UUID> skillModePlayers = new HashSet<>();
	
	/**
	 * Sets the skill mode for a player.
	 * @param playerId The UUID of the player.
	 * @param enabled True to enable skill mode, false to disable it.
	 */
	public void setSkillMode(UUID playerId, boolean enabled) {
		if (enabled) {
			skillModePlayers.add(playerId);
		} else {
			skillModePlayers.remove(playerId);
		}
	}
	
	/**
	 * Checks if a player is in skill mode.
	 * @param playerId The UUID of the player.
	 * @return True if the player is in skill mode, false otherwise.
	 */
	public boolean isInSkillMode(UUID playerId) {
		return skillModePlayers.contains(playerId);
	}
	
	/**
	 * Toggles the skill mode for a player.
	 * @param playerId The UUID of the player.
	 */
	public boolean contains(UUID playerId) {
		return skillModePlayers.contains(playerId);
	}
}
