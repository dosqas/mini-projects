using Microsoft.EntityFrameworkCore;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public DbSet<CharacterPosition> CharacterPositions { get; set; }
    public DbSet<Character> Characters { get; set; }
    public DbSet<Scoreboard> Scoreboards { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Character>()
            .HasOne(c => c.CharacterPosition)
            .WithOne(p => p.Character)
            .HasForeignKey<CharacterPosition>(p => p.CharacterId);
            
        modelBuilder.Entity<Scoreboard>()
            .HasOne(s => s.Character)
            .WithMany()
            .HasForeignKey(s => s.CharacterId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}