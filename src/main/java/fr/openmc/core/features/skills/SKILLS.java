package fr.openmc.core.features.skills;

import fr.openmc.core.features.skills.skill.active.*;
import fr.openmc.core.features.skills.skill.passive.*;
import lombok.Getter;

@Getter
public enum SKILLS {
	
	NIGHT_VISION("Night Vision", "skills:night_vision_skill", "Vision nocturne pendant 5 min", 101, 0, new NightVisionSkill()),
	MINE_BOOST("Mine Boost", "skills:mine_boost_skill", "Boost de minage pendant 10 min", 102, 0, new MineBoostSkill()),
	HEAL("Soin", "skills:heal_skill", "Restaure 3 cœurs instantanément", 103, 0, new HealSkill()),
	ORE_DETECTOR("Detecteur a minerais", "skills:ore_detector_skill", "Indique si des minerais de trouvent dans un rayon de 4x4x4 blocks", 104, 0, new OreDetectorSkill()),
	APNEA("Apnée", "skills:apnea_skill", "Augmente le temps qu'on peut passer sous l'eau", 105, 0, new ApneaSkill()),
	
	ROLL("Roulade", "skills:roll_skill", "Réduit de 10% les dégâts de chute", 201, 0, RollSkill.class),
	AGILITY("Agilité", "skills:agility_skill", "Augmente légèrement la vitesse de déplacement", 202, 0, AgilitySkill.class),
	RESILIENCE("Resilience", "skills:resilience_skill", "Augmente les dégâts de combats (PVP/PVE) de 5%", 203, 0, ResilienceSkill.class),
	SWIM_MASTER("Maitre Nageur", "skills:swim_master_skill", "Augmente la vitesse de nage de 15% comme Léon Marchand", 204, 0, SwimMasterSkill.class),
	IRON_SKIN("Peau de Fer", "skills:iron_skin_skill", "Augmente la résistance aux dégâts directs", 205, 0, IronSkinSkill.class);
	
	private final String name; // En francais
	private final String namespace;
	private final String description;
	private final int id; // 100 : Active | 200 : Passive
	private final int reload; // En secondes
	private ActiveSkill activeSkill;
	private Class<? extends PassiveSkill> passiveSkill;
	
	SKILLS(String name, String namespace, String description, int id, int reload, ActiveSkill activeSkill) {
		this.name = name;
		this.namespace = namespace;
		this.description = description;
		this.id = id;
		this.reload = reload;
		this.activeSkill = activeSkill;
	}
	
	SKILLS(String name, String namespace, String description, int id, int reload, Class<? extends PassiveSkill> passiveSkill) {
		this.name = name;
		this.namespace = namespace;
		this.description = description;
		this.id = id;
		this.reload = reload;
		this.passiveSkill = passiveSkill;
	}
	
	public static SKILLS getSkillByClass(Class<?> skillClass) {
		for (SKILLS skill : SKILLS.values()) {
			if ((skill.getActiveSkill() != null && skill.getActiveSkill().getClass().equals(skillClass)) ||
					(skill.getPassiveSkill() != null && skill.getPassiveSkill().equals(skillClass))) {
				return skill;
			}
		}
		return null; // Retourne null si aucune compétence ne correspond
	}
	
	public static SKILLS getSkillByNamespace(String namespace) {
		for (SKILLS skill : SKILLS.values()) {
			if (skill.getNamespace() != null && skill.getNamespace().equals(namespace)) {
				return skill;
			}
		}
		return null; // Retourne null si aucune compétence ne correspond
	}
	
	public static SKILLS getSkillById(int id) {
		for (SKILLS skill : SKILLS.values()) {
			if (skill.getId() == id) return skill;
		}
		return null;
	}
}
