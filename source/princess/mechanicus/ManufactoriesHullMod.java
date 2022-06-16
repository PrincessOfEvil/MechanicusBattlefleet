package source.princess.mechanicus;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class ManufactoriesHullMod extends BaseHullMod
	{
	public static final float AMMO_BOOST = 200f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id)
		{
		float bays = stats.getNumFighterBays().getBaseValue();
		stats.getNumFighterBays().modifyFlat(id, -bays);

		stats.getBallisticAmmoBonus().modifyPercent(id, AMMO_BOOST);
		stats.getEnergyAmmoBonus().modifyPercent(id, AMMO_BOOST);
		stats.getMissileAmmoBonus().modifyPercent(id, AMMO_BOOST);
		}
		
	@Override
	public String getDescriptionParam(int index, HullSize hullSize)
		{
		switch (index)
			{
			case 0:
				return "flesh is weak";
			case 1:
				return "all";
			case 2:
				return "" + (int) Math.round(AMMO_BOOST) + "%";
			default:
				return null;
			}
		}
	}
