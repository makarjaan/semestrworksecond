package com.makarova.secondsemestrwork.protocol;

import java.util.Arrays;
import java.util.List;

public class MessageType {
    public static final int PLAYER_CONNECTION_TYPE = 1;
    public static final int GAME_START_TYPE = 2;
    public static final int SET_PLAYER_POSITION_TYPE = 3;
    public static final int PLAYER_POSITION_UPDATE_TYPE = 4;
    public static final int INIT_ROCKET_TYPE = 5;
    public static final int GENERATE_ROCKET_TYPE = 6;
    public static final int BULLET_UPDATE_TYPE = 7;
    public static final int PISTOL_UPDATE_TYPE = 8;
    public static List<Integer> getAllTypes() {
        return Arrays.asList(
                PLAYER_CONNECTION_TYPE,
                GAME_START_TYPE,
                SET_PLAYER_POSITION_TYPE,
                PLAYER_POSITION_UPDATE_TYPE,
                INIT_ROCKET_TYPE,
                GENERATE_ROCKET_TYPE,
                BULLET_UPDATE_TYPE,
                PISTOL_UPDATE_TYPE
        );
    }
}
