using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;

[ApiController]
[Route("api/[controller]")]
public class CharactersController : ControllerBase
{
    // GET: api/characters
    [HttpGet]
    public ActionResult<IEnumerable<Character>> GetAll()
    {
        Console.WriteLine("GET: api/characters called");
        return Ok(CharactersData.Characters);
    }

    // GET: api/characters/{id}
    [HttpGet("{id}")]
    public ActionResult<Character> GetById(int id)
    {
        Console.WriteLine($"GET: api/characters/{id} called");
        var character = CharactersData.Characters.FirstOrDefault(c => c.Id == id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found.");
            return NotFound();
        }
        return Ok(character);
    }

    // POST: api/characters
    [HttpPost]
    public ActionResult<Character> Create([FromBody] Character character)
    {
        Console.WriteLine("POST: api/characters called");
        character.Id = CharactersData.Characters.Any() ? CharactersData.Characters.Max(c => c.Id) + 1 : 1;
        CharactersData.Characters.Add(character);
        Console.WriteLine($"Character created: {character.Nume} (id: {character.Id})");
        return CreatedAtAction(nameof(GetById), new { id = character.Id }, character);
    }

    // PUT: api/characters/{id}
    [HttpPut("{id}")]
    public IActionResult Update(int id, [FromBody] Character updated)
    {
        Console.WriteLine($"PUT: api/characters/{id} called");
        var character = CharactersData.Characters.FirstOrDefault(c => c.Id == id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found for update.");
            return NotFound();
        }

        character.Nume = updated.Nume;
        character.Poza = updated.Poza;
        character.Abilitati = updated.Abilitati;
        Console.WriteLine($"Character updated: {character.Nume} (id: {character.Id})");
        return NoContent();
    }

    // DELETE: api/characters/{id}
    [HttpDelete("{id}")]
    public IActionResult Delete(int id)
    {
        Console.WriteLine($"DELETE: api/characters/{id} called");
        var character = CharactersData.Characters.FirstOrDefault(c => c.Id == id);
        if (character == null)
        {
            Console.WriteLine($"Character with id {id} not found for deletion.");
            return NotFound();
        }

        CharactersData.Characters.Remove(character);
        Console.WriteLine($"Character deleted: {character.Nume} (id: {character.Id})");
        return NoContent();
    }

    // POST: api/characters/generate
    [HttpPost("generate")]
    public ActionResult<Character> GenerateRandom()
    {
        Console.WriteLine("POST: api/characters/generate called");
        var names = new[] { "Mage", "Archer", "Rogue", "Cleric", "Paladin", "Druid", "Bard" };
        var rand = new System.Random();
        var name = names[rand.Next(names.Length)];
        var character = new Character
        {
            Id = CharactersData.Characters.Any() ? CharactersData.Characters.Max(c => c.Id) + 1 : 1,
            Nume = name,
            Poza = $"/images/{name.ToLower()}.png",
            Abilitati = new Abilitati
            {
                Health = rand.Next(50, 121),
                Armor = rand.Next(20, 91),
                Mana = rand.Next(30, 121)
            }
        };
        CharactersData.Characters.Add(character);
        Console.WriteLine($"Random character generated: {character.Nume} (id: {character.Id})");
        return Ok(character);
    }
}