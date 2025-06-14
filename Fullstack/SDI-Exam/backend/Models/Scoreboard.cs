using System.ComponentModel.DataAnnotations;

public class Scoreboard
{
    public int Id { get; set; }
    
    [Required]
    public int CharacterId { get; set; }
    
    public Character Character { get; set; }
    
    public int KillCount { get; set; } = 0;
    
    public DateTime LastUpdated { get; set; } = DateTime.UtcNow;
} 