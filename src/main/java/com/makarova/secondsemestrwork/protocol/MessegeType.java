package com.makarova.secondsemestrwork.protocol;

import java.awt.*;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessegeType {
    public static final int PLAYER_CONNECTION_TYPE = 1;
    public static final int GAME_START_TYPE = 2;
    public static final int SET_PLAYER_POSITION_TYPE = 3;
    public static final int PLAYER_POSITION_UPDATE_TYPE = 4;

    public static List<Integer> getAllTypes() {
        return Arrays.asList(
                PLAYER_CONNECTION_TYPE,
                GAME_START_TYPE,
                SET_PLAYER_POSITION_TYPE,
                PLAYER_POSITION_UPDATE_TYPE
        );
    }
}
