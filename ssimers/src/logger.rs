// #[cfg(target_os = "android")]
use jni::objects::{JClass, JObject, JString, JValue};
// #[cfg(target_os = "android")]
use jni::JNIEnv;
use std::fmt::Debug;
use std::mem::take;

pub trait Logger {
    type E: Debug;

    /// Prints a message at the debug level.
    fn d(&mut self, message: impl AsRef<str>) -> Result<(), Self::E>;
}

// #[cfg(test)]
// pub struct PrintlnLogger {}

// #[cfg(test)]
// impl Logger for PrintlnLogger {
//     type E = !;

//     fn d(&self, message: impl AsRef<str>) -> Result<(), Self::E> {
//         println!("{}", message.as_ref());
//         Ok(())
//     }
// }

// #[cfg(target_os = "android")]
pub struct AndroidLogger<'a, 'b> {
    /// JNI environment.
    env: &'b mut JNIEnv<'a>,
    /// Reference to the android.util.Log class.
    log_class: JClass<'a>,
    /// Tag for log messages.
    tag: JString<'a>,
}

// #[cfg(target_os = "android")]
impl<'a, 'b> AndroidLogger<'a, 'b> {
    pub fn new(env: &'b mut JNIEnv<'a>, tag: &str) -> Self {
        let log_class = env.find_class("android/util/Log").unwrap();
        let tag_string = env.new_string(tag).unwrap();

        Self { env: env, log_class: log_class, tag: tag_string }
    }
}

// #[cfg(target_os = "android")]
impl<'a, 'b> Logger for AndroidLogger<'a, 'b> {
    type E = jni::errors::Error;

    fn d(&mut self, message: impl AsRef<str>) -> Result<(), Self::E> {
        let class = &self.log_class;
        let tag_self = self.tag.clone();
        let tag = take(&mut self.tag);
        // let tag = &self.tag;
        self.env.call_static_method(
            class,
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            &[
                JValue::Object(&JObject::from(tag)),
                JValue::Object(&JObject::from(self.env.new_string(message)?)),
            ],
        )?;
        unsafe {
            self.tag = JString::from_raw(tag_self);
        }
        Ok(())
    }
}
