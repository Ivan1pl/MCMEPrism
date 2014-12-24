package me.botsko.mcmeprism.listeners;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionFactory;
import me.botsko.mcmeprism.actionlibs.RecordingQueue;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class PrismVehicleEvents implements Listener {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     */
    public PrismVehicleEvents(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleCreate(final VehicleCreateEvent event) {

        final Vehicle vehicle = event.getVehicle();
        final Location loc = vehicle.getLocation();

        final String coord_key = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        final String player = plugin.preplannedVehiclePlacement.get( coord_key );
        if( player != null ) {
            if( !MCMEPrism.getIgnore().event( "vehicle-place", loc.getWorld(), player ) )
                return;
            RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-place", vehicle, player) );
        }
    }

    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleDestroy(final VehicleDestroyEvent event) {

        final Vehicle vehicle = event.getVehicle();
        final Entity attacker = event.getAttacker();
        // Was it broken by an attack
        if( attacker != null ) {
            if( attacker instanceof Player ) {
                if( !MCMEPrism.getIgnore().event( "vehicle-break", ( (Player) attacker ) ) )
                    return;
                RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-break", vehicle,
                        ((Player) attacker).getName()) );
            } else {
                if( !MCMEPrism.getIgnore().event( "vehicle-break", attacker.getWorld() ) )
                    return;
                RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-break", vehicle, attacker.getType().name()
                        .toLowerCase()) );
            }
        } else {

            // Otherwise its driver was reckless
            final Entity passenger = vehicle.getPassenger();
            if( passenger != null && passenger instanceof Player ) {
                if( !MCMEPrism.getIgnore().event( "vehicle-break", ( (Player) passenger ) ) )
                    return;
                RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-break", vehicle,
                        ((Player) passenger).getName()) );
            }
        }
    }

    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {

        final Vehicle vehicle = event.getVehicle();

        final Entity entity = event.getEntered();
        if( entity instanceof Player ) {
            if( !MCMEPrism.getIgnore().event( "vehicle-enter", ( (Player) entity ) ) )
                return;
            RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-enter", vehicle, ((Player) entity).getName()) );
        } else {
            if( !MCMEPrism.getIgnore().event( "vehicle-enter", entity.getWorld() ) )
                return;
            RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-enter", vehicle, entity.getType().name()
                    .toLowerCase()) );
        }
    }

    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {

        final Vehicle vehicle = event.getVehicle();

        final Entity entity = event.getExited();
        if( entity instanceof Player ) {
            if( !MCMEPrism.getIgnore().event( "vehicle-enter", ( (Player) entity ) ) )
                return;
            RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-exit", vehicle, ((Player) entity).getName()) );
        } else {
            if( !MCMEPrism.getIgnore().event( "vehicle-enter", entity.getWorld() ) )
                return;
            RecordingQueue.addToQueue( ActionFactory.createVehicle("vehicle-exit", vehicle, entity.getType().name()
                    .toLowerCase()) );
        }
    }
}