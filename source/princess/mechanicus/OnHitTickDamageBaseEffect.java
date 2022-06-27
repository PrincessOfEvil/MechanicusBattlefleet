package source.princess.mechanicus;

import java.lang.ref.WeakReference;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.input.InputEventAPI;

public class OnHitTickDamageBaseEffect implements OnHitEffectPlugin
	{	
	/**
	 * Over the entire DoT.
	 * */
	public float getDamage()
		{
		return 1000f;
		}

	/**
	 * Seconds, I assume.
	 * */
	public float getTime()
		{
		return 5f;
		}
		
	/**
	 * Specific per DoT type: DoTs with the same ID stack, increasing total damage, and slightly increasing damage per tick.
	 *  DoTs with different IDs run at the same time, burning down ships faster.
	 * */
	public String getID()
		{
		return "basic";
		}
	
	/**
	 * Where actual magic happens.
	 * */
	public void step(float damage, TargetData data)
		{
		Global.getCombatEngine()
				.applyDamage(data.target, data.target
						.getLocation(), damage, DamageType.OTHER, 0f, true, false, data.projectile, false);
		
		/*
		if (Misc.shouldShowDamageFloaty(data.ship, data.target))
			{
			Global.getCombatEngine()
					.addFloatingDamageText(data.target.getLocation(), damage, Misc
							.getNegativeHighlightColor(), data.target, data.ship);
			}
		*/
		}
	
	/** Implementation details below this point. */
	
	public static final float EPSILON = 1e-05f;
	
	public static class TargetData
		{
		public ShipAPI									ship;
		public ShipAPI									target;
		public DamagingProjectileAPI					projectile;
		// very important: cyclic reference issues very likely, and with how often this might get called, well...
		public WeakReference<EveryFrameCombatPlugin>	targetEffectPlugin;
		public float									damageLeft;
		public float									timeLeft;
		
		public TargetData(ShipAPI ship, ShipAPI target, DamagingProjectileAPI projectile)
			{
			this.ship = ship;
			this.target = target;
			this.projectile = projectile;
			}
		}
	@Override
	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine)
		{
		if (!shieldHit && target instanceof ShipAPI && projectile.getSource() != null)
			{
			ShipAPI ship = projectile.getSource();
			ShipAPI targetShip = (ShipAPI) target;
			
			final String targetDataKey = ship.getId() + targetShip.getId() + "_BFG_" + getID();
			
			Object targetDataObj = engine.getCustomData().get(targetDataKey);
			final TargetData targetData;
			
			if (targetDataObj != null & targetDataObj instanceof TargetData)
				{
				targetData = (TargetData) targetDataObj;
				}
			else
				{
				targetData = new TargetData(ship, targetShip, projectile);
				engine.getCustomData().put(targetDataKey, targetData);
				}
			if (targetData.targetEffectPlugin == null)
				{
				targetData.targetEffectPlugin = new WeakReference<EveryFrameCombatPlugin>(new BaseEveryFrameCombatPlugin()
					{
					@Override
					public void advance(float amount, List<InputEventAPI> events)
						{
						if (Global.getCombatEngine().isPaused()) return;
						if (targetData.damageLeft < EPSILON)
							{
							Global.getCombatEngine().removePlugin(this);
							targetData.targetEffectPlugin.clear();
							targetData.targetEffectPlugin = null;
							return;
							}
							
						float damage = targetData.damageLeft * amount / targetData.timeLeft;
						
						step(damage, targetData);
						
						targetData.timeLeft -= amount;
						targetData.damageLeft -= damage;
						}
					});
				
				engine.addPlugin((BaseEveryFrameCombatPlugin)targetData.targetEffectPlugin.get());
				}
				
			if (targetData.damageLeft < EPSILON)
				{
				targetData.damageLeft = getDamage();
				targetData.timeLeft = getTime();
				}
			else
				{
				targetData.damageLeft += getDamage();
				targetData.timeLeft += getTime() / 2;
				}
			}
		}
	}
