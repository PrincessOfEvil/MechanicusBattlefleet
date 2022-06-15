package source.princess.mechanicus;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class MechanicusHullMod extends BaseHullMod
	{
	public static final float	DEPLOYMENT_COST	= 1.5f;
	
	public static final float	REPAIR_SPEED	= 2f;
	public static final float	FLUX_BOOST		= 1.2f;
	
	public static final float	RANGE_BOOST		= 50f;
	
	public static final float	CREW_MULT		= 0.5f;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id)
		{
		stats.getSuppliesToRecover().modifyMult(id, DEPLOYMENT_COST);
		stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, DEPLOYMENT_COST);
		
		stats.getSensorStrength().unmodify(HullMods.CIVGRADE);
		stats.getSensorProfile().unmodify(HullMods.CIVGRADE);
		
		stats.getRepairRatePercentPerDay().modifyMult(id, REPAIR_SPEED);
		
		stats.getFluxCapacity().modifyMult(id, FLUX_BOOST);
		stats.getFluxDissipation().modifyMult(id, FLUX_BOOST);
		
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BOOST);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BOOST);
		stats.getMissileWeaponRangeBonus().modifyPercent(id, RANGE_BOOST);
		
		stats.getMinCrewMod().modifyMult(id, CREW_MULT);
		stats.getMaxCrewMod().modifyMult(id, CREW_MULT);
		}
		
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id)
		{
		ShieldAPI shield = ship.getShield();
		if (shield != null)
			{
			shield.setType(ShieldType.OMNI);
			}
		}
		
	@Override
	public String getDescriptionParam(int index, HullSize hullSize)
		{
		switch (index)
			{
			case 0:
				return "" + (int) Math.round(CREW_MULT * 100f) + "%";
			case 1:
				return "" + (int) Math.round(FLUX_BOOST * 100f) + "%";
			case 2:
				return "" + (int) Math.round(RANGE_BOOST) + "%";
			case 3:
				return "" + (int) Math.round(REPAIR_SPEED * 100f) + "%";
			case 4:
				return "" + (int) Math.round(DEPLOYMENT_COST * 100f) + "%";
			default:
				return null;
			}
		}
	}