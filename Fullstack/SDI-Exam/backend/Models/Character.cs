using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;

public class Character
{
    public int Id { get; set; }
    public string Nume { get; set; }
    public string Poza { get; set; }
    public float Health { get; set; }
    public float Armor { get; set; }
    public float Mana { get; set; }
    [JsonIgnore]
    public CharacterPosition CharacterPosition { get; set; }

}