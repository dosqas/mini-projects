"use client";
import Link from "next/link";
import { useState, useEffect, useRef } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from "recharts";

const API_BASE = process.env.NEXT_PUBLIC_API_URL as string;
const API_URL = `${API_BASE}/characters`;

function getRandomInt(min: number, max: number) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function CharactersPage() {
  const [characters, setCharacters] = useState<any[]>([]);
  const [showMenu, setShowMenu] = useState(false);
  const [form, setForm] = useState({ nume: "", poza: "", health: "", armor: "", mana: "" });
  const [generating, setGenerating] = useState(false);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  // Fetch characters from backend
  const reload = () => {
    fetch(API_URL)
      .then(res => res.json())
      .then(setCharacters);
  };

  useEffect(() => {
    reload();
  }, []);

  // Statistics
  const total = characters.length;
  const avgHealth = total ? (characters.reduce((sum, c) => sum + c.abilitati.health, 0) / total) : 0;
  const avgArmor = total ? (characters.reduce((sum, c) => sum + c.abilitati.armor, 0) / total) : 0;
  const avgMana = total ? (characters.reduce((sum, c) => sum + c.abilitati.mana, 0) / total) : 0;

  const handleAdd = async () => {
    await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nume: form.nume,
        poza: form.poza,
        abilitati: {
          health: parseFloat(form.health),
          armor: parseFloat(form.armor),
          mana: parseFloat(form.mana),
        },
      }),
    });
    reload();
    setForm({ nume: "", poza: "", health: "", armor: "", mana: "" });
    setShowMenu(false);
  };

  const handleGenerateToggle = () => {
    if (!generating) {
      intervalRef.current = setInterval(async () => {
        await fetch(`${API_URL}/generate`, { method: "POST" });
        reload();
      }, 1000);
      setGenerating(true);
    } else {
      if (intervalRef.current) clearInterval(intervalRef.current);
      setGenerating(false);
    }
  };

  // Chart data
  const chartData = [
    { name: "Health", value: avgHealth },
    { name: "Armor", value: avgArmor },
    { name: "Mana", value: avgMana },
  ];

  return (
    <div style={{ background: "#39FF14", minHeight: "100vh", padding: 24 }}>
      <h1>MMORPG Characters</h1>
      <button onClick={() => setShowMenu((v) => !v)}>Add Character</button>
      <button onClick={handleGenerateToggle} style={{ marginLeft: 8 }}>
        {generating ? "Stop Generating" : "Start Generating"}
      </button>
      {showMenu && (
        <div style={{ border: "1px solid #ccc", padding: 10, margin: 10 }}>
          <input placeholder="Nume" value={form.nume} onChange={e => setForm({ ...form, nume: e.target.value })} />
          <input placeholder="Poza (URL)" value={form.poza} onChange={e => setForm({ ...form, poza: e.target.value })} />
          <input placeholder="Health" value={form.health} onChange={e => setForm({ ...form, health: e.target.value })} />
          <input placeholder="Armor" value={form.armor} onChange={e => setForm({ ...form, armor: e.target.value })} />
          <input placeholder="Mana" value={form.mana} onChange={e => setForm({ ...form, mana: e.target.value })} />
          <button onClick={handleAdd}>Save</button>
        </div>
      )}

      <div style={{ margin: "16px 0", padding: "8px", border: "1px solid #eee", background: "#fafafa" }}>
        <strong>Statistics:</strong>
        <div>Total Characters: {total}</div>
        <div>Average Health: {avgHealth.toFixed(1)}</div>
        <div>Average Armor: {avgArmor.toFixed(1)}</div>
        <div>Average Mana: {avgMana.toFixed(1)}</div>
      </div>

      <div style={{ width: "100%", height: 250 }}>
        <ResponsiveContainer>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Bar dataKey="value" fill="#8884d8" />
          </BarChart>
        </ResponsiveContainer>
      </div>

      <ul>
        {characters.map((char) => (
          <li key={char.id}>
            <Link href={`/characters/${char.id}`}>
              <div style={{ display: "flex", alignItems: "center", cursor: "pointer" }}>
                <img src={char.poza} alt={char.nume} width={50} height={50} style={{ marginRight: 10 }} />
                <span>{char.nume}</span>
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}