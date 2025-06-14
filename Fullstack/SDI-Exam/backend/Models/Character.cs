using Microsoft.EntityFrameworkCore;

public class Character
{
    public int Id { get; set; }
    public string Nume { get; set; }
    public string Poza { get; set; }
    public Abilitati Abilitati { get; set; }
    public CharacterPosition CharacterPosition { get; set; }

}

[Owned]
public class Abilitati
{
    public float Health { get; set; }
    public float Armor { get; set; }
    public float Mana { get; set; }
}