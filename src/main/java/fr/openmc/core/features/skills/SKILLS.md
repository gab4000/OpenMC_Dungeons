<style>
    r {
        color: Red;
        font-weight: bold;
    }
    o {
        color: Orange;
        font-weight: bold;
    }
    g {
        color: Green;
        font-weight: bold;
    }
</style>

# Les compétences

## Table de matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture du système](#architecture-du-système)
    - [Les classes](#les-classes)
- [Guide d'implémentation](#guide-dimplémentation)
    - [Compétences actives](#compétences-actives)
        - [Créer une nouvelle compétence active](#créer-une-nouvelle-compétence-active)
        - [Enregistrer la compétence active](#enregistrer-la-compétence-active)
    - [Compétences passives](#compétences-passives)
        - [Créer une nouvelle compétence passive](#créer-une-nouvelle-compétence-passive)
        - [Enregistrer la compétence passive](#enregistrer-la-compétence-passive)
- [Bonnes pratiques](#bonnes-pratiques)

## Vue d'ensemble

Le système de compétences permet d'implémenter des compétences pour les joueurs dans le jeu. Grâce à un temps énorme
passé à coder ce système, il est très facile pour n'importe quel développeur de créer des compétences.

Voici donc un guide d'implémentation pour vous aider à créer vos propres compétences très simplement.

## Architecture du système

Le système est composé des éléments suivants :

| Composant              | Description                                                          |
|------------------------|----------------------------------------------------------------------|
| **Compétence active**  | Compétence qui nécessite une action du joueur pour être activée.     |
| **Compétence passive** | Compétence qui s'active automatiquement sans intervention du joueur. |
| **SKILLS**             | Enum qui contient toutes les compétences disponibles dans le jeu.    |
| **ActiveSkill**        | Classe de base (abstract) pour les compétences actives.              |
| **PassiveSkill**       | Classe de base (abstract) pour les compétences passives.             |

### Les classes

- L'interaction avec la base de données est gérée par la classe `SkillDatabase`. Elle enregistre les compétences de la
  base de données dans un cache afin d'optimiser les performances. Vous n'aurez normalement pas besoin de
  la modifier.
- Les classes `ActiveSkill` et `PassiveSkill` sont des classes abstraites qui contiennent la logique de base pour les
  compétences actives et passives. Vous devez les étendre pour créer vos propres compétences.
- La classe `SKILLS` est un enum qui contient toutes les compétences disponibles dans le jeu. Vous devez l'utiliser pour
  enregistrer vos compétences.
- La classe `SkillsManager` est le gestionnaire de compétences. Elle est utilisée pour enregistrer les
  listeners des compétences passives. Lorsque vous créez une compétence passive, elle s'enregistre automatiquement. Vous
  n'aurez pas
  besoin de la modifier.
- Les classes `SkillsMenu`, `ActiveSkillsMenu` et `PassiveSkillsMenu` sont les menus qui s'affichent lorsque le joueur
  utilise la commande `/skills`. Vous n'aurez pas besoin de les modifier, car les compétences s'affichent
  automatiquement
  avec leurs descriptions et icônes, en se basant sur l'enum `SKILLS`.
- La classe `SkillsUtils` contient des méthodes utilitaires pour gérer les compétences. Elle permet de changer les
  hotbars lors de l'utilisation du bâton, d'utiliser une compétence active et d'ouvrir le menu des compétences. Vous
  pouvez
  ajouter des méthodes utilitaires si vous le souhaitez.
- La classe `ActiveSkillEvent` est un événement qui est appelé lorsque le joueur utilise une compétence active. Vous
  n'aurez pas besoin de la modifier, mais vous pouvez l'utiliser en tant qu'événement si vous le souhaitez.
- La classe `SkillStateManager` gère le skillMode des joueurs. Elle est utilisée pour savoir si le joueur est en train
  d'utiliser le bâton ou non. Vous n'aurez pas besoin de la modifier, sauf si vous modifiez le comportement du bâton.

## Guide d'implémentation

### Compétences actives

#### Créer une nouvelle compétence active

Créez une classe qui étend de `ActiveSkill` :

```java
package fr.openmc.core.features.skills.skill.active;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;

public class MyActiveSkill extends ActiveSkill {
	
	public MyActiveSkill() {
		// Lien vers la compétence avec l'enum SKILLS
		super(SKILLS.MY_ACTIVE_SKILL);
	}
	
	@Override
	public void activeSkill(Player player) {
		// Logique de la compétence ici
		player.sendMessage("You used MySkill!");
	}
}
```

Dans cet exemple, la compétence active est écrite dans la méthode `activeSkill`. Cette méthode est appelée lorsque le
joueur
utilise la compétence avec le bâton.

#### Enregistrer la compétence active

Ajoutez la compétence dans l'enum `SKILLS` :

```java
package fr.openmc.core.features.skills;

import fr.openmc.core.features.skills.skill.active.*;
import fr.openmc.core.features.skills.skill.passive.*;
import lombok.Getter;

@Getter
public enum SKILLS {
	// Active skills
	// ... autres compétences actives
	
	// Nom de la compétence, namespace IA (avec 'skill' a la fin), description, identifiant (100), cooldown (secondes), instance de la compétence
	MY_ACTIVE_SKILL("Ma compétence active", "skills:my_active_skill_skill", "Envoie un message au joueur", 106, 0, new MySkill()),
	
	// Passive skills
	// ... autres compétences passives
}
```

<r>**IMPORTANT**</r> - La compétence **active** doit être identifiée par un nombre entre **101** et **199**. Lors de
l'ajout d'une
nouvelle
compétence active, on augmente le nombre de 1 à chaque fois. Par exemple, si la dernière compétence active est 106, la
prochaine sera 107.

### Compétences passives

#### Créer une nouvelle compétence passive

Créez une classe qui étend de `PassiveSkill` :

```java
package fr.openmc.core.features.skills.skill.passive;

import fr.openmc.core.features.skills.SKILLS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MyPassiveSkill extends PassiveSkill {
	
	public MyPassiveSkill() {
		super(SKILLS.MY_PASSIVE_SKILL);
		
		this.event = new Listener() {
			@EventHandler
			public void onPlayerJoin(PlayerJoinEvent e) {
				Player player = e.getPlayer();
				
				if (doesntHaveSkill(player)) return;
				player.sendMessage("You have the passive skill!");
			}
		};
	}
}
```

Comme vous pouvez le voir, la compétence passive est directement écrite dans le constructeur de la classe. Elle utilise
un
`Listener` pour guetter un événement spécifique afin de déclencher la compétence si le joueur l'a.

#### Enregistrer la compétence passive

Ajoutez la compétence dans l'enum `SKILLS` :

```java
package fr.openmc.core.features.skills;

import fr.openmc.core.features.skills.skill.active.*;
import fr.openmc.core.features.skills.skill.passive.*;
import lombok.Getter;

@Getter
public enum SKILLS {
	// Active skills
	// ... autres compétences actives
	
	// Passive skills
	// ... autres compétences passives
	
	// Nom de la compétence, namespace IA (avec '_skill' a la fin), description, identifiant (200), instance de la compétence
	MY_PASSIVE_SKILL("Ma compétence passive", "skills:my_passive_skill_skill", "Envoie un message au joueur lors de sa connexion", 206, new MySkill()),
}
```

<r>**IMPORTANT**</r> - La compétence **passive** doit être identifiée par un nombre entre **201** et **299**. Lors de
l'ajout d'une nouvelle compétence passive, on augmente le nombre de 1 à chaque fois. Par exemple, si la dernière
compétence passive
est 206, la prochaine sera 207.

## Bonnes pratiques

1. **Utiliser des noms de compétences clairs** : Choisissez des noms de compétences originaux qui décrivent rapidement
   leur fonctionnalité.
2. **Éviter les redondances** : Si une compétence a des fonctionnalités similaires à une autre, envisagez de les
   combiner ou de les étendre.
3. **Documenter le code** : Ajoutez des commentaires pour expliquer la logique de chaque compétence, surtout si elle est
   complexe.
4. **Tester les compétences** : Avant de déployer une nouvelle compétence, testez-la soigneusement pour vous assurer
   qu'elle fonctionne comme prévu et qu'elle n'interfère pas avec d'autres fonctionnalités du jeu.
5. **Gérer les erreurs** : Assurez-vous de gérer les exceptions et les erreurs potentielles dans le code de la
   compétence pour éviter les plantages du serveur.
6. **Optimiser les performances** : Évitez d'utiliser des boucles ou des opérations coûteuses dans les compétences,
   surtout si elles sont déclenchées fréquemment. Utilisez des méthodes efficaces pour gérer les événements et les
   interactions avec le joueur.

---

Développé par [gab400](https://github.com/gab4000), si besoin d'aide, n'hésitez pas à me contacter sur discord