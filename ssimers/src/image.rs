use std::error::Error;

pub type Pixels = Vec<Vec<u8>>;

#[derive(PartialEq)]
pub struct PixelsNode {
    data: Option<Pixels>,
    width: usize,
    height: usize,
}

impl PixelsNode {
    pub fn new(pix: Pixels) -> Self {
        let w = pix.len();
        let h = pix[0].len();
        Self { data: Some(pix), width: w, height: h }
    }
}

impl<'a> Into<Image> for PixelsNode {
    fn into(self) -> Image {
        Image { pixels: self }
    }
}

#[derive(PartialEq)]
pub struct Image {
    pub pixels: PixelsNode,
}

impl<'a> Image {
    pub fn from_buf(buf: &'a Vec<u8>, width: usize, height: usize) -> Self {
        let pixels = image::load_from_memory(&buf)
            .unwrap()
            .resize_to_fill(width as u32, height as u32, image::imageops::FilterType::Gaussian)
            .to_luma8()
            .to_vec();

        let pixels = Image::into_2d(&pixels, width as u32, height as u32).unwrap();

        Self { pixels: PixelsNode::new(pixels) }
    }

    pub fn from_pixels_vec(&self, pix: &'a Vec<u8>) -> Self {
        let vec_2d =
            Image::into_2d(pix, self.pixels.width as u32, self.pixels.height as u32).unwrap();
        Self { pixels: PixelsNode::new(vec_2d) }
    }

    pub fn pixels(&'a self) -> Option<&'a Pixels> {
        self.pixels.data.as_ref()
    }

    pub fn sub_pixels(&self, win_size: usize) -> Result<Vec<PixelsNode>, Box<dyn Error>> {
        if win_size > self.pixels.width {
            panic!("windows size is bigger than size of actual pixels");
        }

        let mut subs = Vec::<PixelsNode>::new();
        for g in 0..self.pixels.width as usize {
            if g as usize + win_size > self.pixels.width as usize {
                break;
            }
            for h in 0..self.pixels.height as usize {
                if h as usize + win_size > self.pixels.height as usize {
                    break;
                }
                let mut sub = Vec::new();
                for i in g..g + win_size {
                    let mut col = Vec::new();
                    for j in h..h + win_size {
                        col.push(self.pixels.data.as_ref().unwrap()[i][j]);
                    }
                    sub.push(col);
                }
                subs.push(PixelsNode::new(sub));
            }
        }

        Ok(subs)
    }

    fn into_2d(a_vec: &'a Vec<u8>, width: u32, height: u32) -> Result<Pixels, Box<dyn Error>> {
        let mut y = Box::new(Vec::new());
        let mut xy = Vec::<Vec<u8>>::new();

        // flatten vec to 2d vec
        for i in 0..width {
            let c = i * height;
            for j in c..c + height {
                y.push(a_vec[j as usize]);
            }
            xy.push(y.as_ref().to_vec());
            y.clear();
        }

        Ok(xy)
    }

    pub fn flattened(&self) -> Result<Vec<u8>, Box<dyn Error>> {
        Ok(self.pixels.data.as_ref().unwrap().iter().flat_map(|f| f.iter()).copied().collect())
    }

    pub fn length(&self) -> Result<f64, Box<dyn Error>> {
        let pix = self.pixels.data.as_ref().unwrap();
        let len = pix[0].len() * pix.len();

        Ok(len as f64)
    }

    pub fn sum_pixels(&self) -> Result<f64, Box<dyn Error>> {
        let pix_flat = self.flattened()?;

        let mut sum = 0f64;

        for i in pix_flat {
            sum += i as f64
        }

        Ok(sum)
    }
}
