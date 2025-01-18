package fr.openmc.core.features.skills.utils;

import java.util.HashSet;
import java.util.UUID;

public class SkillStateManager {
	
	private final HashSet<UUID> skillModePlayers = new HashSet<>();
	
	public void setSkillMode(UUID playerId, boolean enabled) {
		if (enabled) {
			skillModePlayers.add(playerId);
		} else {
			skillModePlayers.remove(playerId);
		}
	}
	
	public boolean isInSkillMode(UUID playerId) {
		return skillModePlayers.contains(playerId);
	}
	
	public boolean contains(UUID playerId) {
		return skillModePlayers.contains(playerId);
	}
}
