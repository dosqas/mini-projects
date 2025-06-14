using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

[ApiController]
[Route("api/[controller]")]
public class CharactersController : ControllerBase
{
    private readonly AppDbContext _context;

    public CharactersController(AppDbContext context)
    {
        _context = context;
    }

    // GET: api/characters
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Character>>> GetAll()
    {
        Console.WriteLine("GET: api/characters called");
        var characters = await _context.Characters.ToListAsync();
        return Ok(characters);
    }

    // GET: api/characters/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<Character>> GetById(int id)
    {
        Console.WriteLine($"GET: api/characters/{id} called");
        var character = await _context.Characters.FindAsync(id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found.");
            return NotFound();
        }
        return Ok(character);
    }

    // POST: api/characters
    [HttpPost]
    public async Task<ActionResult<Character>> Create([FromBody] Character character)
    {
        Console.WriteLine("POST: api/characters called");
        _context.Characters.Add(character);
        await _context.SaveChangesAsync();
        Console.WriteLine($"Character created: {character.Nume} (id: {character.Id})");
        return CreatedAtAction(nameof(GetById), new { id = character.Id }, character);
    }

    // PUT: api/characters/{id}
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(int id, [FromBody] Character updated)
    {
        Console.WriteLine($"PUT: api/characters/{id} called");
        var character = await _context.Characters.FindAsync(id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found for update.");
            return NotFound();
        }

        character.Nume = updated.Nume;
        character.Poza = updated.Poza;
        character.Abilitati = updated.Abilitati;
        await _context.SaveChangesAsync();
        Console.WriteLine($"Character updated: {character.Nume} (id: {character.Id})");
        return NoContent();
    }

    // DELETE: api/characters/{id}
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        Console.WriteLine($"DELETE: api/characters/{id} called");
        var character = await _context.Characters.FindAsync(id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found for deletion.");
            return NotFound();
        }

        _context.Characters.Remove(character);
        await _context.SaveChangesAsync();
        Console.WriteLine($"Character deleted: {character.Nume} (id: {character.Id})");
        return NoContent();
    }

    // POST: api/characters/generate
    [HttpPost("generate")]
    public async Task<ActionResult<Character>> GenerateRandom()
    {
        Console.WriteLine("POST: api/characters/generate called");
        var names = new[] { "Mage", "Archer", "Rogue", "Cleric", "Paladin", "Druid", "Bard" };
        var rand = new System.Random();
        var name = names[rand.Next(names.Length)];
        var character = new Character
        {
            Nume = name,
            Poza = $"/images/{name.ToLower()}.png",
            Abilitati = new Abilitati
            {
                Health = rand.Next(50, 121),
                Armor = rand.Next(20, 91),
                Mana = rand.Next(30, 121)
            }
        };
        _context.Characters.Add(character);
        await _context.SaveChangesAsync();
        Console.WriteLine($"Random character generated: {character.Nume} (id: {character.Id})");
        return Ok(character);
    }
}