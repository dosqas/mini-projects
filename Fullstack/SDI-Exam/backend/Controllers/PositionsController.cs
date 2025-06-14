using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/[controller]")]
public class PositionsController : ControllerBase
{
    private readonly AppDbContext _context;

    public PositionsController(AppDbContext context)
    {
        _context = context;
    }

    // GET: api/positions
    [HttpGet]
public async Task<IActionResult> GetAll()
{
    var positions = await _context.CharacterPositions
        .Include(p => p.Character)
        .ToListAsync();

    var result = positions.Select(p => new {
    id = p.Id, // <-- add this line!
    x = p.X,
    y = p.Y,
    characterId = p.CharacterId,
    character = p.Character == null ? null : new {
        nume = p.Character.Nume,
        poza = p.Character.Poza
    }
});

    return Ok(result);
}

    // GET: api/positions/{id}
    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(int id)
    {
        var position = await _context.CharacterPositions.FindAsync(id);
        if (position == null)
            return NotFound();
        return Ok(position);
    }

[HttpPost]
public async Task<IActionResult> Create([FromBody] PositionRequest req)
{
    // Check if the character exists
    var character = await _context.Characters.FindAsync(req.id);
    if (character == null)
    {
        return BadRequest("Character does not exist.");
    }

    // (Optional) Remove existing position for this character
    var existing = await _context.CharacterPositions
        .FirstOrDefaultAsync(p => p.CharacterId == req.id);
    if (existing != null)
    {
        _context.CharacterPositions.Remove(existing);
        await _context.SaveChangesAsync();
    }

    // Generate random coordinates
    var rand = new Random();
    var x = rand.Next(0, 100);
    var y = rand.Next(0, 100);

    // Create and save new position
    var position = new CharacterPosition
    {
        CharacterId = req.id,
        X = x,
        Y = y
    };
    _context.CharacterPositions.Add(position);
    await _context.SaveChangesAsync();

    return Ok(new { x, y });
}

    public class PositionRequest
    {
        public int id { get; set; }
    }

[HttpPut("{id}")]
public async Task<IActionResult> Update(int id, [FromBody] CharacterPosition updated)
{
    if (!ModelState.IsValid) return BadRequest(ModelState);
  
    var position = await _context.CharacterPositions.FindAsync(id);
    if (position == null) return NotFound();

    position.CharacterId = updated.CharacterId;
    position.X = updated.X;
    position.Y = updated.Y;

    await _context.SaveChangesAsync();

    return NoContent();
}


    // DELETE: api/positions/{id}
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var position = await _context.CharacterPositions.FindAsync(id);
        if (position == null)
            return NotFound();

        _context.CharacterPositions.Remove(position);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}