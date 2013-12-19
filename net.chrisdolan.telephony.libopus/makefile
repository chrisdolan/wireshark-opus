
H = src/main/cpp/net_chrisdolan_telephony_libopus_OpusDecoder.h
C = src/main/cpp/net_chrisdolan_telephony_libopus_OpusDecoder.c
JNILIB = target/native/libopus.jnilib

C_ARGS = -Wall -Wmissing-prototypes \
	-I/System/Library/Frameworks/JavaVM.framework/Headers -L/System/Library/Frameworks/JavaVM.framework/Libraries -lclient64 \
	-I/sw/include -L/sw/lib -lopus

$(H) : target/classes/net/chrisdolan/telephony/libopus/OpusDecoder.class
	javah -classpath target/classes -d src/main/cpp net.chrisdolan.telephony.libopus.OpusDecoder

$(JNILIB) : $(H) $(C)
	mkdir -p target/native
	$(CXX) -shared $(C_ARGS) -o $(JNILIB) $(C)

all : $(JNILIB)
