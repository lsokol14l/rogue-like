package reneakar.model.entities.behaviors;

import reneakar.model.entities.Character;


public interface AttackerCharacter {

  boolean canAttack(Character target);

  boolean probabilityAttack(Character character);

  int calcDamage(Character target);

  void attack(Character target);
}


