package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.EntityMixin;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link EntityMixin} to tell how long an entity
 *         has been on a block of Phantom Ice.
 *
 */
public interface EntityPushableViaWindAccess {

	public boolean isPushableViaWind();

	public void setPushableViaWind(boolean pushable);

}
