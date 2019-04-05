package me.xnuminousx.spirits.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;

import me.xnuminousx.spirits.ability.dark.Intoxicate;
import me.xnuminousx.spirits.ability.dark.Shackle;
import me.xnuminousx.spirits.ability.dark.Strike;
import me.xnuminousx.spirits.ability.light.Alleviate;
import me.xnuminousx.spirits.ability.light.Orb;
import me.xnuminousx.spirits.ability.light.Shelter;
import me.xnuminousx.spirits.ability.light.Shelter.ShelterType;
import me.xnuminousx.spirits.ability.spirit.Dash;
import me.xnuminousx.spirits.ability.spirit.Possess;
import me.xnuminousx.spirits.ability.spirit.Soar;
import me.xnuminousx.spirits.ability.spirit.Vanish;
import me.xnuminousx.spirits.ability.water.Corrupt;
import me.xnuminousx.spirits.ability.water.Purify;
import org.bukkit.inventory.EquipmentSlot;

public class AbilityListener implements Listener {
	
	private boolean isPossessing;

	@EventHandler
	public void onSwing(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR) {
			return;
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.isCancelled()){
			return;
		}

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Agility")) {
			new Dash(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shackle")) {
			new Shackle(player);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shelter")) {
			new Shelter(player, ShelterType.CLICK);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Strike")) {
			new Strike(player);
			
		}

	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Possess")) {
			if (event.isSneaking()) {
				new Possess(player);
				isPossessing = true;
			} else {
				isPossessing = false;
				return;
			}

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Alleviate")) {
			new Alleviate(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Intoxicate")) {
			new Intoxicate(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Agility")) {
			new Soar(player);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Corrupt")) {
			new Corrupt(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Purify")) {
			new Purify(player);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Shelter")) {
			new Shelter(player, ShelterType.SHIFT);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Vanish")) {
			new Vanish(player);
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Orb")) {
			new Orb(player);
			
		}

	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		
		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		
		if (event.isCancelled() || bPlayer == null) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Vanish")) {
			if (event.getCause() == TeleportCause.SPECTATE) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer == null) {
				return;
			}
			String boundAbility = bPlayer.getBoundAbilityName();
			
			if (boundAbility.equalsIgnoreCase("Possess") && isPossessing && event.getCause() == DamageCause.CONTACT) {
				event.setCancelled(true);
				return;
			}
		}
	}
}
