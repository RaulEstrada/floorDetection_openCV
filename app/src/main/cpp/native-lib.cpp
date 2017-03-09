#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_pvala_floor_1detection_1web_1service_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
