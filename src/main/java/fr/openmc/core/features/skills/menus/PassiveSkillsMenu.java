package fr.openmc.core.features.skills.menus;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.dungeons.levels.DungeonLevelsListener;
import fr.openmc.core.features.skills.SKILLS;
import fr.openmc.core.features.skills.skill.passive.PassiveSkill;
import fr.openmc.core.features.skills.utils.SkillsUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassiveSkillsMenu extends Menu {
	
	public PassiveSkillsMenu(Player owner) {
		super(owner);
	}
	
	@Override
	public @NotNull String getName() {
		return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_passive_skills%");
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGEST;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map map = new HashMap();
		
		int i = 18;
		for (SKILLS skills : SKILLS.values()) {
			if (skills.getId() > 200) {
				ItemStack item = CustomStack.getInstance(skills.getNamespace()).getItemStack();
				ItemMeta meta = item.getItemMeta();
				meta.setLore(List.of(skills.getDescription()));
				item.setItemMeta(meta);
				
				map.put(i, new ItemBuilder(this, item).setOnClick(inventoryClickEvent -> {
					if (! SkillsUtils.playerPassiveSkillsActivated.containsKey(getOwner().getUniqueId())) {
						List<Class<? extends PassiveSkill>> list = new ArrayList<>();
						list.add(skills.getPassiveSkill().getClass());
						SkillsUtils.playerPassiveSkillsActivated.put(getOwner().getUniqueId(), list);
					} else {
						SkillsUtils.playerPassiveSkillsActivated.get(getOwner().getUniqueId()).add(skills.getPassiveSkill().getClass());
					}
					getOwner().closeInventory();
				}));
				i += 2;
			}
		}
		map.put(53, CustomStack.getInstance("skills:skill_point").getItemStack().asQuantity(DungeonLevelsListener.getSkillsPoint(getOwner())));
		
		return map;
	}
}
