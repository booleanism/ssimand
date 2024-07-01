# SSIMers
A naive implementation of SSIM/MSSIM using Rust.

## Getting Started
### SSIM Mode
```rust
let x = Image::from_buf(&fs::read("tests/path.jpg").unwrap(), 100, 100);
let y = Image::from_buf(&fs::read("tests/path.jpg").unwrap(), 100, 100);

let ssim = SsimBuilder::new(&x);

let res = ssim.compare(&y, SsimMode::Global, Some(8)).unwrap();

assert!(res == 1f64);
```
### MSSIM Mode
```rust
let x = Image::from_buf(&fs::read("tests/path.jpg").unwrap(), 100, 100);
let y = Image::from_buf(&fs::read("tests/path.jpg").unwrap(), 100, 100);

let ssim = SsimBuilder::new(&x);

let res = ssim.compare(&y, SsimMode::Local, Some(8)).unwrap();

assert!(res == 1f64);
```
## Read also
[Nilsson, J., & Akenine-Möller, T. (2020). Understanding SSIM.](https://arxiv.org/abs/2006.13846)