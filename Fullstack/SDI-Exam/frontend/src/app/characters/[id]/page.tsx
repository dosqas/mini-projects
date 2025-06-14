"use client";
import { use } from "react";
import { useRouter } from "next/navigation";
import { useState, useEffect } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_URL as string;
const API_URL = `${API_BASE}/characters`;

export default function CharacterDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const router = useRouter();
  const { id } = use(params); // Unwrap params with React.use()
  const [character, setCharacter] = useState<any>(null);
  const [edit, setEdit] = useState(false);
  const [form, setForm] = useState({ nume: "", poza: "", health: "", armor: "", mana: "" });

    const handlePlace = async () => {
    // POST to your backend to create a random position for this character
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/positions`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id }) // or whatever your API expects
      }
    );
    const data = await res.json();

    localStorage.setItem("myCharacterId", id);

    // Redirect to grid page with x and y from the response
    router.push(`/map`);
  };

  useEffect(() => {
    fetch(`${API_URL}/${id}`)
      .then(res => res.json())
      .then(data => {
        setCharacter(data);
        setForm({
          nume: data.nume,
          poza: data.poza,
          health: String(data.health),
          armor: String(data.armor),
          mana: String(data.mana),
        });
      });
  }, [id]);

  if (!character) return <div>Character not found.</div>;

  const handleDelete = async () => {
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    router.push("/characters");
  };

  const handleUpdate = async () => {
    await fetch(`${API_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        ...character,
        nume: form.nume,
        poza: form.poza,
        abilitati: {
          health: parseFloat(form.health),
          armor: parseFloat(form.armor),
          mana: parseFloat(form.mana),
        },
      }),
    });
    setEdit(false);
    // Refetch updated character
    const res = await fetch(`${API_URL}/${id}`);
    setCharacter(await res.json());
  };

  return (
    <div>
      <button onClick={() => router.push("/characters")}>Back</button>
      {edit ? (
        <div>
          <input
            placeholder="Nume"
            value={form.nume}
            onChange={e => setForm({ ...form, nume: e.target.value })}
          />
          <input
            placeholder="Poza (URL)"
            value={form.poza}
            onChange={e => setForm({ ...form, poza: e.target.value })}
          />
          <input
            placeholder="Health"
            value={form.health}
            onChange={e => setForm({ ...form, health: e.target.value })}
          />
          <input
            placeholder="Armor"
            value={form.armor}
            onChange={e => setForm({ ...form, armor: e.target.value })}
          />
          <input
            placeholder="Mana"
            value={form.mana}
            onChange={e => setForm({ ...form, mana: e.target.value })}
          />
          <button onClick={handleUpdate}>Save</button>
        </div>
      ) : (
        <>
          <h1>{character.Nume}</h1>
          <img src={character.Poza} alt={character.nume} width={100} height={100} />
          <p>ID: {character.Id}</p>
          <h3>Abilități</h3>
          <ul>
            <li>Health: {character.Health}</li>
            <li>Armor: {character.Armor}</li>
            <li>Mana: {character.Mana}</li>
          </ul>
          <button onClick={() => setEdit(true)}>Edit</button>
          <button onClick={handleDelete} style={{ color: "red" }}>Delete</button>
          <button onClick={handlePlace}>
            Place Character Randomly
          </button>
        </>
      )}
    </div>
  );
}