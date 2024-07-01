use jni::{
    objects::{JByteArray, JClass},
    sys::jfloat,
    JNIEnv,
};

use crate::{
    image::Image,
    logger::{AndroidLogger, Logger},
    SsimBuilder,
};

#[no_mangle]
pub extern "C" fn Java_com_whoevencares_ssimand_NativeLib_newSsimBuilder(
    mut env: JNIEnv,
    _: JClass,
    buf_a: JByteArray,
    buf_b: JByteArray,
) -> jfloat {
    let buf_a = env.convert_byte_array(&buf_a).unwrap();
    let buf_b = env.convert_byte_array(&buf_b).unwrap();
    let img_a = Image::from_buf(&buf_a, 3, 3);
    let img_b = Image::from_buf(&buf_b, 3, 3);
    // todo!("implement please...");
    let mut log = AndroidLogger::new(&mut env, "JNICALL");
    log.d("test from JNI").unwrap();
    let ssim = SsimBuilder::new(&img_a);
    ssim.compare(&img_b, crate::SsimMode::Global, None).unwrap() as f32
    // log.d(len.iter().fold(String::new(), |acc, &num| acc + &num.to_string() + ", ")).unwrap();
    // 0.0
}
