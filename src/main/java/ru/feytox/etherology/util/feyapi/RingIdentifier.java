package ru.feytox.etherology.util.feyapi;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.enums.RingType;

import java.util.List;
import java.util.stream.Collectors;

public class RingIdentifier extends Identifier {
    private int ringsNum;
    private List<RingType> ringsTypes;

    public RingIdentifier(Identifier identifier, int ringsNum, List<Integer> ringsTypes) {
        super(identifier.toString());
        this.ringsNum = ringsNum;
        this.ringsTypes = ringsTypes.stream().map(RingType::getRingType).collect(Collectors.toList());;
    }

    public RingIdentifier(Identifier identifier, List<RingType> ringsTypes, int ringsNum) {
        super(identifier.toString());
        this.ringsNum = ringsNum;
        this.ringsTypes = ringsTypes;
    }

    public int getRingsNum() {
        return ringsNum;
    }

    public List<RingType> getRingsTypes() {
        return ringsTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (!(o instanceof RingIdentifier id)) return false;

        return ringsTypes.equals(id.getRingsTypes()) && ringsNum == id.getRingsNum();
    }
}
