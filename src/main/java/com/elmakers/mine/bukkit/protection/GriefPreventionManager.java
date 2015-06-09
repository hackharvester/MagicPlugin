package com.elmakers.mine.bukkit.protection;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GriefPreventionManager implements BlockBuildManager, BlockBreakManager  {
	private boolean enabled = false;
	private GriefPreventionAPI api = null;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled && api != null;
	}

	public void initialize(Plugin plugin) {
		if (enabled) {
			try {
				Plugin griefPlugin = plugin.getServer().getPluginManager().getPlugin("GriefPrevention");
				if (griefPlugin != null) {
                    api = new GriefPreventionAPI(griefPlugin);
				}
			} catch (Throwable ex) {
			}

			if (api == null) {
				plugin.getLogger().info("GriefPrevention not found, claim protection will not be used.");
			} else {
				plugin.getLogger().info("GriefPrevention found, will respect claim build permissions for construction spells");
			}
		} else {
			plugin.getLogger().info("GriefPrevention manager disabled, claim protection will not be used.");
            api = null;
		}
	}

	public boolean hasBuildPermission(Player player, Block block) {
		if (enabled && block != null && api != null) {
			return api.hasBuildPermission(player, block);
		}
		return true;
	}

    @Override
    public boolean hasBreakPermission(Player player, Block block) {
        return hasBuildPermission(player, block);
    }
}
