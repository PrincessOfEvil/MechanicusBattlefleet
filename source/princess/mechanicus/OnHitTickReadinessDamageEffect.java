package source.princess.mechanicus;

public class OnHitTickReadinessDamageEffect extends OnHitTickDamageBaseEffect
	{
	@Override
	public float getDamage()
		{
		return 0.15f;
		}

	@Override
	public float getTime()
		{
		return 10f;
		}

	@Override
	public String getID()
		{
		return "readiness";
		}

	@Override
	public void step(float damage, TargetData data)
		{
		if (data.target.losesCRDuringCombat())
			data.target.setCurrentCR(data.target.getCurrentCR() - damage);
		}
	}
