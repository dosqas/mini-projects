using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/[controller]")]
public class ScoreboardController : ControllerBase
{
    private readonly AppDbContext _context;

    public ScoreboardController(AppDbContext context)
    {
        _context = context;
    }

    // GET: api/scoreboard
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var scoreboards = await _context.Scoreboards
            .Include(s => s.Character)
            .OrderByDescending(s => s.KillCount)
            .ThenBy(s => s.LastUpdated)
            .ToListAsync();

        var result = scoreboards.Select(s => new {
            id = s.Id,
            characterId = s.CharacterId,
            characterName = s.Character?.Nume ?? "Unknown",
            killCount = s.KillCount,
            lastUpdated = s.LastUpdated
        });

        return Ok(result);
    }

    // GET: api/scoreboard/{characterId}
    [HttpGet("{characterId}")]
    public async Task<IActionResult> GetByCharacterId(int characterId)
    {
        var scoreboard = await _context.Scoreboards
            .Include(s => s.Character)
            .FirstOrDefaultAsync(s => s.CharacterId == characterId);

        if (scoreboard == null)
        {
            // Create new scoreboard entry if it doesn't exist
            scoreboard = new Scoreboard
            {
                CharacterId = characterId,
                KillCount = 0,
                LastUpdated = DateTime.UtcNow
            };
            _context.Scoreboards.Add(scoreboard);
            await _context.SaveChangesAsync();
        }

        return Ok(new {
            id = scoreboard.Id,
            characterId = scoreboard.CharacterId,
            characterName = scoreboard.Character?.Nume ?? "Unknown",
            killCount = scoreboard.KillCount,
            lastUpdated = scoreboard.LastUpdated
        });
    }

    // POST: api/scoreboard/increment-kill/{characterId}
    [HttpPost("increment-kill/{characterId}")]
    public async Task<IActionResult> IncrementKill(int characterId)
    {
        var scoreboard = await _context.Scoreboards
            .FirstOrDefaultAsync(s => s.CharacterId == characterId);

        if (scoreboard == null)
        {
            // Create new scoreboard entry if it doesn't exist
            scoreboard = new Scoreboard
            {
                CharacterId = characterId,
                KillCount = 1,
                LastUpdated = DateTime.UtcNow
            };
            _context.Scoreboards.Add(scoreboard);
        }
        else
        {
            scoreboard.KillCount++;
            scoreboard.LastUpdated = DateTime.UtcNow;
        }

        await _context.SaveChangesAsync();

        return Ok(new {
            characterId = scoreboard.CharacterId,
            killCount = scoreboard.KillCount,
            lastUpdated = scoreboard.LastUpdated
        });
    }
} 