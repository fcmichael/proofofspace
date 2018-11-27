package core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum ConsoleCode {
    FINISH(0),
    PRINT_BLOCKCHAIN(1),
    ADD_NEW_BLOCK(2),
    UNKNOWN(-1);

    private final int id;

    public static ConsoleCode fromId(int id) {
        for (ConsoleCode type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
