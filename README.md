# Cryptify

Cryptify is a JavaFX desktop app for text file encryption and decryption.

It supports three modes:
- Caesar
- XOR
- AES-GCM

Use AES-GCM for any real data. Caesar and XOR are included for learning/demo use.

## Dev Setup

Requirements:
- Java 11+
- `7z` (used by setup script)

First-time setup after clone:
```bash
./setup
./compile
./run
```

Notes:
- `setup` creates `dependencies/` and puts JavaFX files there.
- `compile` and `run` call `setup` automatically.

## Release Packaging

```bash
./package
```

This creates `release/<date>/` with:
- `encryptify.jar`
- launchers (`encryptify`, `encryptify.bat`)
- `dependencies/`

## Usage

1. Add a `.txt` file (drag/drop or file picker)
2. Set an encryption key
3. Encrypt or decrypt

## Project Note

Recent commits include cleanup and script updates. A lot of this work existed locally before being pushed to GitHub.
