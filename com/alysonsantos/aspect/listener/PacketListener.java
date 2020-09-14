package com.alysonsantos.aspect.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.plugin.Plugin;

public class PacketListener extends PacketAdapter {

    public PacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        if (packetContainer.getIntegers().read(0) == 1) {
            NbtCompound nbtCompound = (NbtCompound) packetContainer.getNbtModifier().read(0);
            if (nbtCompound.getKeys().contains("EntityId")) {
                nbtCompound.put("EntityId", "null");
            }
            if (nbtCompound.getKeys().contains("SpawnData")) {
                nbtCompound.put("SpawnData", nbtCompound.getCompound("SpawnData").put("id", "null"));
            }
        }
    }
}
