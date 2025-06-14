using Microsoft.EntityFrameworkCore;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public DbSet<CharacterPosition> CharacterPositions { get; set; }
    public DbSet<Character> Characters { get; set; }
    public DbSet<Abilitati> Abilities { get; set; }

}