"use client";
import { useEffect, useState } from "react";

const GRID_SIZE = 100;
const CELL_SIZE = 24; // px

type Position = {
  id: number | string;
  x: number;
  y: number;
  characterId: number | string;
  character?: {
    nume?: string;
    poza?: string;
    mana?: number;
    health?: number;
    armor?: number;
  };
};

function shouldColorCell(
  positions: Position[],
  myX: number,
  myY: number,
  myCharacterId: string | number | null,
  cellX: number,
  cellY: number,
  radius: number
): boolean {
  // Skip if no character ID or positions
  if (!myCharacterId || !positions) return false;
  
  // Exclude your own cell
  if (myX === cellX && myY === cellY) return false;
  
  // Calculate distance
  const dx = myX - cellX;
  const dy = myY - cellY;
  const dist = Math.sqrt(dx * dx + dy * dy);
  
  // Check if within radius AND has a player (that's not you)
  return dist <= radius && 
         positions.some(p => 
           p.x === cellX && 
           p.y === cellY && 
           p.characterId !== myCharacterId
         );
}

function getCellBackgroundColor(
  positions: Position[],
  myPosition: Position | undefined,
  myCharacterId: string | number | null,
  cellX: number,
  cellY: number,
  playerRadius: number,
  visualRadius: number
) {
  const pos = positions.find(p => p.x === cellX && p.y === cellY);
  
  // Don't render players with health 0 or below
  if (pos && pos.character && pos.character.health !== undefined && pos.character.health <= 0) {
    return "#f9f9f9"; // Return empty cell color
  }
  
  const isMine = pos && pos.characterId === myCharacterId;

  if (isMine) return "#FFD700";

  if (myPosition && myCharacterId != null) {
    // Check if any OTHER player is within playerRadius of MY position
    const redNearby = positions.some(p => {
      if (p.characterId === myCharacterId) return false;
      // Skip players with health 0 or below
      if (p.character && p.character.health !== undefined && p.character.health <= 0) return false;
      
      const dx = p.x - myPosition.x;
      const dy = p.y - myPosition.y;
      const dist = Math.sqrt(dx * dx + dy * dy);
      return dist <= playerRadius;
    });
    
    // If there's a player nearby, color cells within visual radius red
    if (redNearby) {
      const dx = myPosition.x - cellX;
      const dy = myPosition.y - cellY;
      const dist = Math.sqrt(dx * dx + dy * dy);
      if (dist <= visualRadius && dist > 0) return "#FF4444";
    }

    // Visual radius (only if no red nearby)
    const dx = myPosition.x - cellX;
    const dy = myPosition.y - cellY;
    const dist = Math.sqrt(dx * dx + dy * dy);
    if (dist <= visualRadius && dist > 0) return "#FFFF99";
  }

  if (pos) return "#fff";
  return "#f9f9f9";
}


// Function to check if a move is valid (no collision)
function canMoveTo(
  positions: Position[],
  newX: number,
  newY: number,
  myCharacterId: string
): boolean {
  // Check bounds
  if (newX < 0 || newX >= GRID_SIZE || newY < 0 || newY >= GRID_SIZE) {
    return false;
  }
  
  // Check for collision with other players
  const collision = positions.some(
    (pos) => 
      pos.x === newX && 
      pos.y === newY && 
      String(pos.characterId) !== myCharacterId
  );
  
  return !collision;
}

// Function to move other players randomly
function moveOtherPlayersRandomly(positions: Position[], myCharacterId: string | null) {
  if (!myCharacterId) return;

  positions.forEach(async (pos) => {
    // Skip my player
    if (String(pos.characterId) === myCharacterId) return;

    // Skip dead players
    if (pos.character && pos.character.health !== undefined && pos.character.health <= 0) {
      console.log(`💀 Skipping dead player in movement: ${pos.character?.nume || 'Unknown'} (Health: ${pos.character.health})`);
      return;
    }

    // Generate random direction
    const directions = [
      { dx: 0, dy: -1 }, // up
      { dx: 0, dy: 1 },  // down
      { dx: -1, dy: 0 }, // left
      { dx: 1, dy: 0 }   // right
    ];
    
    const randomDir = directions[Math.floor(Math.random() * directions.length)];
    const newX = pos.x + randomDir.dx;
    const newY = pos.y + randomDir.dy;

    // Check if move is valid
    if (canMoveTo(positions, newX, newY, String(pos.characterId))) {
      try {
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${pos.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            X: newX,
            Y: newY,
            CharacterId: pos.characterId,
          }),
        });
      } catch (err) {
        console.error("Error moving other player:", err);
      }
    }
  });
}

// Function to get border color based on health percentage
function getBorderColor(character: any): string {
  if (!character || character.health === undefined || character.health <= 0) {
    return "transparent"; // Don't show if health <= 0
  }
  
  const healthPercent = character.health / 100; // Assuming max health is 100
  
  if (healthPercent > 0.66) return "#00FF00"; // Bright green
  if (healthPercent > 0.33) return "#FFA500"; // Orange
  return "#FF0000"; // Red
}

// Function to increment kill count for a character
const incrementKillCount = async (characterId: number) => {
  try {
    await fetch(`${process.env.NEXT_PUBLIC_API_URL}/scoreboard/increment-kill/${characterId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });
    console.log(`🏆 Incremented kill count for character ${characterId}`);
  } catch (err) {
    console.error("Error incrementing kill count:", err);
  }
};

// Function to attack players in radius
async function attackPlayersInRadius(
  positions: Position[], 
  myPosition: Position, 
  myCharacterId: string | number,
  playerRadius: number
) {
  const myCharacter = myPosition.character;
  console.log("🔍 My character data:", myCharacter);
  
  // Use default mana if not available
  const mana = myCharacter?.mana || 1000;
  console.log(`🔮 Using mana: ${mana} (${myCharacter?.mana ? 'from character' : 'default value'})`);
  
  const damage = Math.floor(mana * 0.35);
  console.log(`🎯 Preparing attack with ${damage} damage (${mana} mana × 0.35)`);
  
  const targets = positions.filter(pos => {
    // Skip my player - convert both to strings for comparison
    if (String(pos.characterId) === String(myCharacterId)) {
      console.log(`⏭️ Skipping my player: ${pos.character?.nume || 'Unknown'} (ID: ${pos.characterId})`);
      return false;
    }
    
    // Skip players with health 0 or below
    if (pos.character && pos.character.health !== undefined && pos.character.health <= 0) {
      console.log(`💀 Skipping dead player: ${pos.character?.nume || 'Unknown'} (Health: ${pos.character.health})`);
      return false;
    }
    
    const dx = pos.x - myPosition.x;
    const dy = pos.y - myPosition.y;
    const dist = Math.sqrt(dx * dx + dy * dy);
    return dist <= playerRadius;
  });

  if (targets.length === 0) {
    console.log("❌ No players in range to attack!");
    console.log(`📍 My position: (${myPosition.x}, ${myPosition.y})`);
    console.log(`🎯 Checking radius: ${playerRadius} cells`);
    console.log(`👥 All players:`, positions.map(p => `${p.character?.nume || 'Unknown'} at (${p.x}, ${p.y}) - Health: ${p.character?.health || 'N/A'} - ID: ${p.characterId}`));
    return;
  }

  console.log(`⚔️ Attacking ${targets.length} players within ${playerRadius} cell radius!`);
  console.log(`📍 My position: (${myPosition.x}, ${myPosition.y})`);
  console.log(`🎯 Targets found:`, targets.map(t => `${t.character?.nume || 'Unknown'} at (${t.x}, ${t.y})`));

  // Attack each target
  for (const target of targets) {
    try {
      const currentHealth = target.character?.health || 100;
      const currentArmor = target.character?.armor || 0;
      
      console.log(`\n🎯 Attacking ${target.character?.nume || 'Unknown'} at (${target.x}, ${target.y})`);
      console.log(`💔 Current stats - Health: ${currentHealth}, Armor: ${currentArmor}`);
      
      let remainingDamage = damage;
      let newArmor = currentArmor;
      let newHealth = currentHealth;

      // Damage goes to armor first
      if (newArmor > 0) {
        console.log(`🛡️ Armor absorbs damage first...`);
        if (remainingDamage >= newArmor) {
          const armorAbsorbed = newArmor;
          remainingDamage -= newArmor;
          newArmor = 0;
          console.log(`🛡️ Armor destroyed! Absorbed ${armorAbsorbed} damage`);
        } else {
          newArmor -= remainingDamage;
          console.log(`🛡️ Armor absorbs ${remainingDamage} damage, remaining armor: ${newArmor}`);
          remainingDamage = 0;
        }
      }

      // Remaining damage goes to health
      if (remainingDamage > 0) {
        console.log(`💔 Health takes ${remainingDamage} damage`);
        newHealth -= remainingDamage;
      }

      console.log(`📊 Final stats - Health: ${newHealth}, Armor: ${newArmor}`);

      // Update target's health and armor
      const updateData = {
        X: target.x,
        Y: target.y,
        CharacterId: target.characterId,
        Health: newHealth,
        Armor: newArmor,
      };
      console.log(`📤 Sending update to server for ${target.character?.nume || 'Unknown'}:`, updateData);
      
      if (newHealth <= 0) {
        // Delete the player from database if defeated
        console.log(`🗑️ Deleting defeated player ${target.character?.nume || 'Unknown'} from database...`);
        try {
          await fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${target.id}`, {
            method: "DELETE",
          });
          console.log(`✅ Successfully deleted ${target.character?.nume || 'Unknown'} from database!`);
        } catch (err) {
          console.error(`❌ Error deleting player ${target.character?.nume || 'Unknown'}:`, err);
          // Fallback to updating health if delete fails
          await fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${target.id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updateData),
          });
        }
        // Increment kill count for the attacker
        if (myCharacterId) {
          incrementKillCount(Number(myCharacterId));
        }
      } else {
        // Update health and armor if not defeated
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${target.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(updateData),
        });
      }

      // Log health status
      if (newHealth <= 0) {
        console.log(`💀 ${target.character?.nume || 'Unknown'} has been defeated and deleted from database!`);
      } else {
        console.log(`✅ Successfully hit ${target.character?.nume || 'Unknown'} for ${damage} total damage!`);
        if (newHealth < 33) {
          console.log(`🔴 ${target.character?.nume || 'Unknown'} is critically wounded!`);
        } else if (newHealth < 66) {
          console.log(`🟠 ${target.character?.nume || 'Unknown'} is moderately wounded!`);
        } else {
          console.log(`🟢 ${target.character?.nume || 'Unknown'} is still healthy!`);
        }
      }
      
    } catch (err) {
      console.error("❌ Error attacking player:", err);
    }
  }

  console.log(`\n🎉 Attack sequence completed! Mana used: ${mana}`);
}

export default function GridPage() {
  const [positions, setPositions] = useState<Position[]>([]);
  const [myCharacterId, setMyCharacterId] = useState<string | null>(null);
  const [scoreboard, setScoreboard] = useState<any[]>([]);

  const PLAYER_RADIUS = 5; // radius in cells for detecting other players
  const VISUAL_RADIUS = 5; // radius in cells for visual highlighting around my player

  // Fetch positions from API
  const fetchPositions = () => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions`)
      .then((res) => res.json())
      .then((data: Position[]) => {
        console.log("📡 Fetched positions from server:", data.map((p: Position) => `${p.character?.nume || 'Unknown'} at (${p.x}, ${p.y}) - Health: ${p.character?.health || 'N/A'}`));
        setPositions(data);
      })
      .catch((err) => console.error("Error fetching positions:", err));
  };

  // Fetch scoreboard from API
  const fetchScoreboard = () => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/scoreboard`)
      .then((res) => res.json())
      .then((data) => {
        console.log("🏆 Fetched scoreboard:", data);
        setScoreboard(data);
      })
      .catch((err) => console.error("Error fetching scoreboard:", err));
  };

  // Load myCharacterId from localStorage once on mount
  useEffect(() => {
    if (typeof window !== "undefined") {
      setMyCharacterId(localStorage.getItem("myCharacterId"));
    }
  }, []);

  // Set mana for my player
  const setPlayerMana = async () => {
    if (!myCharacterId || !myPosition) return;
    
    try {
      await fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${myPosition.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          X: myPosition.x,
          Y: myPosition.y,
          CharacterId: myPosition.characterId,
          Mana: 1000,
        }),
      });
      console.log("✨ Set 1000 mana for your player!");
      fetchPositions();
    } catch (err) {
      console.error("Error setting mana:", err);
    }
  };

  // Initial fetch + periodic refresh every 2 seconds
  useEffect(() => {
    fetchPositions();
    fetchScoreboard();
    const interval = setInterval(() => {
      fetchPositions();
      fetchScoreboard();
    }, 2000);
    return () => clearInterval(interval);
  }, []);

  // Move other players randomly every second
  useEffect(() => {
    if (!myCharacterId || positions.length === 0) return;

    const moveInterval = setInterval(() => {
      moveOtherPlayersRandomly(positions, myCharacterId);
    }, 1000);

    return () => clearInterval(moveInterval);
  }, [positions, myCharacterId]);

  const myCharacterIdNum = myCharacterId ? Number(myCharacterId) : null;
  const myPosition = positions.find(p => p.characterId === myCharacterIdNum);

  // Handle keyboard input for moving player
  useEffect(() => {
    if (!myCharacterId) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      let dx = 0, dy = 0;
      
      if (e.key === "ArrowUp") dy = -1;
      else if (e.key === "ArrowDown") dy = 1;
      else if (e.key === "ArrowLeft") dx = -1;
      else if (e.key === "ArrowRight") dx = 1;
      else if (e.key === "L") {
        // Attack with L key
        if (myPosition) {
          attackPlayersInRadius(positions, myPosition, myCharacterId, PLAYER_RADIUS);
        }
        return;
      }
      else if (e.key === "M") {
        // Set mana with M key
        console.log("🔮 M key pressed - setting mana to 1000!");
        setPlayerMana();
        return;
      }

      if (dx !== 0 || dy !== 0) {
        const myPos = positions.find(
          (p) => String(p.characterId) === myCharacterId
        );
        if (!myPos) return;

        const newX = myPos.x + dx;
        const newY = myPos.y + dy;

        // Use the canMoveTo function to check if move is valid
        if (!canMoveTo(positions, newX, newY, myCharacterId)) {
          console.log("Move blocked - collision or out of bounds!");
          return;
        }

        fetch(`${process.env.NEXT_PUBLIC_API_URL}/positions/${myPos.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            X: newX,
            Y: newY,
            CharacterId: myPos.characterId,
          }),
        })
          .then(() => {
            fetchPositions();
          })
          .catch((err) => console.error("Error updating position:", err));
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [positions, myCharacterId, myPosition]);

  // Count nearby players using shouldColorCell function
  const nearbyPlayersCount = myPosition && myCharacterIdNum !== null 
    ? positions.filter(pos => 
        shouldColorCell(positions, myPosition.x, myPosition.y, myCharacterIdNum, pos.x, pos.y, PLAYER_RADIUS)
      ).length 
    : 0;

  return (
    <div>
      <h2>Character Grid</h2>
      <div>
        <p>Active players: {positions.length}</p>
        <p>My Character ID: {myCharacterId || "Not set"}</p>
        {myPosition && (
          <>
            <p>My Position: ({myPosition.x}, {myPosition.y})</p>
            <p>Nearby players (within {PLAYER_RADIUS} cells): {nearbyPlayersCount}</p>
          </>
        )}
      </div>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: `repeat(${GRID_SIZE}, ${CELL_SIZE}px)`,
          gridTemplateRows: `repeat(${GRID_SIZE}, ${CELL_SIZE}px)`,
          border: "1px solid #000",
          width: GRID_SIZE * CELL_SIZE,
          height: GRID_SIZE * CELL_SIZE,
          fontSize: 10,
          overflow: "auto",
        }}
      >
        {[...Array(GRID_SIZE * GRID_SIZE)].map((_, idx) => {
          const x = idx % GRID_SIZE;
          const y = Math.floor(idx / GRID_SIZE);
          const pos = positions.find(p => p.x === x && p.y === y);
          
          // Don't render players with health 0 or below
          const shouldRenderPlayer = pos && pos.character && 
            (pos.character.health === undefined || pos.character.health > 0);
          
          // Use our centralized color function
          const bgColor = getCellBackgroundColor(
            positions, 
            myPosition, 
            myCharacterIdNum, 
            x, 
            y, 
            PLAYER_RADIUS, 
            VISUAL_RADIUS
          );

          return (
            <div
              key={idx}
              style={{
                width: CELL_SIZE,
                height: CELL_SIZE,
                border: shouldRenderPlayer && pos.character ? `3px solid ${getBorderColor(pos.character)}` : "1px solid #eee",
                background: bgColor,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                overflow: "hidden",
              }}
              title={shouldRenderPlayer && pos ? `(${x},${y}): ${pos.character?.nume}${pos.character?.health !== undefined ? ` - HP: ${pos.character.health}` : ''}${pos.character?.mana !== undefined ? ` - MP: ${pos.character.mana}` : ''}${pos.character?.armor !== undefined ? ` - Armor: ${pos.character.armor}` : ''}` : `(${x},${y})`}
            >
              {shouldRenderPlayer && pos && pos.character?.poza ? (
                <img
                  src={pos.character.poza}
                  alt={pos.character.nume}
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                  title={pos.character.nume}
                />
              ) : ""}
            </div>
          );
        })}
      </div>

      {/* Scoreboard */}
      <div style={{ marginTop: "20px", padding: "15px", backgroundColor: "#f5f5f5", borderRadius: "8px" }}>
        <h3 style={{ margin: "0 0 10px 0", color: "#333" }}>🏆 Scoreboard</h3>
        {scoreboard.length > 0 ? (
          <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
            {scoreboard.map((score, index) => (
              <div 
                key={score.id} 
                style={{ 
                  display: "flex", 
                  justifyContent: "space-between", 
                  padding: "8px 12px", 
                  backgroundColor: index === 0 ? "#FFD700" : index === 1 ? "#C0C0C0" : index === 2 ? "#CD7F32" : "#fff",
                  borderRadius: "4px",
                  border: index < 3 ? "2px solid #333" : "1px solid #ddd",
                  fontWeight: index < 3 ? "bold" : "normal"
                }}
              >
                <span>
                  {index + 1}. {score.characterName}
                </span>
                <span>
                  {score.killCount} kills
                </span>
              </div>
            ))}
          </div>
        ) : (
          <p style={{ margin: 0, color: "#666" }}>No scores yet. Start playing to see the leaderboard!</p>
        )}
      </div>

      <div style={{ marginTop: "10px", fontSize: "12px" }}>
        <p><strong>Legend:</strong></p>
        <p>🟨 Gold = Your character</p>
        <p>🟡 Light Yellow = Your {VISUAL_RADIUS}-cell visual radius</p>
        <p>🟥 Red = Players within {PLAYER_RADIUS}-cell detection radius</p>
        <p>⬜ White = Other players</p>
        <p>⬜ Light Gray = Empty cells</p>
        <p><strong>Border Colors:</strong></p>
        <p>🟢 Bright Green = Health &gt; 66%</p>
        <p>🟠 Orange = Health 33-66%</p>
        <p>🔴 Red = Health &lt; 33%</p>
        <p>⚪ Transparent = Health ≤ 0 (player hidden)</p>
        <p><strong>Controls:</strong></p>
        <p>Arrow keys = Move</p>
        <p>L key = Attack (deals mana × 0.35 damage)</p>
        <p>M key = Set mana to 1000</p>
      </div>
    </div>
  );
}