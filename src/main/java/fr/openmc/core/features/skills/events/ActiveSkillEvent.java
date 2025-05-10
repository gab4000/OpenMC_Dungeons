package fr.openmc.core.features.skills.events;

import fr.openmc.core.features.skills.skill.active.ActiveSkill;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ActiveSkillEvent extends Event {
	
	private static final HandlerList HANDLER_LIST = new HandlerList();
	private final Player player;
	private final ActiveSkill skill;
	
	/**
	 * Constructor for ActiveSkillEvent.
	 *
	 * @param player The player who triggered the event.
	 * @param skill  The active skill associated with the event.
	 */
	public ActiveSkillEvent(Player player, ActiveSkill skill) {
		this.player = player;
		this.skill = skill;
	}
	
	/**
	 * Gets the handler list for this event.
	 *
	 * @return The handler list.
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
	
	/**
	 * Gets the handlers for this event.
	 *
	 * @return The handler list.
	 */
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}
}
