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
        var positions = await _context.CharacterPositions.ToListAsync();
        return Ok(positions);
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

    // POST: api/positions
    [HttpPost]
    public async Task<IActionResult> Create([FromBody] CharacterPosition position)
    {
        _context.CharacterPositions.Add(position);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(GetById), new { id = position.Id }, position);
    }

    // PUT: api/positions/{id}
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, [FromBody] CharacterPosition updated)
    {
        var position = await _context.CharacterPositions.FindAsync(id);
        if (position == null)
            return NotFound();

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