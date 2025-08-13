# 🌟 Mini-Projects

A collection of small but impactful projects developed for internship applications, coursework, and coding challenges. Each project demonstrates my ability to quickly build functional solutions while showcasing proficiency in diverse languages, frameworks, and problem-solving techniques.

### Key Highlights

✔ **Rapid Development** – Solutions built in 1-2 days  
✔ **Diverse Tech Stack** – C#, Java, JavaScript, REST APIs, SQL, and frameworks  
✔ **Real-World Applicability** – From data processing to interactive web apps  

---

## Projects

### 🖥️ C# Canvas & Rectangle Overlap Analyzer

A console application that:

* Takes user input for canvas dimensions (origin at bottom-left).
* Reads rectangle coordinates from `input.txt`.
* Performs four key analyses:

  1. **Filtering** – Lists rectangles fully within the canvas.
  2. **Overlap Detection** – Identifies non-overlapping rectangles.
  3. **Nesting Check** – Finds rectangles entirely enclosed by others.
  4. **Uncovered Area Calculation** – Calculates the area of the canvas not covered by rectangles.
* Includes discussions on **optimizations** for calculating uncovered areas.

---

### 📊 Java Internship Applicant Processor

A data processing system that:

* **Validates & cleans** CSV data (names, emails, scores).
* **Adjusts scores** dynamically (bonuses/penalties for submission timing).
* **Generates analytics**:

  * Unique applicant count
  * Top 3 applicants by adjusted score (last names only)
  * Average score of the top 50% pre-adjustment
* Uses **Java Streams** for memory efficiency and outputs results in **JSON**.

---

### 🎮 Full-Stack MMORPG Character & Battle Simulator

A multiplayer-ready web app built for the *Systems for Design and Implementation* course's exam:

* **Character Management** 🧙 – Hardcoded starter list with ID, image, name, and float-based stats (health, armor, mana) in a master/detail view.
* **CRUD Operations** 🛠️ – Add, edit, delete, and update characters.
* **Statistics & Random Generation** 🎲 – Generate random characters and display stat analytics.
* **Backend Integration** 🔄 – Moved logic to a backend service and deployed on a VM for LAN/WAN access.
* **Interactive Map** 🗺️ – Grid-based map where characters spawn randomly, persisted in a database.
* **Movement System** ⬆⬇⬅➡ – Player character moves around the grid (tracked via browser local storage).
* **Collision Detection** 🚨 – Grid tiles highlight red when enemies are nearby.
* **Enemy AI** 🤖 – Enemies move randomly.
* **Combat System** ⚔️ – Attack and remove enemies from the map; damage formula: `damage = mana * 0.35`.
* **Scoreboard** 🏆 – Tracks which character has the most kills.

---

## 📜 License

This repository is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---

## 💡 Contact

Questions, feedback, or ideas? Reach out anytime at [sebastian.soptelea@proton.me](mailto:sebastian.soptelea@proton.me).
