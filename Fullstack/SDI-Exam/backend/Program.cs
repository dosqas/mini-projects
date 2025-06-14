using Microsoft.EntityFrameworkCore;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add CORS policy
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

string? rawUrl = Environment.GetEnvironmentVariable("DATABASE_URL");
string connectionString;

if (!string.IsNullOrWhiteSpace(rawUrl))
{
    var databaseUri = new Uri(rawUrl);
    var userInfo = databaseUri.UserInfo.Split(':');

    var npgsqlBuilder = new NpgsqlConnectionStringBuilder
    {
        Host = databaseUri.Host,
        Port = databaseUri.Port,
        Username = userInfo[0],
        Password = userInfo[1],
        Database = databaseUri.AbsolutePath.Trim('/'),
        SslMode = SslMode.Require,
        TrustServerCertificate = true // Add this if you get SSL errors
    };

    connectionString = npgsqlBuilder.ConnectionString;
}
else
{
    connectionString = builder.Configuration.GetConnectionString("DefaultConnection")!;
}

// Register the DbContext
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();


var app = builder.Build();

bool add_characters = true; // Set this as needed, or read from config/env

if (add_characters)
{
    using (var scope = app.Services.CreateScope())
    {
        var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
        if (!db.Characters.Any())
        {
            db.Characters.AddRange(
                new Character { Nume = "Mage", Poza = "/images/mage.jpg", Abilitati = new Abilitati { Health = 70, Armor = 30, Mana = 100 } },
                new Character { Nume = "Archer", Poza = "/images/archer.png", Abilitati = new Abilitati { Health = 80, Armor = 40, Mana = 60 } },
                new Character { Nume = "Rogue", Poza = "/images/rogue.png", Abilitati = new Abilitati { Health = 75, Armor = 35, Mana = 50 } },
                new Character { Nume = "Cleric", Poza = "/images/cleric.png", Abilitati = new Abilitati { Health = 85, Armor = 50, Mana = 90 } },
                new Character { Nume = "Paladin", Poza = "/images/paladin.jpg", Abilitati = new Abilitati { Health = 95, Armor = 80, Mana = 70 } }
            );
            db.SaveChanges();
        }
    }
}

app.UseCors("AllowAll"); 

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();