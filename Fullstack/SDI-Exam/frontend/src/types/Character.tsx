export interface Character {
  id: number;
  nume: string;
  poza: string;
  abilitati: {
    health: number;
    armor: number;
    mana: number;
  };
}