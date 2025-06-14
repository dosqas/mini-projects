using System.Collections.Generic;

public static class CharactersData
{
    public static List<Character> Characters { get; } = new List<Character>
    {
        new Character
        {
            Id = 1,
            Nume = "Mage",
            Poza = "/images/mage.jpg",
            Abilitati = new Abilitati { Health = 70, Armor = 30, Mana = 100 }
        },
        new Character
        {
            Id = 2,
            Nume = "Archer",
            Poza = "/images/archer.png",
            Abilitati = new Abilitati { Health = 80, Armor = 40, Mana = 60 }
        },
        new Character
        {
            Id = 3,
            Nume = "Rogue",
            Poza = "/images/rogue.png",
            Abilitati = new Abilitati { Health = 75, Armor = 35, Mana = 50 }
        },
        new Character
        {
            Id = 4,
            Nume = "Cleric",
            Poza = "/images/cleric.png",
            Abilitati = new Abilitati { Health = 85, Armor = 50, Mana = 90 }
        },
        new Character
        {
            Id = 5,
            Nume = "Paladin",
            Poza = "/images/paladin.jpg",
            Abilitati = new Abilitati { Health = 95, Armor = 80, Mana = 70 }
        }
    };
}