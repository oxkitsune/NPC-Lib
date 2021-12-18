package com.github.juliarn.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.github.juliarn.npc.NPC;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MovementModifier extends NPCModifier{

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
   * <strong>Note: This is a very hacky solution and a proper way to handle teleport/relative movement
   * packets should be implemented! I Just need this implementation real quick!</strong>
   *
   * @param location the location
   *
   * @return this {@link MovementModifier}
   * @since 2.8-SNAPShOT
   */
  public MovementModifier queueMovement (@NotNull Location location) {

    super.queuePacket((targetNpc, target)  -> {

      PacketContainer container = new PacketContainer(Server.REL_ENTITY_MOVE);

      container.getIntegers().write(0, targetNpc.getEntityId());
      container.getShorts().write(0, getDeltaPosition(targetNpc.getLocation().getX(), location.getX()));
      container.getShorts().write(1, getDeltaPosition(targetNpc.getLocation().getY(), location.getY()));
      container.getShorts().write(2, getDeltaPosition(targetNpc.getLocation().getZ(), location.getZ()));
      container.getBooleans().write(0, true);

      return container;
    });

    npc.setLocation(location);

    return this;
  }

  private short getDeltaPosition (@NotNull Double from, @NotNull Double to) {
    return (short) ((from * 32 - to * 32) * 128);
  }

}
