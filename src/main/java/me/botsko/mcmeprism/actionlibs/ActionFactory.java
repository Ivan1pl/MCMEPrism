package me.botsko.mcmeprism.actionlibs;

import java.util.Map;
import me.botsko.mcmeprism.actions.BannerAction;

import me.botsko.mcmeprism.actions.BlockAction;
import me.botsko.mcmeprism.actions.BlockChangeAction;
import me.botsko.mcmeprism.actions.BlockShiftAction;
import me.botsko.mcmeprism.actions.EntityAction;
import me.botsko.mcmeprism.actions.EntityTravelAction;
import me.botsko.mcmeprism.actions.GrowAction;
import me.botsko.mcmeprism.actions.Handler;
import me.botsko.mcmeprism.actions.HangingItemAction;
import me.botsko.mcmeprism.actions.ItemStackAction;
import me.botsko.mcmeprism.actions.PlayerAction;
import me.botsko.mcmeprism.actions.PlayerDeathAction;
import me.botsko.mcmeprism.actions.PrismProcessAction;
import me.botsko.mcmeprism.actions.PrismRollbackAction;
import me.botsko.mcmeprism.actions.SignAction;
import me.botsko.mcmeprism.actions.UseAction;
import me.botsko.mcmeprism.actions.VehicleAction;
import me.botsko.mcmeprism.appliers.PrismProcessType;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class ActionFactory {

    /**
     * GenericAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createBlock(String action_type, String player) {
        final BlockAction a = new BlockAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        return a;
    }

    /**
     * BlockAction
     *
     * @param action_type
     * @param block
     * @param player
     */
    public static Handler createBlock(String action_type, Block block, String player) {
        final BlockAction a = new BlockAction();
        a.setActionType( action_type );
        a.setBlock( block );
        a.setPlayerName( player );
        return a;
    }

    /**
     * BlockAction
     *
     * @param action_type
     * @param state
     * @param player
     */
    public static Handler createBlock(String action_type, BlockState state, String player) {
        final BlockAction a = new BlockAction();
        a.setActionType( action_type );
        a.setBlock( state );
        a.setPlayerName( player );
        return a;
    }

    /**
     * BlockChangeAction | WorldeditAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createBlockChange(String action_type, Location loc, int oldId, byte oldSubid, int newId, byte newSubid,
                                            String player) {
        final BlockChangeAction a = new BlockChangeAction();
        a.setActionType( action_type );
        a.setBlockId( newId );
        a.setBlockSubId( newSubid );
        a.setOldBlockId( oldId );
        a.setOldBlockSubId( oldSubid );
        a.setPlayerName( player );
        a.setLoc( loc );
        return a;
    }

    /**
     * BlockShiftAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createBlockShift(String action_type, Block from, Location to, String player) {
        final BlockShiftAction a = new BlockShiftAction();
        a.setActionType( action_type );
        a.setBlock( from );
        a.setPlayerName( player );
        a.setToLocation( to );
        return a;
    }

    /**
     * EntityAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createEntity(String action_type, Entity entity, String player) {
        return ActionFactory.createEntity(action_type, entity, player, null);
    }

    public static Handler createEntity(String action_type, Entity entity, String player, String dyeUsed) {
        final EntityAction a = new EntityAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setEntity( entity, dyeUsed );
        return a;
    }

    /**
     * EntityTravelAction
     * 
     * @param action_type
     */
    public static Handler createEntityTravel(String action_type, Entity entity, Location from, Location to, TeleportCause cause) {
        final EntityTravelAction a = new EntityTravelAction();
        a.setEntity( entity );
        a.setActionType( action_type );
        a.setLoc( from );
        a.setToLocation( to );
        a.setCause( cause );
        return a;
    }

    /**
     * GrowAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createGrow(String action_type, BlockState blockstate, String player) {
        final GrowAction a = new GrowAction();
        a.setActionType( action_type );
        a.setBlock( blockstate );
        a.setPlayerName( player );
        return a;
    }

    /**
     * HangingItemAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createHangingItem(String action_type, Hanging hanging, String player) {
        final HangingItemAction a = new HangingItemAction();
        a.setActionType( action_type );
        a.setHanging( hanging );
        a.setPlayerName( player );
        return a;
    }

    /**
     * ItemStackAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createItemStack(String action_type, ItemStack item, Map<Enchantment, Integer> enchantments,
                                          Location loc, String player) {
        return ActionFactory.createItemStack(action_type, item, 1, -1, enchantments, loc, player);
    }

    public static Handler createItemStack(String action_type, ItemStack item, int quantity, int slot,
                                          Map<Enchantment, Integer> enchantments, Location loc, String player) {
        final ItemStackAction a = new ItemStackAction();
        a.setActionType( action_type );
        a.setLoc( loc );
        a.setPlayerName( player );
        a.setItem( item, quantity, slot, enchantments );
        return a;
    }

    /**
     * PlayerAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createPlayer(String action_type, Player player, String additionalInfo) {
        final PlayerAction a = new PlayerAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( player.getLocation() );
        a.setData( additionalInfo );
        return a;
    }

    /**
     * PlayerDeathAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createPlayerDeath(String action_type, Player player, String cause, String attacker) {
        final PlayerDeathAction a = new PlayerDeathAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( player.getLocation() );
        a.setCause( cause );
        a.setAttacker( attacker );
        return a;
    }

    /**
     * PrismProcessActionData
     * 
     * @param action_type
     * @param player
     */
    public static Handler createPrismProcess(String action_type, PrismProcessType processType, Player player, String parameters) {
        final PrismProcessAction a = new PrismProcessAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( player.getLocation() );
        a.setProcessData( processType, parameters );
        return a;
    }

    /**
     * PrismRollbackAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createPrismRollback(String action_type, BlockState oldblock, BlockState newBlock, String player,
                                              int parent_id) {
        final PrismRollbackAction a = new PrismRollbackAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( oldblock.getLocation() );
        a.setBlockChange( oldblock, newBlock, parent_id );
        return a;
    }

    /**
     * SignAction
     * 
     * @param action_type
     * @param block
     * @param player
     */
    public static Handler createSign(String action_type, Block block, String[] lines, String player) {
        final SignAction a = new SignAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setBlock( block, lines );
        return a;
    }
    
    /**
     * BannerAction
     * 
     * @param action_type
     * @param block
     * @param player
     */
    public static Handler createBanner(String action_type, Block block, String[] lines, String player) {
        final BannerAction a = new BannerAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setBlock( block, lines );
        return a;
    }

    /**
     * UseAction
     * 
     * @param action_type
     * @param block
     * @param player
     */
    public static Handler createUse(String action_type, String item_used, Block block, String player) {
        final UseAction a = new UseAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( block.getLocation() );
        a.setData( item_used );
        return a;
    }

    /**
     * VehicleAction
     * 
     * @param action_type
     * @param player
     */
    public static Handler createVehicle(String action_type, Vehicle vehicle, String player) {
        final VehicleAction a = new VehicleAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( vehicle.getLocation() );
        a.setVehicle( vehicle );
        return a;
    }

    // Backwards compat:

    @Deprecated
    public static Handler create(String action_type, String player) {
        return createBlock(action_type, player);
    }

    @Deprecated
    public static Handler create(String action_type, Block block, String player) {
        return createBlock(action_type, block, player);
    }

    @Deprecated
    public static Handler create(String action_type, Location loc, int oldId, byte oldSubid, int newId, byte newSubid,
                                 String player) {
        return createBlockChange(action_type, loc, oldId, oldSubid, newId, newSubid, player);
    }

    @Deprecated
    public static Handler create(String action_type, Block from, Location to, String player) {
        return createBlockShift(action_type, from, to, player);
    }

    @Deprecated
    public static Handler create(String action_type, Entity entity, String player) {
        return createEntity( action_type, entity, player);
    }

    public static Handler create(String action_type, Entity entity, String player, String dyeUsed) {
        return createEntity(action_type, entity, player, dyeUsed);
    }

    @Deprecated
    public static Handler create(String action_type, Entity entity, Location from, Location to, TeleportCause cause) {
        return createEntityTravel(action_type, entity, from, to, cause);
    }

    @Deprecated
    public static Handler create(String action_type, BlockState blockstate, String player) {
        return createGrow(action_type, blockstate, player);
    }

    @Deprecated
    public static Handler create(String action_type, Hanging hanging, String player) {
        return createHangingItem(action_type, hanging, player);
    }

    @Deprecated
    public static Handler create(String action_type, ItemStack item, Map<Enchantment, Integer> enchantments,
                                 Location loc, String player) {
        return createItemStack(action_type, item, enchantments, loc, player);
    }

    @Deprecated
    public static Handler create(String action_type, ItemStack item, int quantity, int slot,
                                 Map<Enchantment, Integer> enchantments, Location loc, String player) {
        return createItemStack(action_type, item, quantity, slot, enchantments, loc, player);
    }

    @Deprecated
    public static Handler create(String action_type, Player player, String additionalInfo) {
        return createPlayer(action_type, player, additionalInfo);
    }

    @Deprecated
    public static Handler create(String action_type, Player player, String cause, String attacker) {
        return createPlayerDeath(action_type, player, cause, attacker);
    }

    @Deprecated
    public static Handler create(String action_type, PrismProcessType processType, Player player, String parameters) {
        final PrismProcessAction a = new PrismProcessAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( player.getLocation() );
        a.setProcessData( processType, parameters );
        return a;
    }

    @Deprecated
    public static Handler create(String action_type, BlockState oldblock, BlockState newBlock, String player,
                                 int parent_id) {
        final PrismRollbackAction a = new PrismRollbackAction();
        a.setActionType( action_type );
        a.setPlayerName( player );
        a.setLoc( oldblock.getLocation() );
        a.setBlockChange( oldblock, newBlock, parent_id );
        return a;
    }

    @Deprecated
    public static Handler create(String action_type, Block block, String[] lines, String player) {
        return createSign(action_type, block, lines, player);
    }

    @Deprecated
    public static Handler create(String action_type, String item_used, Block block, String player) {
        return createUse(action_type, item_used, block, player);
    }

    @Deprecated
    public static Handler create(String action_type, Vehicle vehicle, String player) {
        return createVehicle(action_type, vehicle, player);
    }
}