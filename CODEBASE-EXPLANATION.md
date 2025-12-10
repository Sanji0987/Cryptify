# CryptoDrop - Complete Codebase Explanation

**A comprehensive guide to understanding every file in the CryptoDrop encryption application**

---

# 📚 Table of Contents

1. [Main.java - Application Entry Point](#1-mainjava---application-entry-point)
2. [CipherSelector.java - Cipher Selection Dialog](#2-cipherselectorjava---cipher-selection-dialog)
3. [CryptoHelper.java - File Encryption Utility](#3-cryptohelperjava---file-encryption-utility)
4. [Cipher.java - Abstract Base Class](#4-cipherjava---abstract-base-class)
5. [CaesarCipher.java - Caesar Cipher Implementation](#5-caesarcipherjava---caesar-cipher-implementation)
6. [XORCipher.java - XOR Cipher Implementation](#6-xorcipherjava---xor-cipher-implementation)
7. [AESCipher.java - AES-GCM Implementation](#7-aescipherjava---aes-gcm-implementation)
8. [CipherFactory.java - Factory Pattern](#8-cipherfactoryjava---factory-pattern)
9. [FileHeaderUtil.java - File Header Management](#9-fileheaderutiljava---file-header-management)
10. [KeyManager.java - Key Management](#10-keymanagerjava---key-management)
11. [FileManager.java - File List Management](#11-filemanagerjava---file-list-management)
12. [DialogHelper.java - Custom Dialogs](#12-dialoghelperjava---custom-dialogs)
13. [Styles.java - UI Styling](#13-stylesjava---ui-styling)
14. [Interfaces](#14-interfaces)

---

# 1. Main.java - Application Entry Point

**Purpose:** The entry point of the CryptoDrop application. Sets up the entire JavaFX user interface and coordinates all interactions between managers.

## 🎯 Core Responsibilities

- Initialize the JavaFX application window
- Create the two-panel layout (file list + drag-and-drop zone)
- Set up event handlers for all user interactions
- Coordinate FileManager and KeyManager instances
- Handle encryption/decryption requests

---

## 📊 Class Structure

```java
public class Main extends Application {
    private ObservableList<String> uiFileList;
    private FileManager fileManager;
    private KeyManager keyManager;
}
```

### **Instance Variables:**

| Variable | Type | Purpose |
|----------|------|---------|
| `uiFileList` | `ObservableList<String>` | Observable list of file paths that automatically updates the ListView |
| `fileManager` | `FileManager` | Manages file operations (add, delete, clear) |
| `keyManager` | `KeyManager` | Manages encryption key (derive from password, store, retrieve) |

---

## 🏗️ Application Lifecycle

### **1. main() Method - Lines 225-227**

```java
public static void main(String[] args) {
    launch(args);
}
```

**Explanation:**
- Entry point when you run `java Main`
- `launch(args)` is a JavaFX method that:
  1. Initializes JavaFX framework
  2. Creates the primary Stage (window)
  3. Calls `start(Stage primaryStage)` method

---

### **2. start() Method - Lines 29-200**

This is where the **entire UI is built**.

#### **Phase 1: Manager Initialization (Lines 31-33)**

```java
fileManager = new FileManager(uiFileList);
keyManager = new KeyManager();
```

**Key Concept: Dependency Injection**
- `FileManager` receives `uiFileList` reference
- When FileManager modifies the list, UI updates automatically (Observer Pattern!)

---

#### **Phase 2: Layout Structure (Lines 36-42)**

```java
HBox mainContainer = new HBox(15);  // Horizontal box, 15px spacing
VBox leftPanel = new VBox(10);      // Vertical box, 10px spacing
VBox rightPanel = new VBox(12);     // Vertical box, 12px spacing
```

**Visual Structure:**
```
┌─────────────────────────────────────────┐
│         mainContainer (HBox)            │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │  leftPanel   │  │  rightPanel     │ │
│  │   (VBox)     │  │    (VBox)       │ │
│  └──────────────┘  └─────────────────┘ │
└─────────────────────────────────────────┘
```

---

#### **Phase 3: Left Panel - File List (Lines 39-69)**

**Components:**
1. **Label** - "FILE LIST" header
2. **ListView** - Shows file paths from `uiFileList`
3. **Clear All Button** - Clears all files
4. **Delete Selected Button** - Removes selected file

**Key Code - ListView Binding (Line 47):**
```java
ListView<String> fileListView = new ListView<>(uiFileList);
```

**🔍 Deep Dive: ObservableList Magic**

When you pass `uiFileList` to `ListView`:
- ListView **observes** the list for changes
- When you do `uiFileList.add("file.txt")`, ListView **automatically** updates
- No manual refresh needed!
- This is the **Observer Pattern** in action

**Example Flow:**
```
1. User drops file
2. Code: uiFileList.add("/path/to/file.txt")
3. ObservableList fires change event
4. ListView receives event
5. ListView redraws itself with new item
6. User sees file appear in list ✅
```

---

**Button Handler Example - Clear All (Lines 51-58):**

```java
clearListBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        fileManager.clearAll();
    }
});
```

**OOP Concepts:**
- **Anonymous Inner Class**: `new EventHandler<ActionEvent>() { ... }`
- **Delegation**: Main doesn't clear the list directly, asks FileManager to do it
- **Event-Driven Programming**: Code runs when user clicks

---

#### **Phase 4: Right Panel - Drag & Drop (Lines 71-126)**

**Drop Zone Setup:**
```java
VBox dropZone = new VBox();
dropZone.setPrefHeight(160);
dropZone.setAlignment(Pos.CENTER);
```

**Three Event Handlers for Drag-and-Drop:**

##### **1. onDragOver - Lines 89-98**

```java
dropZone.setOnDragOver(new EventHandler<DragEvent>() {
    @Override
    public void handle(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dropZone.setStyle(Styles.DROP_ZONE_ACTIVE);
        }
        event.consume();
    }
});
```

**What this does:**
- Fires **continuously** while user drags over the zone
- **Checks** if dragged items are files
- **Accepts** the transfer (tells OS we can handle this)
- **Changes style** for visual feedback (zone highlights)
- `event.consume()` - prevents event from bubbling to parent

**Visual Feedback:**
```
Normal: Gray border
Dragging over: Green glowing border (Styles.DROP_ZONE_ACTIVE)
```

---

##### **2. onDragExited - Lines 100-106**

```java
dropZone.setOnDragExited(new EventHandler<DragEvent>() {
    @Override
    public void handle(DragEvent event) {
        dropZone.setStyle(Styles.DROP_ZONE_NORMAL);
        event.consume();
    }
});
```

**What this does:**
- Fires when drag **leaves** the zone
- Resets style back to normal
- Provides clean UX

---

##### **3. onDragDropped - Lines 108-126** ⭐ **CRITICAL!**

```java
dropZone.setOnDragDropped(new EventHandler<DragEvent>() {
    @Override
    public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        
        if (db.hasFiles()) {
            success = true;
            for (File file : db.getFiles()) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".txt") || name.endsWith(".enc")) {
                    uiFileList.add(file.getAbsolutePath());
                    fileManager.addFile(file.getAbsolutePath());
                }
            }
        }
        
        event.setDropCompleted(success);
        event.consume();
    }
});
```

**Line-by-Line Breakdown:**

| Line | Code | Purpose |
|------|------|---------|
| 111 | `Dragboard db = event.getDragboard()` | Get the "clipboard" holding dragged data |
| 115 | `for (File file : db.getFiles())` | Loop through each dropped file |
| 116 | `String name = file.getName().toLowerCase()` | Get filename in lowercase |
| 117 | `if (name.endsWith(".txt") ...` | **File validation** - only .txt and .enc |
| 118 | `uiFileList.add(file.getAbsolutePath())` | Add to UI list (ListView updates!) |
| 119 | `fileManager.addFile(...)` | FileManager checks for duplicates |
| 123 | `event.setDropCompleted(success)` | Tell OS the drop succeeded |

**Why add to both uiFileList AND fileManager?**
- `uiFileList.add()` → **UI updates immediately**
- `fileManager.addFile()` → **Duplicate prevention** (FileManager checks first)

---

#### **Phase 5: Action Buttons (Lines 132-187)**

##### **Add File Button - Lines 132-139**

```java
fileAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        fileManager.openFileChooser(primaryStage);
    }
});
```

**What happens:**
1. User clicks "Add File..."
2. FileManager opens JavaFX FileChooser dialog
3. User selects file
4. FileManager adds to `uiFileList`
5. ListView automatically updates

---

##### **Set Key Button - Lines 141-148**

```java
keyAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        keyManager.showKeyWindow(primaryStage);
    }
});
```

**What happens:**
1. Opens password entry dialog
2. User enters password
3. KeyManager derives 256-bit key using SHA-256
4. Key stored in KeyManager

---

##### **Encrypt Button - Lines 150-167** ⭐ **IMPORTANT!**

```java
encryptBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        
        // Validation 1: File selected?
        if (selectedFile == null) {
            DialogHelper.showError("No File Selected", "Please select a file...");
            return;
        }
        
        // Validation 2: Key set?
        if (!keyManager.hasKey()) {
            DialogHelper.showError("No Key Set", "Please set an encryption key first!");
            return;
        }
        
        // Proceed with encryption
        CipherSelector.show(primaryStage, new File(selectedFile), 
                          keyManager.getKey(), uiFileList, true);
    }
});
```

**Guard Clauses Pattern:**
- Check file selection → return early if fail
- Check key existence → return early if fail
- Only proceed if both checks pass

**Final Step:**
- `CipherSelector.show(..., true)` - last parameter `true` = encryption mode
- Opens dialog for user to choose Caesar/XOR/AES

---

##### **Decrypt Button - Lines 170-190** ⭐ **USES CIPHER CLASSES!**

```java
decryptBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        
        if (selectedFile == null) {
            DialogHelper.showError("No File Selected", "...");
            return;
        }
        
        if (!keyManager.hasKey()) {
            DialogHelper.showError("No Key Set", "...");
            return;
        }
        
        try {
            CryptoHelper.decryptFile(new File(selectedFile), keyManager.getKey());
            DialogHelper.showSuccess("Decryption Complete", "File decrypted successfully!");
        } catch (Exception e) {
            DialogHelper.showError("Decryption Error", e.getMessage());
        }
    }
});
```

**Key Differences from Encrypt:**
- No cipher selection needed (auto-detected from file header!)
- Uses `CryptoHelper.decryptFile()` directly
- Proper exception handling with try-catch
- Shows success dialog on completion

**Flow:**
```
1. Validate file & key
2. Call CryptoHelper.decryptFile(file, key)
   ↓
3. CryptoHelper reads file header
4. Detects cipher type (1=Caesar, 2=XOR, 3=AES)
5. CipherFactory creates appropriate Cipher object
6. Cipher.decrypt() called (polymorphism!)
7. File overwritten with decrypted data
8. Success dialog shown
```

---

## 🎨 Helper Method: applyButtonStyle() - Lines 203-216

```java
private void applyButtonStyle(Button button) {
    button.setPrefWidth(350);
    button.setPrefHeight(32);
    button.setStyle(Styles.BUTTON_NORMAL);
    
    button.setOnMouseEntered(e -> {
        button.setStyle(Styles.BUTTON_HOVER);
    });
    
    button.setOnMouseExited(e -> {
        button.setStyle(Styles.BUTTON_NORMAL);
    });
}
```

**OOP Principle: DRY (Don't Repeat Yourself)**
- Instead of styling each button individually, one method styles all
- Called for all 6 buttons (Clear, Delete, Add File, Set Key, Encrypt, Decrypt)

**Lambda Expressions:**
```java
e -> { button.setStyle(Styles.BUTTON_HOVER); }
```
- Short syntax for anonymous function
- Equivalent to:
```java
new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent e) {
        button.setStyle(Styles.BUTTON_HOVER);
    }
}
```

---

## 🔄 Complete Execution Flow

### **Application Startup:**
```
JVM starts
   ↓
main() executes
   ↓
launch(args) initializes JavaFX
   ↓
start(Stage primaryStage) called
   ↓
Create managers (FileManager, KeyManager)
   ↓
Build UI (layouts, buttons, event handlers)
   ↓
Show window
   ↓
Event loop starts (waits for user input)
```

### **User Encrypts File:**
```
1. User drags file → onDragDropped adds to list
2. User clicks "Set Encryption Key" → KeyManager shows dialog
3. User enters password → KeyManager derives key with SHA-256
4. User selects file in ListView
5. User clicks "Encrypt Selected"
   ↓
6. Validation checks (file selected? key set?)
   ↓
7. CipherSelector.show() opens dialog
8. User chooses cipher (e.g., AES)
   ↓
9. CipherSelector calls CryptoHelper.encryptFile(file, key, (byte)3)
   ↓
10. CryptoHelper:
    - Creates AESCipher via CipherFactory
    - Reads file
    - Calls cipher.encrypt(data)
    - Adds header [ENCR][3]
    - Saves file
11. DialogHelper.showSuccess() - Done! ✅
```

---

## 📚 Key Concepts Demonstrated

### **1. JavaFX Application Lifecycle**
- `main()` → `launch()` → `start()` → `stop()`
- Understanding the framework's execution order

### **2. Observer Pattern**
- `ObservableList<String> uiFileList`
- ListView automatically updates when list changes
- No manual refresh needed

### **3. Event-Driven Programming**
- Button clicks trigger event handlers
- Drag-and-drop events (DragOver, DragExited, DragDropped)
- Application reacts to user actions

### **4. Composition**
- Main **has-a** FileManager
- Main **has-a** KeyManager
- Not inheritance, but object composition

### **5. Delegation**
- Main doesn't manage files directly → delegates to FileManager
- Main doesn't manage keys directly → delegates to KeyManager
- Separation of concerns

### **6. Guard Clauses**
- Early returns for validation
- Cleaner than nested if statements
```java
if (selectedFile == null) return;
if (!keyManager.hasKey()) return;
// proceed with operation
```

### **7. Anonymous Inner Classes**
- Event handlers implemented as anonymous classes
- Concise event handling

### **8. Lambda Expressions**
- Mouse hover effects: `e -> button.setStyle(...)`
- Modern Java syntax (Java 8+)

---

## 🎓 For Your Technical Review

**When explaining Main.java, emphasize:**

1. **"This is the entry point and UI controller"**
   - Sets up the entire interface
   - Coordinates all managers

2. **"Uses ObservableList for reactive UI"**
   - When data changes, UI updates automatically
   - Observer pattern in practice

3. **"Demonstrates event-driven architecture"**
   - Application responds to user actions
   - Event handlers for buttons, drag-and-drop

4. **"Follows separation of concerns"**
   - Main handles UI
   - FileManager handles file operations
   - KeyManager handles encryption keys
   - Each class has ONE responsibility

5. **"Integrates with OOP cipher system"**
   - Line 184: `CryptoHelper.decryptFile()` uses Cipher classes
   - Shows full stack integration

---

# 2. CipherSelector.java - Cipher Selection Dialog

**Purpose:** Displays a dialog window allowing users to choose which cipher to use for encryption/decryption.

## 🎯 Core Responsibilities

- Show cipher options (Caesar, XOR, AES)
- Handle encryption OR decryption based on mode
- Display warnings for weak ciphers (Caesar, XOR)
- Call CryptoHelper with appropriate cipher type
- Show success/error dialogs

---

## 📊 Class Structure

```java
public class CipherSelector {
    public static void show(Stage owner, File file, SecretKey key,
                           ObservableList<String> fileList, boolean isEncrypting)
}
```

**Static Utility Class** - No instance needed, just call `CipherSelector.show(...)`

### **Parameters Explained:**

| Parameter | Type | Purpose |
|-----------|------|---------|
| `owner` | `Stage` | Parent window (for positioning dialog) |
| `file` | `File` | The file to encrypt/decrypt |
| `key` | `SecretKey` | The encryption/decryption key |
| `fileList` | `ObservableList<String>` | UI file list (unused now but kept for compatibility) |
| `isEncrypting` | `boolean` | `true` = encryption mode, `false` = decryption mode |

---

## 🏗️ Dialog Construction (Lines 20-28)

```java
Stage cipherStage = new Stage();
cipherStage.setTitle(isEncrypting ? "Select Encryption Cipher" : "Select Decryption Cipher");

VBox cipherBox = new VBox(15);
cipherBox.setAlignment(Pos.CENTER);

Label titleLabel = new Label(isEncrypting ? "Choose encryption method:" : "Choose decryption method:");
```

**Ternary Operator:**
```java
condition ? valueIfTrue : valueIfFalse
```
- Title changes based on mode
- Label text changes based on mode

---

## 🔘 Button 1: Caesar Cipher (Lines 30-62)

### **Encryption Mode (Lines 35-50):**

```java
if (isEncrypting) {
    boolean confirmed = DialogHelper.showConfirm("Caesar Cipher Warning",
            "⚠️ WARNING: Caesar cipher does not verify keys!\n\n" +
            "If you forget your encryption key, your data will be PERMANENTLY LOST.\n\n" +
            "Consider using AES encryption for important data.\n\n" +
            "Do you want to continue?");
    
    if (confirmed) {
        try {
            CryptoHelper.encryptFile(file, key, (byte) 1);  // 1 = Caesar
            DialogHelper.showSuccess("Caesar Encryption", "File encrypted successfully!");
        } catch (Exception e) {
            DialogHelper.showError("Encryption Error", e.getMessage());
        }
        cipherStage.close();
    }
}
```

**Flow Breakdown:**

1. **Show confirmation dialog** with warning
2. **If user clicks "Continue":**
   - Call `CryptoHelper.encryptFile(file, key, (byte) 1)`
   - `(byte) 1` = Caesar cipher type code
   - Wrapped in try-catch for safety
   - Show success dialog
   - Close cipher selection window
3. **If user clicks "Cancel":**
   - Dialog closes
   - Returns to cipher selection (window stays open)

**Why the warning?**
- Caesar and XOR don't have authentication
- Wrong password → garbage data (not detected)
- AES-GCM has authentication tag → detects wrong password

---

### **Decryption Mode (Lines 52-60):**

```java
else {
    try {
        CryptoHelper.decryptFile(file, key);
        DialogHelper.showSuccess("Caesar Decryption", "File decrypted successfully!");
    } catch (Exception e) {
        DialogHelper.showError("Decryption Error", e.getMessage());
    }
    cipherStage.close();
}
```

**Key Difference:**
- No cipher type parameter needed!
- `CryptoHelper.decryptFile()` auto-detects from file header
- No warning needed (already encrypted)

---

## 🔘 Button 2: XOR Cipher (Lines 64-96)

**Exact same pattern as Caesar**, but with:
- `CryptoHelper.encryptFile(file, key, (byte) 2)` - type 2 = XOR
- Different warning text
- Different success message

**Code reuse opportunity:**
- These are very similar
- Could be refactored to a helper method
- But clarity > brevity for educational code

---

## 🔘 Button 3: AES Encryption (Lines 98-120)

```java
aesBtn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        if (isEncrypting) {
            try {
                CryptoHelper.encryptFile(file, key, (byte) 3);  // 3 = AES
                DialogHelper.showSuccess("AES Encryption", "File encrypted successfully!");
            } catch (Exception e) {
                DialogHelper.showError("Encryption Error", e.getMessage());
            }
        } else {
            try {
                CryptoHelper.decryptFile(file, key);
                DialogHelper.showSuccess("AES Decryption", "File decrypted successfully!");
            } catch (Exception e) {
                DialogHelper.showError("Decryption Error", e.getMessage());
            }
        }
        cipherStage.close();
    }
});
```

**Key Differences:**
- **No warning dialog!** - AES is secure
- Same encryption/decryption pattern
- `(byte) 3` = AES cipher type

---

## 🎨 Window Positioning (Lines 125-127)

```java
cipherStage.setX(owner.getX() + owner.getWidth() + 10);
cipherStage.setY(owner.getY());
```

**Visual:**
```
┌──────────────┐    ┌──────────────┐
│    Main      │    │   Cipher     │
│   Window     │    │   Selector   │
│              │ 10 │              │
└──────────────┘    └──────────────┘
       owner              dialog
```

**Positions dialog to the right of main window with 10px gap**

---

## 🔄 Complete Flow

### **Encryption:**
```
User clicks "Encrypt Selected" in Main
   ↓
Main.java calls: CipherSelector.show(..., true)
   ↓
Dialog appears with 3 buttons: Caesar, XOR, AES
   ↓
User clicks "Caesar Cipher"
   ↓
Warning dialog: "Caesar cipher doesn't verify keys!"
   ↓
User clicks "Continue"
   ↓
CryptoHelper.encryptFile(file, key, (byte) 1)
   ↓
CipherFactory.createCipherByType(1, key) → returns CaesarCipher object
   ↓
CaesarCipher.encrypt(data)
   ↓
FileHeaderUtil.createHeader(1) → [E][N][C][R][1][0][0][0]
   ↓
Save: header + encrypted data
   ↓
DialogHelper.showSuccess("File encrypted successfully!")
   ↓
Cipher selector closes
```

---

## 📚 Key Concepts Demonstrated

### **1. Static Utility Method**
```java
public static void show(...)
```
- No need to create instance: `new CipherSelector()`
- Just call: `CipherSelector.show(...)`

### **2. Mode-based Behavior**
```java
if (isEncrypting) {
    // encryption logic
} else {
    // decryption logic
}
```
- One class handles both modes
- Cleaner than separate EncryptionDialog and DecryptionDialog classes

### **3. User Confirmation**
```java
boolean confirmed = DialogHelper.showConfirm(...);
if (confirmed) {
    // proceed
}
```
- Important for destructive operations
- Prevents accidental weak cipher usage

### **4. Exception Handling**
```java
try {
    CryptoHelper.encryptFile(...);
} catch (Exception e) {
    DialogHelper.showError("...", e.getMessage());
}
```
- All operations wrapped in try-catch
- User-friendly error messages
- Application doesn't crash on error

### **5. Integration with OOP System**
```java
CryptoHelper.encryptFile(file, key, (byte) 1)
```
- Internally calls:
  - `CipherFactory.createCipherByType(1, key)` → Factory Pattern
  - Returns `CaesarCipher` object → Polymorphism
  - Calls `cipher.encrypt(data)` → Abstraction
- UI layer doesn't know about Cipher classes directly!

---

## 🎓 For Your Technical Review

**Key points to mention:**

1. **"This is a static utility class for cipher selection"**
   - Displays modal dialog
   - Handles both encryption and decryption modes

2. **"Implements user safety features"**
   - Warnings for weak ciphers
   - Confirmation dialogs
   - Clear error messages

3. **"Integrates with the Cipher class hierarchy"**
   - Calls CryptoHelper
   - Which uses CipherFactory
   - Which creates Cipher objects
   - Full OOP stack!

4. **"Demonstrates defensive programming"**
   - All operations in try-catch
   - User confirmation for risky operations
   - Clear success/failure feedback

---

# 3. CryptoHelper.java - File Encryption Utility

**Purpose:** Bridges the Cipher class hierarchy with file I/O operations. Provides static utility methods for encrypting and decrypting files using Cipher objects.

**This is the modern replacement for the deprecated CryptoUtils.java**

---

## 🎯 Core Responsibilities

- Encrypt files using specified Cipher type
- Decrypt files with auto-detection of cipher type
- Handle file I/O (reading/writing with headers
)
- Combine headers with encrypted data
- Extract encrypted data from files

---

## 📊 Class Structure

```java
public class CryptoHelper {
    public static void encryptFile(File file, SecretKey key, byte cipherType) throws Exception
    public static void decryptFile(File file, SecretKey key) throws Exception
    private static byte[] combineArrays(byte[] a, byte[] b)
    private static byte[] extractData(byte[] fileBytes)
}
```

**All methods are static** - utility class pattern

---

## 🔐 Method 1: encryptFile() - Lines 26-43

```java
public static void encryptFile(File file, SecretKey key, byte cipherType) throws Exception {
    // Create appropriate cipher using Factory pattern
    Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
    
    // Read file data
    byte[] fileData = Files.readAllBytes(file.toPath());
    
    // Encrypt using cipher
    byte[] encrypted = cipher.encrypt(fileData);
    
    // Create header
    byte[] header = FileHeaderUtil.createHeader(cipherType);
    
    // Combine header + encrypted data
    byte[] combined = combineArrays(header, encrypted);
    
    // Write back to file
    Files.write(file.toPath(), combined);
}
```

### **Step-by-Step Breakdown:**

#### **Step 1: Create Cipher Object (Line 28)**
```java
Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
```

**Factory Pattern in action:**
```
cipherType = 1 → CipherFactory returns new CaesarCipher(key)
cipherType = 2 → CipherFactory returns new XORCipher(key)
cipherType = 3 → CipherFactory returns new AESCipher(key)
```

**Polymorphism:**
- All returned as `Cipher` (abstract type)
- Actual type could be any of the three implementations
- Code doesn't care which specific type!

#### **Step 2: Read File (Line 31)**
```java
byte[] fileData = Files.readAllBytes(file.toPath());
```

**Example:**
```
File: hello.txt with "Hello World"
fileData = [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
           H   e    l    l    o    SP  W   o    r    l    d
```

#### **Step 3: Encrypt (Line 34)**
```java
byte[] encrypted = cipher.encrypt(fileData);
```

**Polymorphic method call:**
- If cipher is CaesarCipher → calls CaesarCipher.encrypt()
- If cipher is XORCipher → calls XORCipher.encrypt()
- If cipher is AESCipher → calls AESCipher.encrypt()

**Example with AES:**
```
Input:  [72, 101, 108, 108, 111, ...]
Output: [205, 178, 67, 234, 12, 198, ...] (encrypted + auth tag)
```

#### **Step 4: Create Header (Line 37)**
```java
byte[] header = FileHeaderUtil.createHeader(cipherType);
```

**Header format (8 bytes):**
```
Position: [0] [1] [2] [3] [4]      [5] [6] [7]
Content:  [E] [N] [C] [R] [type]   [0] [0] [0]
Bytes:    69  78  67  82  1/2/3    0   0   0
```

**Example for AES:**
```
header = [69, 78, 67, 82, 3, 0, 0, 0]
          E   N   C   R   AES (reserved)
```

#### **Step 5: Combine Arrays (Line 40)**
```java
byte[] combined = combineArrays(header, encrypted);
```

**Visual:**
```
header:    [69, 78, 67, 82, 3, 0, 0, 0]
encrypted: [205, 178, 67, 234, 12, ...]

combined:  [69, 78, 67, 82, 3, 0, 0, 0, 205, 178, 67, 234, 12, ...]
           └────────── header ────────┘ └─────── encrypted ────────┘
```

#### **Step 6: Write to File (Line 43)**
```java
Files.write(file.toPath(), combined);
```

**Original file is OVERWRITTEN:**
```
Before: "Hello World" (plain text)
After:  [ENCR][3][encrypted binary data] (encrypted)
```

---

## 🔓 Method 2: decryptFile() - Lines 52-73 ⭐ **AUTO-DETECTION MAGIC**

```java
public static void decryptFile(File file, SecretKey key) throws Exception {
    // Detect cipher type from file header
    byte cipherType = FileHeaderUtil.readCipherType(file);
    
    if (cipherType == -1) {
        throw new IOException("File is not encrypted or was not encrypted by this application");
    }
    
    // Create appropriate cipher using Factory pattern
    Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
    
    // Read file
    byte[] fileBytes = Files.readAllBytes(file.toPath());
    
    // Extract encrypted data (skip header)
    byte[] encryptedData = extractData(fileBytes);
    
    // Decrypt using cipher
    byte[] decrypted = cipher.decrypt(encryptedData);
    
    // Write decrypted data back to file
    Files.write(file.toPath(), decrypted);
}
```

### **Step-by-Step Breakdown:**

#### **Step 1: Read Header to Detect Cipher (Line 54)**
```java
byte cipherType = FileHeaderUtil.readCipherType(file);
```

**How it works:**
```
File bytes: [69, 78, 67, 82, 2, 0, 0, 0, 145, 234, ...]
            [E] [N] [C] [R] ↑ ...
                            |
                       cipherType = 2 (XOR!)
```

**Verification:**
1. Check file length ≥ 8 bytes
2. Check bytes [0-3] = "ENCR" (magic bytes)
3. Return byte [4] (cipher type)
4. Return -1 if checks fail

#### **Step 2: Validate Encryption (Line 56-58)**
```java
if (cipherType == -1) {
    throw new IOException("File is not encrypted...");
}
```

**User tries to decrypt plain text file:**
```
hello.txt contains: "Hello World"
Bytes: [72, 101, 108, ...]
       [H] [e]  [l]  ...
       
Not "ENCR" → cipherType = -1 → Exception thrown → Error dialog shown
```

#### **Step 3: Create Appropriate Cipher (Line 61)**
```java
Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
```

**Auto-selection:**
```
If cipherType = 1 → creates CaesarCipher
If cipherType = 2 → creates XORCipher
If cipherType = 3 → creates AESCipher
```

**This is why users don't need to remember which cipher they used!**

#### **Step 4: Read File (Line 64)**
```java
byte[] fileBytes = Files.readAllBytes(file.toPath());
```

**Complete file in memory:**
```
[69, 78, 67, 82, 2, 0, 0, 0, 145, 234, 67, 89, ...]
└───────── header ──────────┘ └── encrypted data ──┘
```

#### **Step 5: Extract Data (Skip Header) (Line 67)**
```java
byte[] encryptedData = extractData(fileBytes);
```

**Using helper method:**
```java
private static byte[] extractData(byte[] fileBytes) {
    int headerSize = FileHeaderUtil.getHeaderSize();  // 8
    byte[] data = new byte[fileBytes.length - headerSize];
    System.arraycopy(fileBytes, headerSize, data, 0, data.length);
    return data;
}
```

**Result:**
```
Input:  [69, 78, 67, 82, 2, 0, 0, 0, 145, 234, 67, 89, ...]
Output:                             [145, 234, 67, 89, ...]
        └────── skip 8 bytes ──────┘ └─ extracted data ──┘
```

#### **Step 6: Decrypt (Line 70)**
```java
byte[] decrypted = cipher.decrypt(encryptedData);
```

**Polymorphic call** - uses the correct cipher's decrypt method

**Example with XOR:**
```
Encrypted: [145, 234, 67, 89, ...]
Key:       [148, 73, 42, ...]

XOR operation reverses encryption:
145 XOR 148 = 13
234 XOR 73  = 163
...

Decrypted: [13, 163, ...] (original data restored!)
```

#### **Step 7: Write Decrypted Data (Line 73)**
```java
Files.write(file.toPath(), decrypted);
```

**File is overwritten with original content:**
```
Before: [ENCR][2][encrypted data]
After:  "Hello World" (plain text restored!)
```

---

## 🔧 Helper Method 1: combineArrays() - Lines 84-88

```java
private static byte[] combineArrays(byte[] a, byte[] b) {
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
}
```

**System.arraycopy() explained:**
```java
System.arraycopy(source, srcPos, dest, destPos, length);
```

**First copy:**
```
a = [1, 2, 3]
result = [0, 0, 0, 0, 0, 0]

System.arraycopy(a, 0, result, 0, 3);

result = [1, 2, 3, 0, 0, 0]
          └─ copied ─┘
```

**Second copy:**
```
b = [4, 5, 6]
result = [1, 2, 3, 0, 0, 0]

System.arraycopy(b, 0, result, 3, 3);

result = [1, 2, 3, 4, 5, 6]
          └─ a ──┘ └─ b ──┘
```

---

## 🔧 Helper Method 2: extractData() - Lines 96-101

```java
private static byte[] extractData(byte[] fileBytes) {
    int headerSize = FileHeaderUtil.getHeaderSize();  // Returns 8
    byte[] data = new byte[fileBytes.length - headerSize];
    System.arraycopy(fileBytes, headerSize, data, 0, data.length);
    return data;
}
```

**Visual example:**
```
fileBytes = [H, E, A, D, E, R, __, __, D, A, T, A]
             0  1  2  3  4  5  6   7   8  9  10 11
             └──────── 8 bytes ────────┘  └─ data ─┘

headerSize = 8
data.length = 12 - 8 = 4

System.arraycopy(fileBytes, 8, data, 0, 4);
                           ↑ start at position 8
                           
data = [D, A, T, A]
```

---

## 🔄 Complete Workflow Comparison

### **Encryption:**
```
1. User: CipherSelector → clicks "AES"
2. CipherSelector: CryptoHelper.encryptFile(file, key, (byte) 3)
3. CryptoHelper: CipherFactory.createCipherByType(3, key)
4. CipherFactory: return new AESCipher(key)
5. CryptoHelper: cipher.encrypt(fileData)
6. AESCipher: Performs AES-GCM encryption
7. CryptoHelper: FileHeaderUtil.createHeader(3) → [E][N][C][R][3]
8. CryptoHelper: combineArrays(header, encrypted)
9. CryptoHelper: Files.write() → overwrites file
10. Done! File is encrypted with header
```

### **Decryption:**
```
1. User: Main → clicks "Decrypt Selected"
2. Main: CryptoHelper.decryptFile(file, key)
3. CryptoHelper: FileHeaderUtil.readCipherType(file)
4. FileHeaderUtil: Reads header, returns 3 (AES)
5. CryptoHelper: CipherFactory.createCipherByType(3, key)
6. CipherFactory: return new AESCipher(key)
7. CryptoHelper: extractData(fileBytes) → removes header
8. CryptoHelper: cipher.decrypt(encryptedData)
9. AESCipher: Performs AES-GCM decryption
10. CryptoHelper: Files.write() → overwrites with decrypted
11. Done! File restored to original
```

---

## 📚 Key Concepts Demonstrated

### **1. Static Utility Class Pattern**
- All methods static
- No instance needed
- Acts as namespace for related functions

### **2. Factory Pattern Integration**
```java
Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
```
- CryptoHelper doesn't know about specific cipher classes
- Delegates object creation to factory
- Loose coupling!

### **3. Polymorphism in Action**
```java
cipher.encrypt(data);  // Could be Caesar, XOR, or AES
cipher.decrypt(data);  // Runtime determines which implementation
```
- Same interface for all ciphers
- Actual behavior depends on concrete type

### **4. Auto-Detection Magic**
```java
byte cipherType = FileHeaderUtil.readCipherType(file);
```
- File "remembers" which cipher was used
- User doesn't need to remember
- Better UX!

### **5. File I/O with Headers**
```
[8-byte header][encrypted data]
```
- Metadata stored with data
- Self-describing file format

### **6. Error Handling**
```java
if (cipherType == -1) {
    throw new IOException("File is not encrypted...");
}
```
- Validates before attempting decryption
- Clear error messages

---

## 🎓 For Your Technical Review

**Key points:**

1. **"CryptoHelper is the modern replacement for CryptoUtils"**
   - Cleaner, smaller (103 lines vs 305)
   - Uses OOP Cipher classes instead of procedural static methods

2. **"Demonstrates Factory Pattern integration"**
   - Doesn't create Cipher objects directly
   - Uses CipherFactory for object creation
   - Loose coupling between layers

3. **"Implements auto-detection for better UX"**
   - Reads file header to determine cipher type
   - Users don't need to remember which cipher they used
   - Reduces user error

4. **"Shows proper separation of concerns"**
   - CryptoHelper: File I/O + header management
   - Cipher classes: Encryption/decryption logic
   - FileHeaderUtil: Header format management
   - Each class has ONE job

5. **"Polymorphism enables flexible encryption"**
   - Same code works with all three ciphers
   - Easy to add new ciphers
   - Demonstrates OOP power

---

(Continue to next section...)
