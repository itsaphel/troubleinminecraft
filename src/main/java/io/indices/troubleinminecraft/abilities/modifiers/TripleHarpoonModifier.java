package io.indices.troubleinminecraft.abilities.modifiers;

import io.indices.troubleinminecraft.abilities.HarpoonAbility;
import io.indices.troubleinminecraft.abilities.TTTAbility;

public class TripleHarpoonModifier extends AbilityModifier {

    @Override
    public void affect(TTTAbility ability) {
        if (ability instanceof HarpoonAbility) {
            ((HarpoonAbility) ability).setQuantity(3);
        }
    }
}
