cc -G -D_FILE_OFFSET_BITS=64 -D_LARGEFILE64_SOURCE -D_LARGEFILE_SOURCE *.c -I/opt/hdf5-1.8.2-32/include -I/usr/java/include -I/usr/java/include/solaris /opt/hdf5-1.8.2-32/lib/libhdf5.a -lz -o jhdf5.so