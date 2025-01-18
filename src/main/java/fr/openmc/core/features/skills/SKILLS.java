package fr.openmc.core.features.skills;

import fr.openmc.core.features.skills.skill.active.*;
import fr.openmc.core.features.skills.skill.passive.AgilitySkill;
import fr.openmc.core.features.skills.skill.passive.PassiveSkill;
import fr.openmc.core.features.skills.skill.passive.ResilienceSkill;
import fr.openmc.core.features.skills.skill.passive.RollSkill;
import lombok.Getter;

@Getter
public enum SKILLS {
	
	NIGHT_VISION("Night Vision", "skills:night_vision_scroll", 101, 0, new NightVisionSkill()),
	MINE_BOOST("Mine Boost", "skills:mine_boost_scroll", 102, 0, new MineBoostSkill()),
	HEAL("Soin", "skills:heal_scroll", 103, 0, new HealSkill()),
	ORE_DETECTOR("Detecteur a minerais", "skills:ore_detector_scroll", 104, 0, new OreDetectorSkill()),
	SWIM_MASTER("Maitre Nageur", "skills:swim_master_scroll", 105, 0, new SwimMasterSkill()),
	
	ROLL("Roulade", "Réduit de 10% les dégâts de chute", 201, 0, RollSkill.class),
	AGILITY("Agilité", "Augmente légèrement la vitesse de déplacement", 202, 0, AgilitySkill.class),
	RESILIENCE("Resilience", "Diminue les dégâts de combats (PVP/PVE) de 5%", 203, 0, ResilienceSkill.class);
	
	private final String name; // En francais
	private final String namespace;
	private final int id; // 100 : Active | 200 : Passive
	private final int reload; // En secondes
	private ActiveSkill activeSkill;
	private Class<? extends PassiveSkill> passiveSkill;
	
	SKILLS(String name, String namespace, int id, int reload, ActiveSkill activeSkill) {
		this.name = name;
		this.namespace = namespace;
		this.id = id;
		this.reload = reload;
		this.activeSkill = activeSkill;
	}
	
	SKILLS(String name, String namespace, int id, int reload, Class<? extends PassiveSkill> passiveSkill) {
		this.name = name;
		this.namespace = namespace;
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
}
