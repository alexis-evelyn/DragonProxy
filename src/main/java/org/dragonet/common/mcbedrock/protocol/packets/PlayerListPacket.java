package org.dragonet.common.mcbedrock.protocol.packets;

import org.dragonet.common.mcbedrock.protocol.PEPacket;
import org.dragonet.common.mcbedrock.protocol.ProtocolInfo;
import org.dragonet.common.mcbedrock.protocol.type.PlayerListEntry;
import org.dragonet.common.mcbedrock.protocol.type.Skin;

/**
 * Created on 2017/10/22.
 */
public class PlayerListPacket extends PEPacket {

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;
    public byte type;
    public PlayerListEntry[] entries;

    public PlayerListPacket() {

    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_LIST_PACKET;
    }

    @Override
    public void encodePayload() {
        putByte(type);
        if (entries != null && entries.length > 0) {
            putUnsignedVarInt(entries.length);
            for (PlayerListEntry e : entries) {
                putUUID(e.uuid);
                if (type == TYPE_ADD) {
                    putUnsignedVarLong(e.eid);
                    putString(e.username);
                    e.skin.write(this);
                    putString(e.xboxUserId);
                }
            }
        } else {
            putUnsignedVarInt(0);
        }
    }

    @Override
    public void decodePayload() {
        type = (byte) (getByte() & 0xFF);
        int len = (int) getUnsignedVarInt();
        entries = new PlayerListEntry[len];
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                entries[i] = new PlayerListEntry();
                entries[i].uuid = getUUID();
                if (type == TYPE_ADD) {
                    entries[i].eid = getVarLong();
                    entries[i].username = getString();
                    entries[i].skin = Skin.read(this);
                    entries[i].xboxUserId = getString();
                }
            }
        }
    }
}