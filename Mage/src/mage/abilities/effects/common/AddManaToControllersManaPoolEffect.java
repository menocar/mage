/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mage.abilities.effects.common;

import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author magenoxx
 */
public class AddManaToControllersManaPoolEffect extends OneShotEffect {

    protected Mana mana;

    public AddManaToControllersManaPoolEffect(Mana mana) {
        super(Outcome.PutManaInPool);
        this.mana = mana;
        this.staticText = "Add " + mana.toString() + " to your mana pool";
    }

    public AddManaToControllersManaPoolEffect(final AddManaToControllersManaPoolEffect effect) {
        super(effect);
        this.mana = effect.mana;
    }

    @Override
    public AddManaToControllersManaPoolEffect copy() {
        return new AddManaToControllersManaPoolEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            player.getManaPool().addMana(mana, game, source);
            return true;
        }
        return false;
    }
}