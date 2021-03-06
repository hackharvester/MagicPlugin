package com.elmakers.mine.bukkit.action.builtin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import com.elmakers.mine.bukkit.action.CompoundAction;
import com.elmakers.mine.bukkit.api.action.ActionHandler;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.utility.CompatibilityUtils;

public class CheckHealthAction extends CompoundAction {
    private Double minHealth;
    private Double maxHealth;
    private boolean fullHealth;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters)
    {
        super.prepare(context, parameters);
        if (parameters.contains("min_health")) {
            minHealth = parameters.getDouble("min_health");
        }
        if (parameters.contains("max_health")) {
            maxHealth = parameters.getDouble("max_health");
        }
        fullHealth = parameters.getBoolean("full_health", false);
    }

    protected boolean isAllowed(CastContext context) {
        Entity targetEntity = context.getTargetEntity();
        if (targetEntity == null || !(targetEntity instanceof Damageable)) return false;
        Damageable damageable = (Damageable)targetEntity;
        if (fullHealth && damageable.getHealth() < CompatibilityUtils.getMaxHealth(damageable)) return false;
        if (minHealth != null && damageable.getHealth() < minHealth) return false;
        if (maxHealth != null && damageable.getHealth() > maxHealth) return false;
        return true;
    }

    @Override
    public SpellResult step(CastContext context) {
        boolean allowed = isAllowed(context);
        ActionHandler actions = getHandler("actions");
        if (actions == null || actions.size() == 0) {
            return allowed ? SpellResult.CAST : SpellResult.STOP;
        }

        if (!allowed) {
            return SpellResult.NO_TARGET;
        }
        return startActions();
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public boolean requiresTargetEntity() {
        return true;
    }
}