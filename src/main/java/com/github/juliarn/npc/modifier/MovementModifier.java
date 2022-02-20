package com.github.juliarn.npc.modifier;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.github.juliarn.npc.NPC;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MovementModifier extends NPCModifier {

  /**
   * Creates a new npc modifier.
   *
   * @param npc The npc this modifier is for.
   */
  public MovementModifier(@NotNull NPC npc) {
    super(npc);
  }

  /**
   * Move this {@link NPC} to the specified {@link Location}
   * <strong>Note: This is a very hacky solution and a proper way to handle teleport/relative
   * movement
   * packets should be implemented! I Just need this implementation real quick!</strong>
   *
   * @param location the location
   * @return this {@link MovementModifier}
   * @since 2.8-SNAPShOT
   */
  public MovementModifier queueMovement(@NotNull Location location) {
    short deltaX = (short) ((location.getX() * 32 - npc.getLocation().getX() * 32) * 128);
    short deltaY = (short) ((location.getY() * 32 - npc.getLocation().getY() * 32) * 128);
    short deltaZ = (short) ((location.getZ() * 32 - npc.getLocation().getZ() * 32) * 128);

    byte yawAngle = (byte) (location.getYaw() * 256F / 360F);
    byte pitchAngle = (byte) (location.getPitch() * 256F / 360F);

    super.queueInstantly((targetNpc, target) -> {
      PacketContainer container = new PacketContainer(Server.ENTITY_HEAD_ROTATION);
      container.getIntegers().write(0, targetNpc.getEntityId());
      container.getBytes().write(0, yawAngle);

      return container;
    });

    super.queueInstantly((targetNpc, target) -> {
      PacketContainer container = new PacketContainer(Server.REL_ENTITY_MOVE_LOOK);
      container.getIntegers().write(0, targetNpc.getEntityId());
      container.getShorts().write(0, deltaX);
      container.getShorts().write(1, deltaY);
      container.getShorts().write(2, deltaZ);

      // fix rotation
      container.getBytes().write(0, yawAngle);
      container.getBytes().write(1, pitchAngle);

      container.getBooleans().write(0, true);

      return container;
    });

    npc.setLocation(location);

    return this;
  }

  private short getDeltaPosition(@NotNull Double from, @NotNull Double to) {
    return (short) ((from * 32 - to * 32) * 128);
  }

}
