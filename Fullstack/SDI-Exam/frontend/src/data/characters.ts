import type { Character } from "@/types/Character";

export const characters: Character[] = [
  {
    id: 1,
    nume: "Mage",
    poza: "/images/mage.jpg",
    abilitati: { health: 70, armor: 30, mana: 100 }
  },
  {
    id: 2,
    nume: "Archer",
    poza: "/images/archer.png",
    abilitati: { health: 80, armor: 40, mana: 60 }
  },
  {
    id: 3,
    nume: "Rogue",
    poza: "/images/rogue.png",
    abilitati: { health: 75, armor: 35, mana: 50 }
  },
  {
    id: 4,
    nume: "Cleric",
    poza: "/images/cleric.png",
    abilitati: { health: 85, armor: 50, mana: 90 }
  },
  {
    id: 5,
    nume: "Paladin",
    poza: "/images/paladin.jpg",
    abilitati: { health: 95, armor: 80, mana: 70 }
  }
];