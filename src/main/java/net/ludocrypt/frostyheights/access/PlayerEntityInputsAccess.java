package net.ludocrypt.frostyheights.access;

import net.ludocrypt.frostyheights.mixin.common.PlayerEntityMixin;

/**
 * 
 * @author LudoCrypt
 *
 *         Duck interface used in {@link PlayerEntityMixin} to get the exact
 *         input speeds from the controller.
 *
 */
public interface PlayerEntityInputsAccess {

	public float getSidewaysInputSpeed();

	public float getForwardInputSpeed();

	public void setSidewaysInputSpeed(float speed);

	public void setForwardInputSpeed(float speed);

}
