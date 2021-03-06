# The path (cygwin) to the NDK
ANDROID_NDK_BASE = /cygdrive/c/android-ndk-r4b

# The name of the native library
LIBNAME = libAndroidDemo.so

# Find all the C++ sources in the jni folder
#SOURCES = $(wildcard jni/*.cpp)

# Find all subfolders of native folder
SRC_DIRS = $(shell find jni -type d -print)

# Find all the C++ sources in the source folders
SOURCES = $(foreach DIR,$(SRC_DIRS),$(wildcard $(DIR)/*.cpp))



# Derive the object file names from the source file names
OBJS	= $(SOURCES:jni/%.cpp=obj/%.o)

# The native library with symbols for gdb (eventually)
SYMS	= obj/$(LIBNAME)

# The real native library stripped of symbols
LIB		= libs/armeabi/$(LIBNAME)

# Android toolchain prefix
ANDROID_TOOL_PREFIX = $(ANDROID_NDK_BASE)/build/prebuilt/windows/arm-eabi-4.4.0/bin/arm-eabi-

# Android compiler flags
ANDROID_CFLAGS = -march=armv5te -mtune=xscale -msoft-float -fpic -mthumb-interwork \
		-ffunction-sections -funwind-tables -fstack-protector -fno-short-enums \
		-fno-exceptions -fno-rtti \
		-D__ARM_ARCH_5__ -D__ARM_ARCH_5T__ -D__ARM_ARCH_5E__ -D__ARM_ARCH_5TE__ -DANDROID -O2 -DNDEBUG -g \
		-I$(ANDROID_NDK_BASE)/build/platforms/android-8/arch-arm/usr/include

# Android link flags
ANDROID_LDFLAGS = -nostdlib -shared -Bsymbolic --no-undefined

# Android lib dir and libs
ANDROID_LIBS = -L$(ANDROID_NDK_BASE)/build/platforms/android-8/arch-arm/usr/lib \
		-llog -lc -lstdc++ -lm \
		$(ANDROID_NDK_BASE)/build/prebuilt/windows/arm-eabi-4.4.0/lib/gcc/arm-eabi/4.4.0/thumb/libgcc.a

# Default CDT target is all, build the native library
all:	$(LIB)

# Strip the symbols to create the runtime library
$(LIB):	$(SYMS)
	@mkdir -p $(dir $@)
	$(ANDROID_TOOL_PREFIX)strip --strip-debug $^ -o $@

# Link the objects into the symbol library
$(SYMS):	$(OBJS)
	@mkdir -p $(dir $@)
	$(ANDROID_TOOL_PREFIX)ld $(ANDROID_LDFLAGS) -o $(SYMS) $(OBJS) $(ANDROID_LIBS)

# Compile also generating dependencies with -MD
obj/%.o:	jni/%.cpp
	@mkdir -p $(dir $@)
	$(ANDROID_TOOL_PREFIX)g++ $(ANDROID_CFLAGS) -MD -o $@ -c $<

# Optionally include the generated dependencies
-include $(OBJS:%.o=%.d)

# Clean by deleting all the objs and the lib
clean:
	rm -fr obj libs
