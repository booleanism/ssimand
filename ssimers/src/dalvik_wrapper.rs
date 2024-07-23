use jni::{
    objects::{JByteArray, JClass},
    sys::jfloat,
    JNIEnv,
};

static SIZE: (usize, usize) = (224, 224);

use crate::{image::Image, SsimBuilder};

#[no_mangle]
pub extern "C" fn Java_com_whoevencares_ssimand_NativeLib_newSsimBuilder(
    env: JNIEnv,
    _: JClass,
    buf_a: JByteArray,
    buf_b: JByteArray,
) -> jfloat {
    let buf_a = env.convert_byte_array(&buf_a).unwrap();
    let buf_b = env.convert_byte_array(&buf_b).unwrap();
    let img_a = Image::from_buf(&buf_a, SIZE.0, SIZE.1);
    let img_b = Image::from_buf(&buf_b, SIZE.0, SIZE.1);
    let ssim = SsimBuilder::new(&img_a);
    ssim.compare(&img_b, crate::SsimMode::Global, None).unwrap() as f32
}
