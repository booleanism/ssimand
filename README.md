# SSIMand
## Requirements
- Rust (mine 1.81-nightly)
- Androik NDK
- Androik SDK

## Setup
- Add Rust target
```sh
rustup target add aarch64-linux-android armv7-linux-androideabi
```

## Build
```sh
./gradlew ssimersAll assembleDebug
```
