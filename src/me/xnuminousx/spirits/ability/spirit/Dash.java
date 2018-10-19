package me.xnuminousx.spirits.ability.spirit;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;

import me.xnuminousx.spirits.Methods;
import me.xnuminousx.spirits.Methods.SpiritType;
import me.xnuminousx.spirits.ability.api.SpiritAbility;

public class Dash extends SpiritAbility implements AddonAbility {

	private long dashCooldown;
	private long distance;
	private World originWorld;

	public Dash(Player player) {
		super(player);
		
		if (!bPlayer.canBend(this)) {
			return;
		}
		setFields();
		start();
	}

	private void setFields() {
		this.dashCooldown = ConfigManager.getConfig().getLong("Abilities.Spirits.Neutral.Agility.Dash.Cooldown");
		this.distance = ConfigManager.getConfig().getLong("Abilities.Spirits.Neutral.Agility.Dash.Distance");
		this.originWorld = player.getWorld();
	}

	@Override
	public void progress() {
		Methods.readGeneralMethods(this, player, originWorld);
		if (bPlayer.isOnCooldown("Dash")) {
			remove();
			return;
		} else {
			progressDash();
		}
	}

	private void progressDash() {
		Location loc = player.getLocation();
		Methods.setPlayerVelocity(player, true, distance, 0.2);
		loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_HURT, 1.5F, 0.5F);
		loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.3F, 0.5F);
		Methods.playSpiritParticles(bPlayer, player.getLocation(), 0.5F, 0.5f, 0.5F, 0, 10);
		bPlayer.addCooldown("Dash", dashCooldown);
		remove();
		return;
	}

	@Override
	public long getCooldown() {

		return dashCooldown;
	}

	@Override
	public Location getLocation() {

		return null;
	}

	@Override
	public String getName() {
		
		return "Agility";
	}

	@Override
	public String getAuthor() {
		return Methods.setSpiritDescriptionColor(SpiritType.NEUTRAL) + Methods.getAuthor();
	}

	@Override
	public String getVersion() {
		return Methods.setSpiritDescriptionColor(SpiritType.NEUTRAL) + Methods.getVersion();
	}
	
	@Override
	public boolean isEnabled() {
		return ConfigManager.getConfig().getBoolean("Abilities.Spirits.Neutral.Agility.Enabled");
	}

	@Override
	public boolean isExplosiveAbility() {

		return false;
	}

	@Override
	public boolean isHarmlessAbility() {

		return false;
	}

	@Override
	public boolean isIgniteAbility() {

		return false;
	}

	@Override
	public boolean isSneakAbility() {

		return false;
	}

	@Override
	public void load() {
	}

	@Override
	public void stop() {
	}

}
