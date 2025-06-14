import { CharactersProvider } from "@/contexts/characters";

export default function CharactersLayout({ children }: { children: React.ReactNode }) {
  return <CharactersProvider>{children}</CharactersProvider>;
}