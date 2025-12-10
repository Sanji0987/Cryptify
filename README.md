# Cryptify

A simple file encryption application with Caesar, XOR, and AES encryption support.

## Installation

### Linux / macOS
1. Extract the archive
2. Run: `./Cryptify`

### Windows
1. Extract the archive
2. Install Java (if not installed): https://www.java.com/download
3. Double-click `Cryptify.bat`

**OR** run from Command Prompt:
```
Cryptify.bat
```

## What You Need

- Java 11 or higher
- The files must stay together:
  - `Cryptify.jar`
  - `Cryptify` (Linux/Mac) or `Cryptify.bat` (Windows)
  - `lib/` folder (contains JavaFX libraries)

## Features

- **Caesar Cipher** - Simple shift encryption
- **XOR Cipher** - Fast bitwise encryption  
- **AES-GCM** - Military-grade encryption with authentication

## Usage

1. Launch the application
2. Add files to encrypt/decrypt
3. Set your encryption key
4. Choose your cipher method
5. Encrypt or decrypt!

## Security Notes

- **AES is recommended** for important data
- Caesar and XOR don't verify keys - wrong key = lost data!
- Keep your encryption keys safe!

## License

Free to use and modify.
