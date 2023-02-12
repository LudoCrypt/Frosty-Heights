package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.climbing.ClimbingMovementHandler;
import net.ludocrypt.frostyheights.mixin.common.PlayerEntityMixin;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link PlayerEntityMixin} to tell if a player
 *         is attached to a wall with a climbing pickaxe.
 *
 */
public interface PlayerEntityPickAttachedAccess {

	public boolean isPickAttached();

	public void setPickAttached(boolean pickAttached);

	public ClimbingMovementHandler getMovementHandler();

}
