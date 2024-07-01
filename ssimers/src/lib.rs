use std::error::Error;

use crate::image::Image;

use crate::ssim::Ssim;

pub mod dalvik_wrapper;
pub mod image;
pub mod logger;
pub mod ssim;
// extern crate image;
// extern crate ;
// pub use crate::image;

pub enum SsimMode {
    Local,
    Global,
}

pub struct SsimBuilder<'a>(&'a Image);

impl<'a> SsimBuilder<'a> {
    pub fn new(img_x: &'a Image) -> Self {
        Self { 0: img_x }
    }

    pub fn compare(
        &self,
        img_y: &'a Image,
        mode: SsimMode,
        win_size: Option<usize>,
    ) -> Result<f64, Box<dyn Error>> {
        let x_length = self.0.length()?;
        if x_length != img_y.length()? || x_length == 0f64 {
            panic!()
        }

        match mode {
            SsimMode::Local => {
                let mut sum_result = 0f64;
                let mut count = 0usize;

                for (i, j) in self
                    .0
                    .sub_pixels(win_size.unwrap())
                    .unwrap()
                    .into_iter()
                    .zip(img_y.sub_pixels(win_size.unwrap()).unwrap().into_iter())
                {
                    let _a: Image = i.into();
                    let _b: Image = j.into();

                    let mut wrapper = Ssim::new(&_a);
                    let result = wrapper.result(&_b)?;

                    sum_result += result;
                    count += 1;
                }

                Ok(sum_result / count as f64)
            }
            SsimMode::Global => {
                let mut wrapper = Ssim::new(self.0);
                let result = wrapper.result(&img_y)?;

                Ok(result)
            }
        }
    }
}
