"use client";
import React, { createContext, useContext, useState, ReactNode } from "react";
import { characters as initialCharacters } from "@/data/characters";
import type { Character } from "@/types/Character";

interface CharactersContextType {
  characters: Character[];
  setCharacters: React.Dispatch<React.SetStateAction<Character[]>>;
}

const CharactersContext = createContext<CharactersContextType | undefined>(undefined);

export function CharactersProvider({ children }: { children: ReactNode }) {
  const [characters, setCharacters] = useState<Character[]>(initialCharacters);

  return (
    <CharactersContext.Provider value={{ characters, setCharacters }}>
      {children}
    </CharactersContext.Provider>
  );
}

export function useCharacters() {
  const context = useContext(CharactersContext);
  if (!context) throw new Error("useCharacters must be used within a CharactersProvider");
  return context;
}