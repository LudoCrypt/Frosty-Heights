package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.EntityMixin;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link EntityMixin} to tell if an entity can
 *         be pushed by wind.
 *
 */
public interface EntityPushableViaWindAccess {

	public boolean isPushableViaWind();

	public void setPushableViaWind(boolean pushable);

}
