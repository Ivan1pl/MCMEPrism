/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.botsko.mcmeprism.actions;

import java.util.ArrayList;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.appliers.ChangeResult;
import me.botsko.mcmeprism.appliers.ChangeResultType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import us.dhmc.mcmeelixr.TypeUtils;

/**
 *
 * @author Ivan1pl
 */
public class BannerAction extends GenericAction {

    public class BannerChangeActionData {
        public String[] lines;
        public String banner_type;
        public BlockFace facing;
    }

    /**
	 * 
	 */
    protected Block block;

    /**
	 * 
	 */
    protected BannerChangeActionData actionData;

    /**
     * 
     * @param block
     * @param lines
     */
    public void setBlock(Block block, String[] lines) {

        // Build an object for the specific details of this action
        actionData = new BannerChangeActionData();

        if( block != null ) {
            actionData.banner_type = block.getType().name();
            final org.bukkit.material.Banner banner = (org.bukkit.material.Banner) block.getState().getData();
            actionData.facing = banner.getFacing();
            this.block = block;
            this.world_name = block.getWorld().getName();
            this.x = block.getX();
            this.y = block.getY();
            this.z = block.getZ();
        }
        if( lines != null ) {
            actionData.lines = lines;
        }
    }

    /**
	 * 
	 */
    @Override
    public void setData(String data) {
        this.data = data;
        if( data != null && !this.data.isEmpty() ) {
            actionData = gson.fromJson( data, BannerChangeActionData.class );
        }
    }

    /**
	 * 
	 */
    @Override
    public void save() {
        data = gson.toJson( actionData );
    }

    /**
     * 
     * @return
     */
    public String[] getLines() {
        return actionData.lines;
    }

    /**
     * 
     * @return
     */
    public Material getBannerType() {
        if( actionData.banner_type != null ) {
            final Material m = Material.valueOf( actionData.banner_type );
            if( m != null ) { return m; }
        }
        return Material.BANNER;
    }

    /**
     * 
     * @return
     */
    public BlockFace getFacing() {
        return actionData.facing;
    }

    /**
     * 
     * @return
     */
    @Override
    public String getNiceName() {
        String name = "banner (";
        if( actionData.lines != null && actionData.lines.length > 0 ) {
            name += TypeUtils.join( actionData.lines, ", " );
        } else {
            name += "no text";
        }
        name += ")";
        return name;
    }

    /**
	 * 
	 */
    @Override
    public ChangeResult applyRestore(Player player, QueryParameters parameters, boolean is_preview) {

        final Block block = getWorld().getBlockAt( getLoc() );

        // Ensure a banner exists there (and no other block)
        if( block.getType().equals( Material.AIR ) || block.getType().equals( Material.BANNER )
                || block.getType().equals( Material.STANDING_BANNER ) || block.getType().equals( Material.WALL_BANNER ) ) {

            if( block.getType().equals( Material.AIR ) ) {
                block.setType( getBannerType() );
            }

            // Set the facing direction
            if( block.getState().getData() instanceof org.bukkit.material.Banner ) {
                final org.bukkit.material.Banner s = (org.bukkit.material.Banner) block.getState().getData();
                s.setFacingDirection( getFacing() );
            }
            // Set the content
            if( block.getState() instanceof org.bukkit.block.Banner ) {

                // Set banner data
                final String[] lines = getLines();
                final org.bukkit.block.Banner banner = (org.bukkit.block.Banner) block.getState();
                int i = 0;
                if( lines != null && lines.length > 0 ) {
                    ArrayList <Pattern> patterns = new ArrayList<Pattern>();
                    DyeColor tmpcolor = DyeColor.BLACK;
                    PatternType tmppattern;
                    for ( final String line : lines ) {
                        if(i == 0) {
                            banner.setBaseColor(DyeColor.valueOf(line));
                        }
                        else if(i % 2 == 1) {
                            tmpcolor = DyeColor.valueOf(line);
                        }
                        else {
                            tmppattern = PatternType.valueOf(line);
                            patterns.add(new Pattern(tmpcolor, tmppattern));
                        }
                        i++;
                    }
                    banner.setPatterns(patterns);
                }
                banner.update();
                return new ChangeResult( ChangeResultType.APPLIED, null );
            }
        }
        return new ChangeResult( ChangeResultType.SKIPPED, null );
    }
}