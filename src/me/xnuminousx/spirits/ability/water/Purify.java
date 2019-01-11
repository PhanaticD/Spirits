package me.xnuminousx.spirits.ability.water;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.xnuminousx.spirits.Main;
import me.xnuminousx.spirits.Methods;
import me.xnuminousx.spirits.elements.SpiritElement;
import net.md_5.bungee.api.ChatColor;

public class Purify extends WaterAbility implements AddonAbility {

	public LivingEntity target;
	private double range;
	public static Set<Integer> heldEntities = new HashSet<Integer>();
	private long duration;
	private long cooldown;
	private boolean hasReached = true;
	private int ticks;
	private int chargeTicks;
	private long starttime;
	private boolean targetfroze = false;
	private boolean setElement;
	private MovementHandler mh;
	private double switchpercent;

	public Purify(Player player) {
		super(player);
		if (!bPlayer.canBend(this)) {
			return;
		}
		setFields();

		Entity e = GeneralMethods.getTargetedEntity(player, range);
		if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
			target = (LivingEntity) e;
		}
		starttime = System.currentTimeMillis();

		if (target == null) {
			return;
		}
		mh = new MovementHandler(target, this);
		heldEntities.add(target.getEntityId());

		start();
	}

	private void setFields() {
		this.cooldown = Main.plugin.getConfig().getLong("Abilities.Spirits.Water.Purify.Cooldown");
		this.duration = Main.plugin.getConfig().getLong("Abilities.Spirits.Water.Purify.Duration");
		this.range = Main.plugin.getConfig().getDouble("Abilities.Spirits.Water.Purify.Range");
		this.setElement = Main.plugin.getConfig().getBoolean("Abilities.Spirits.Water.Purify.SetElement");
		this.switchpercent = Main.plugin.getConfig().getDouble("Abilities.Spirits.Water.Purify.SwitchPercent");
	}

	public double calculateSize(LivingEntity entity) {
		return (entity.getEyeLocation().distance(entity.getLocation()) / 2 + 0.8D);
	}

	@Override
	public void remove() {
		super.remove();

		if (target != null) {
			heldEntities.remove(target.getEntityId());
		}
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Purify";
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
		return true;
	}

	@Override
	public void progress() {
		if (!bPlayer.canBendIgnoreCooldowns(this)) {
			remove();
			return;
		}

		if (target == null || target.isDead()) {
			remove();
			return;
		}

		if (!target.getWorld().equals(player.getWorld())) {
			remove();
			return;
		}

		if (target.getLocation().distance(player.getLocation()) > range) {
			remove();
			return;
		}
		if (!player.isSneaking()) {
			bPlayer.addCooldown(this);
			remove();
			return;
		}

		Entity e = GeneralMethods.getTargetedEntity(player, range);
		if (!(e instanceof LivingEntity) || e.getEntityId() != target.getEntityId()) {
			bPlayer.addCooldown(this);
			remove();
			return;
		}

		if (GeneralMethods.isRegionProtectedFromBuild(this, target.getLocation()) || ((target instanceof Player) && Commands.invincible.contains(((Player) target).getName()))) {
			remove();
			return;
		}

		if (System.currentTimeMillis() - starttime > (duration - (switchpercent * duration))) {
			createNewSpirals();

			paralyze(target);
		} else {
			createSpirals();
		}

		if (System.currentTimeMillis() - starttime > duration) {
			finish();
			remove();
			bPlayer.addCooldown(this);
			return;
		}

		ticks++;
		Long chargingTime = System.currentTimeMillis() - getStartTime();
		this.chargeTicks = (int) (chargingTime / 25);

	}

	private void finish() {
		if (target instanceof Player && setElement) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) target);
			if (bPlayer.hasElement(SpiritElement.DARK_SPIRIT)) {
				bPlayer.getElements().set(bPlayer.getElements().indexOf(SpiritElement.DARK_SPIRIT), SpiritElement.LIGHT_SPIRIT);
				GeneralMethods.saveElements(bPlayer);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent(player, (Player)target, SpiritElement.LIGHT_SPIRIT, PlayerChangeElementEvent.Result.CHOOSE));
				target.sendMessage(SpiritElement.LIGHT_SPIRIT.getColor() + "You are now a" + ChatColor.BOLD + "" + ChatColor.AQUA + " LightSpirit");
				ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.1F);
			} else {
				target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
				target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
				ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.1F);
			}
		} else {
			target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
			target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
			ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.1F);
		}
		mh.reset();
	}

	public void paralyze(LivingEntity entity) {
		if (!targetfroze) {
			if (entity instanceof Creature) {
				((Creature) entity).setTarget(null);
			}

			if (entity instanceof Player) {
				if (Suffocate.isChannelingSphere((Player) entity)) {
					Suffocate.remove((Player) entity);
				}
			}
			mh.stop(ChatColor.YELLOW + "* PURIFYING *");
			targetfroze = true;
		}
	}

	private void createSpirals() {
		if (hasReached) {
			int amount = chargeTicks + 2;
			double maxHeight = 4;
			double distanceFromPlayer = 1.5;

			int angle = 5 * amount + 5 * ticks;
			double x = Math.cos(Math.toRadians(angle)) * distanceFromPlayer;
			double z = Math.sin(Math.toRadians(angle)) * distanceFromPlayer;
			double height = (amount * 0.10) % maxHeight;
			Location displayLoc = target.getLocation().clone().add(x, height, z);

			int angle2 = 5 * amount + 180 + 5 * ticks;
			double x2 = Math.cos(Math.toRadians(angle2)) * distanceFromPlayer;
			double z2 = Math.sin(Math.toRadians(angle2)) * distanceFromPlayer;
			Location displayLoc2 = target.getLocation().clone().add(x2, height, z2);
			GeneralMethods.displayColoredParticle("42aaf4", displayLoc2);
			GeneralMethods.displayColoredParticle("42aaf4", displayLoc);
			GeneralMethods.displayColoredParticle("70ddff", displayLoc2);
			GeneralMethods.displayColoredParticle("70ddff", displayLoc);
			//todo: check here too
			ParticleEffect.SPELL_MOB.display(displayLoc2, 0, 66 / 255D, 170 / 255D, 244 / 255D, 1);
			ParticleEffect.SPELL_MOB.display(displayLoc, 0, 66 / 255D, 170 / 255D, 244 / 255D, 1);
		}
	}

	private void createNewSpirals() {
		if (hasReached) {
			int amount = chargeTicks + 2;
			double maxHeight = 4;
			double distanceFromPlayer = 1.5;

			int angle = 5 * amount + 5 * ticks;
			double x = Math.cos(Math.toRadians(angle)) * distanceFromPlayer;
			double z = Math.sin(Math.toRadians(angle)) * distanceFromPlayer;
			double height = (amount * 0.10) % maxHeight;
			Location displayLoc = target.getLocation().clone().add(x, height, z);

			int angle2 = 5 * amount + 180 + 5 * ticks;
			double x2 = Math.cos(Math.toRadians(angle2)) * distanceFromPlayer;
			double z2 = Math.sin(Math.toRadians(angle2)) * distanceFromPlayer;
			Location displayLoc2 = target.getLocation().clone().add(x2, height, z2);
			GeneralMethods.displayColoredParticle("faff9b", displayLoc2);
			GeneralMethods.displayColoredParticle("faff9b", displayLoc);
			GeneralMethods.displayColoredParticle("f6ff5e", displayLoc2);
			GeneralMethods.displayColoredParticle("f6ff5e", displayLoc);
		}
	}

	@Override
	public String getAuthor() {
		return "Prride";
	}

	@Override
	public String getDescription() {
		return Main.plugin.getConfig().getString("Language.Abilities.Water.Purify.Description");
	}

	@Override
	public String getInstructions() {
		return Main.plugin.getConfig().getString("Language.Abilities.Water.Purify.Instructions");
	}

	@Override
	public String getVersion() {
		return Methods.getVersion();
	}

	@Override
	public boolean isEnabled() {
		return Main.plugin.getConfig().getBoolean("Abilities.Spirits.Water.Purify.Enabled");
	}

	@Override
	public void load() {
	}

	@Override
	public void stop() {
	}

}
