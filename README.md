# 📝 Notepad with Advanced Undo/Redo System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)

A feature-rich text editor implementing custom undo/redo functionality using doubly-linked lists with character and word-level granularity.

---

## 🌟 Features

### Core Functionality
- ✅ **Dual Undo/Redo Modes**
  - Character Mode: Granular character-by-character undo
  - Word Mode: Efficient word-level undo with smart batching
- ✅ **File Operations**: New, Open, Save, Save As with unsaved changes detection
- ✅ **Text Editing**: Cut, Copy, Paste, Select All
- ✅ **Search**: Find with highlighting and Find & Replace
- ✅ **Analysis**: Word frequency counter with unique word detection

### Technical Highlights
- 🔗 Custom **Doubly-Linked List** implementation for timeline management
- 🧠 Intelligent memory management with 1000-state limit
- ⚡ O(1) time complexity for undo/redo operations
- 🎯 Smart redo history pruning when timeline branches
- ⏱️ 500ms typing debounce for word mode optimization

---

## 📸 Screenshots

### Main Interface
![Main Window](screenshots/main-window.png)

### Word Frequency Analysis
![Word Frequency](screenshots/word-frequency.png)

### About Dialog
![About Dialog](screenshots/about-dialog.png)

---

## 🏗️ Architecture

### Data Structure Design
```
Timeline Representation (Doubly-Linked List):

head                                    current
 ↓                                         ↓
[""] ↔ ["H"] ↔ ["He"] ↔ ["Hel"] ↔ ["Hello"]
  ↑                                       ↑
oldest                                 newest
state                                   state

← prev links (for UNDO)
→ next links (for REDO)
```

### Class Structure

- **Node.java** - Doubly-linked list node
- **Stack.java** - Custom stack with bidirectional navigation
- **Notepad.java** - Main application + UI
- **About.java** - About dialog

### Key Algorithms

**Push Operation (Saving State)**
- Time Complexity: O(1)
- Checks for duplicates
- Clears redo history if timeline branches
- Links new node to timeline
- Enforces memory limit by removing oldest states

**Undo Operation**
- Time Complexity: O(1)
- Moves current pointer backward (prev)
- Returns previous state data

**Redo Operation**
- Time Complexity: O(1)
- Moves current pointer forward (next)
- Returns next state data

---

## 🚀 Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Steps

1. **Clone the repository**
```bash
   git clone https://github.com/YOUR_USERNAME/notepad-undo-redo-system.git
   cd notepad-undo-redo-system
```

2. **Compile the project**
```bash
   cd src
   javac Notepad.java
```

3. **Run the application**
```bash
   java Notepad
```

---

## 💻 Usage

### Basic Operations
| Action | Shortcut | Description |
|--------|----------|-------------|
| New File | `Ctrl + N` | Create new document |
| Open File | `Ctrl + O` | Open existing file |
| Save | `Ctrl + S` | Save current file |
| Undo | `Ctrl + Z` | Undo last change |
| Redo | `Ctrl + Y` | Redo last undo |
| Find | `Ctrl + F` | Search text |
| Replace | `Ctrl + H` | Find and replace |

### Switching Undo Modes
1. Go to **Edit Menu** → **Undo Mode**
2. Select **Character** or **Word**
3. Mode indicator appears in title bar

---

## 🧪 Technical Details

### Memory Management
- Maximum States: 1000
- Average State Size: ~1KB (for 1000 characters)
- Total Memory Usage: ~1MB (worst case)

### Word Mode Optimization
- **Trigger**: Space, punctuation, or 500ms idle time
- **Benefit**: Reduces state count by 70-90%
- **Use Case**: Long-form writing, documentation

### Character Mode
- **Trigger**: Every character insertion/deletion
- **Benefit**: Maximum precision for editing
- **Use Case**: Code editing, precise text manipulation

---

## 🎓 Educational Value

### Data Structures Demonstrated
1. **Doubly-Linked List**: Core timeline structure
2. **HashSet**: Word frequency analysis
3. **Stack Concept**: LIFO with bidirectional extension

### Concepts Covered
- Dynamic memory management
- Timeline/history tracking
- Event-driven programming
- GUI development with Swing
- File I/O operations
- String manipulation and parsing

---

## 🤝 Developer

**Developed by:** Ali Ahmed Memon

**Academic Supervisor**: Mam Irum Sindhu  
**Institution**: Sukkur IBA University,(IET)
**Year**: 2025  
**Course**: Data Structures and Algorithms

---

## 📄 License

This project is licensed under the MIT License.
```
MIT License

Copyright (c) 2025 Ali Ahmed Memon

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgments

- Inspired by classic Windows Notepad
- Built with Java Swing GUI framework
- Special thanks to Mam Irum Sindhu for guidance
- Sukkur IBA University for academic support

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

Made with ❤️ by Ali Ahmed Memon

</div>