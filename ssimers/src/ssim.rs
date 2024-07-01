use std::error::Error;

use crate::image::Image;

const K1: f64 = 0.01f64;
const K2: f64 = 0.03f64;
const L: usize = 255;
const C1: f64 = (K1 * L as f64) * (K1 * L as f64);
const C2: f64 = (K2 * L as f64) * (K2 * L as f64);
const C3: f64 = C2 / 2f64;
const ALPHA: f64 = 1f64;
const BETA: f64 = 1f64;
const DELTA: f64 = 1f64;

pub trait SsimOps<'a> {
    fn pixel_mean(&self) -> Result<f64, Box<dyn Error>>;
    fn variance(&self) -> Result<f64, Box<dyn Error>>;
    fn covariance(&self, img_y: &'a Image) -> Result<f64, Box<dyn Error>>;
}

impl<'a> SsimOps<'a> for Image {
    fn pixel_mean(&self) -> Result<f64, Box<dyn Error>> {
        Ok(self.sum_pixels()? / self.length()?)
    }

    fn variance(&self) -> Result<f64, Box<dyn Error>> {
        let pix_flat = self.flattened()?;

        let mut sum = 0f64;
        let mean = self.pixel_mean()?;
        let len = self.length()?;

        for i in pix_flat {
            let n = i as f64 - mean;
            sum += n.powi(2);
        }

        Ok(sum / (len as f64 - 1f64))
    }

    fn covariance(&self, img_y: &'a Image) -> Result<f64, Box<dyn Error>> {
        let flat_x_pix = self.flattened()?;
        let flat_y_pix = img_y.flattened()?;
        let x_mean = self.pixel_mean()?;
        let y_mean = img_y.pixel_mean()?;
        let x_length = self.length()?;

        let mut sum = 0f64;

        if x_length != img_y.length()? {
            panic!("the size not same");
        }

        for i in 0..x_length as usize {
            let lhs = flat_x_pix[i] as f64 - x_mean;
            let rhs = flat_y_pix[i] as f64 - y_mean;

            sum += lhs * rhs;
        }

        Ok(sum / (x_length - 1f64))
    }
}

struct ImageCache<'a> {
    img: &'a Image,
    mean: f64,
    var: f64,
    sum: f64,
}

pub struct Ssim<'a> {
    img_x: Option<ImageCache<'a>>,
    img_y: Option<ImageCache<'a>>,
}

impl<'a> Ssim<'a> {
    pub fn new(img_x: &'a Image) -> Self {
        Self {
            img_x: Some(ImageCache {
                img: img_x,
                mean: img_x.pixel_mean().unwrap(),
                var: img_x.variance().unwrap(),
                sum: img_x.sum_pixels().unwrap(),
            }),
            img_y: None,
        }
    }

    pub fn result(&mut self, img_y: &'a Image) -> Result<f64, Box<dyn Error>> {
        self.img_y = Some(ImageCache {
            img: img_y,
            mean: img_y.pixel_mean()?,
            sum: img_y.sum_pixels()?,
            var: img_y.variance()?,
        });

        let luminance = self.luminance()?;
        let constrast = self.contrast()?;
        let structure = self.structure()?;

        Ok(luminance.powf(ALPHA) * constrast.powf(BETA) * structure.powf(DELTA))
    }

    fn luminance(&self) -> Result<f64, Box<dyn Error>> {
        let numerator =
            2f64 * self.img_x.as_ref().unwrap().mean * self.img_y.as_ref().unwrap().mean + C1;
        let denominator = self.img_x.as_ref().unwrap().mean.powi(2)
            + self.img_y.as_ref().unwrap().mean.powi(2)
            + C1;

        Ok(numerator / denominator)
    }

    fn contrast(&self) -> Result<f64, Box<dyn Error>> {
        let numerator =
            2f64 * self.img_x.as_ref().unwrap().sum * self.img_y.as_ref().unwrap().sum + C2;
        let denominator = self.img_x.as_ref().unwrap().var + self.img_y.as_ref().unwrap().var + C2;

        Ok(numerator / denominator)
    }

    fn structure(&self) -> Result<f64, Box<dyn Error>> {
        let nominator =
            self.img_x.as_ref().unwrap().img.covariance(&self.img_y.as_ref().unwrap().img)? + C3;
        let denominator = self.img_x.as_ref().unwrap().sum * self.img_y.as_ref().unwrap().sum + C3;

        Ok(nominator / denominator)
    }
}
