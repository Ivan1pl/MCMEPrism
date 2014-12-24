package me.botsko.mcmeprism.appliers;

import org.bukkit.command.CommandSender;

public interface ApplierCallback {
    public void handle(CommandSender sender, ApplierResult result);
}
