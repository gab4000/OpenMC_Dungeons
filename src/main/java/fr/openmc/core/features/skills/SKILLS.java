package fr.openmc.core.features.skills;

import fr.openmc.core.features.skills.skill.active.*;
import fr.openmc.core.features.skills.skill.passive.*;
import lombok.Getter;

@Getter
public enum SKILLS {
	
	// Active Skills
	NIGHT_VISION("Night Vision", "skills:night_vision_skill", "Vision nocturne pendant 5 min", 101, 0, new NightVisionSkill()),
	MINE_BOOST("Mine Boost", "skills:mine_boost_skill", "Boost de minage pendant 10 min", 102, 0, new MineBoostSkill()),
	HEAL("Soin", "skills:heal_skill", "Restaure 3 cœurs instantanément", 103, 0, new HealSkill()),
	ORE_DETECTOR("Detecteur a minerais", "skills:ore_detector_skill", "Indique si des minerais de trouvent dans un rayon de 4x4x4 blocks", 104, 0, new OreDetectorSkill()),
	OXYGEN_BOTTLE("Boutille d'oxygène", "skills:oxygen_bottle_skill", "Augmente le temps qu'on peut passer sous l'eau", 105, 0, new OxygenBottleSkill()),
	
	// Passive Skills
	ROLL("Roulade", "skills:roll_skill", "Réduit de 10% les dégâts de chute", 201, new RollSkill()),
	AGILITY("Agilité", "skills:agility_skill", "Augmente légèrement la vitesse de déplacement", 202, new AgilitySkill()),
	RESILIENCE("Resilience", "skills:resilience_skill", "Augmente les dégâts de combats (PVP/PVE) de 5%", 203, new ResilienceSkill()),
	SWIM_MASTER("Maitre Nageur", "skills:swim_master_skill", "Augmente la vitesse de nage de 15% comme Léon Marchand", 204, new SwimMasterSkill()),
	IRON_SKIN("Peau de Fer", "skills:iron_skin_skill", "Augmente la résistance aux dégâts directs", 205, new IronSkinSkill());
	
	// Enum fields
	private final String name; // En francais
	private final String namespace;
	private final String description;
	private final int id; // 100 : Active | 200 : Passive
	private int cooldown; // En secondes
	private ActiveSkill activeSkill;
	private PassiveSkill passiveSkill;
	
	/**
	 * Enum constructor for active skill.
	 *
	 * @param name        The name of the skill.
	 * @param namespace   The namespace of the skill.
	 * @param description The description of the skill.
	 * @param id          The ID of the skill.
	 * @param cooldown    The reload cooldown time of the skill in seconds.
	 */
	SKILLS(String name, String namespace, String description, int id, int cooldown, ActiveSkill activeSkill) {
		this.name = name;
		this.namespace = namespace;
		this.description = description;
		this.id = id;
		this.cooldown = cooldown;
		this.activeSkill = activeSkill;
	}
	
	/**
	 * Enum constructor for passive skill.
	 *
	 * @param name        The name of the skill.
	 * @param namespace   The namespace of the skill.
	 * @param description The description of the skill.
	 * @param id          The ID of the skill.
	 */
	SKILLS(String name, String namespace, String description, int id, PassiveSkill passiveSkill) {
		this.name = name;
		this.namespace = namespace;
		this.description = description;
		this.id = id;
		this.passiveSkill = passiveSkill;
	}
	
	/**
	 * Enum constructor for passive skill without namespace.
	 *
	 * @param skills The skills enum.
	 * @return True if the skill is an active skill, false otherwise.
	 */
	public static boolean isActiveSkill(SKILLS skills) {
		return skills.getId() > 100 && skills.getId() < 200;
	}
	
	/**
	 * Check if the skill is a passive skill.
	 *
	 * @param skills The skills enum.
	 * @return True if the skill is a passive skill, false otherwise.
	 */
	public static boolean isPassiveSkill(SKILLS skills) {
		return skills.getId() > 200 && skills.getId() < 300;
	}
	
	/**
	 * Get the skill by its namespace.
	 *
	 * @param namespace The namespace of the skill.
	 * @return The SKILLS enum constant corresponding to the namespace, or null if not found.
	 */
	public static SKILLS getSkillByNamespace(String namespace) {
		for (SKILLS skill : SKILLS.values()) {
			if (skill.getNamespace() != null && skill.getNamespace().equals(namespace)) {
				return skill;
			}
		}
		return null;
	}
	
	/**
	 * Get the skill by its ID.
	 *
	 * @param id The ID of the skill.
	 * @return The SKILLS enum constant corresponding to the ID, or null if not found.
	 */
	public static SKILLS getSkillById(int id) {
		for (SKILLS skill : SKILLS.values()) {
			if (skill.getId() == id) return skill;
		}
		return null;
	}
}
