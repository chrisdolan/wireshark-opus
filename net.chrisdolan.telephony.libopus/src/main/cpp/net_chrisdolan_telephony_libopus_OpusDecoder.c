#include <opus/opus.h>
#include "net_chrisdolan_telephony_libopus_OpusDecoder.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_net_chrisdolan_telephony_libopus_OpusDecoder_nativeCreate
  (JNIEnv *env, jobject self, jint samplerate, jint channels)
{
    int error = OPUS_OK;
    int nRate = samplerate;
    int nChannels = channels;
    OpusDecoder *dec = opus_decoder_create(nRate, nChannels, &error);
    if (error == OPUS_OK) {
        jlong out = (jlong) dec;
        return out;
    } else {
        return (jlong)0;
    }
}

JNIEXPORT jint JNICALL Java_net_chrisdolan_telephony_libopus_OpusDecoder_nativeParseChannels
  (JNIEnv *env, jobject self, jbyteArray packetArr)
{
    jint nChannels = 0;

    jbyte* pkt = env->GetByteArrayElements(packetArr, NULL);
    if (pkt) {
        nChannels = opus_packet_get_nb_channels((const unsigned char *)pkt);
    }
    env->ReleaseByteArrayElements(packetArr, pkt, JNI_ABORT);

    return nChannels;
}

JNIEXPORT jint JNICALL Java_net_chrisdolan_telephony_libopus_OpusDecoder_nativeDecode
  (JNIEnv *env, jobject self, jlong nativeDecoder, jbyteArray packetArr, jshortArray outArr, jint nframes)
{
    int decoded = 0;
    jsize pckLen = env->GetArrayLength(packetArr);
    jbyte* pkt = env->GetByteArrayElements(packetArr, NULL);
    if (pkt) {
        jshort* out = env->GetShortArrayElements(outArr, NULL);
        if (out) {
            decoded = opus_decode((OpusDecoder *) nativeDecoder, (const unsigned char *)pkt, (opus_int32)pckLen, (opus_int16*)out, (int)nframes, (int)0);
        }
        env->ReleaseShortArrayElements(outArr, out, 0);
    }
    env->ReleaseByteArrayElements(packetArr, pkt, JNI_ABORT);

    return decoded;
}

JNIEXPORT void JNICALL Java_net_chrisdolan_telephony_libopus_OpusDecoder_nativeDestroy
  (JNIEnv *env, jobject self, jlong nativeDecoder)
{
    OpusDecoder *dec = (OpusDecoder *) nativeDecoder;
    opus_decoder_destroy(dec);
}

#ifdef __cplusplus
}
#endif
