/*
 * Copyright 2007 - 2014 ETH Zuerich, CISD and SIS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.systemsx.cisd.hdf5;


import static ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures.INT_NO_COMPRESSION;
import static ch.systemsx.cisd.hdf5.hdf5lib.H5D.H5Dwrite;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5P_DEFAULT;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5S_ALL;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5T_NATIVE_UINT8;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5T_STD_U8LE;

import ch.systemsx.cisd.base.mdarray.MDArray;
import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.hdf5.cleanup.ICallableWithCleanUp;
import ch.systemsx.cisd.hdf5.cleanup.ICleanUpRegistry;

/**
 * The implementation of {@link IHDF5ByteWriter}.
 * 
 * @author Bernd Rinn
 */
class HDF5UnsignedByteWriter extends HDF5ByteReader implements IHDF5UnsignedByteWriter
{
    private final HDF5BaseWriter baseWriter;

    HDF5UnsignedByteWriter(HDF5BaseWriter baseWriter)
    {
        super(baseWriter);
        assert baseWriter != null;

        this.baseWriter = baseWriter;
    }

    // /////////////////////
    // Attributes
    // /////////////////////

    @Override
    public void setAttr(final String objectPath, final String name, final byte value)
    {
        assert objectPath != null;
        assert name != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Object> addAttributeRunnable =
                new ICallableWithCleanUp<Object>()
                    {
                        @Override
                        public Object call(ICleanUpRegistry registry)
                        {
                            if (baseWriter.useSimpleDataSpaceForAttributes)
                            {
                                final int dataSpaceId =
                                        baseWriter.h5.createSimpleDataSpace(new long[]
                                            { 1 }, registry);
                                baseWriter.setAttribute(objectPath, name, H5T_STD_U8LE,
                                        H5T_NATIVE_UINT8, dataSpaceId, new byte[]
                                            { value }, registry);
                            } else
                            {
                                baseWriter.setAttribute(objectPath, name, H5T_STD_U8LE,
                                        H5T_NATIVE_UINT8, -1, new byte[]
                                            { value }, registry);
                            }
                            return null; // Nothing to return.
                        }
                    };
        baseWriter.runner.call(addAttributeRunnable);
    }

    @Override
    public void setArrayAttr(final String objectPath, final String name,
            final byte[] value)
    {
        assert objectPath != null;
        assert name != null;
        assert value != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> setAttributeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    if (baseWriter.useSimpleDataSpaceForAttributes)
                    {
                        final int dataSpaceId = baseWriter.h5.createSimpleDataSpace(new long[]
                            { value.length }, registry);
                        baseWriter.setAttribute(objectPath, name, H5T_STD_U8LE, H5T_NATIVE_UINT8,
                                dataSpaceId, value, registry);
                    } else
                    {
                        final int memoryTypeId =
                                baseWriter.h5.createArrayType(H5T_NATIVE_UINT8, value.length, registry);
                        final int storageTypeId =
                                baseWriter.h5.createArrayType(H5T_STD_U8LE, value.length, registry);
                        baseWriter.setAttribute(objectPath, name, storageTypeId, memoryTypeId, -1, value, 
                                registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(setAttributeRunnable);
    }

    @Override
    public void setMDArrayAttr(final String objectPath, final String name,
            final MDByteArray value)
    {
        assert objectPath != null;
        assert name != null;
        assert value != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> addAttributeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    if (baseWriter.useSimpleDataSpaceForAttributes)
                    {
                        final int dataSpaceId =
                                baseWriter.h5.createSimpleDataSpace(value.longDimensions(), registry);
                        baseWriter.setAttribute(objectPath, name, H5T_STD_U8LE, H5T_NATIVE_UINT8,
                                dataSpaceId, value.getAsFlatArray(), registry);
                    } else
                    {
                        final int memoryTypeId =
                                baseWriter.h5.createArrayType(H5T_NATIVE_UINT8, value.dimensions(),
                                        registry);
                        final int storageTypeId =
                                baseWriter.h5.createArrayType(H5T_STD_U8LE, value.dimensions(),
                                        registry);
                        baseWriter.setAttribute(objectPath, name, storageTypeId, memoryTypeId, -1,
                                value.getAsFlatArray(), registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(addAttributeRunnable);
    }

    @Override
    public void setMatrixAttr(final String objectPath, final String name,
            final byte[][] value)
    {
        setMDArrayAttr(objectPath, name, new MDByteArray(value));
    }
    
    // /////////////////////
    // Data Sets
    // /////////////////////

    @Override
    public void write(final String objectPath, final byte value)
    {
        assert objectPath != null;

        baseWriter.checkOpen();
        baseWriter.writeScalar(objectPath, H5T_STD_U8LE, H5T_NATIVE_UINT8, value);
    }

    @Override
    public void writeArray(final String objectPath, final byte[] data)
    {
        writeArray(objectPath, data, INT_NO_COMPRESSION);
    }

    @Override
    public void writeArray(final String objectPath, final byte[] data,
            final HDF5IntStorageFeatures features)
    {
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    final int dataSetId =
                            baseWriter.getOrCreateDataSetId(objectPath, 
                                H5T_STD_U8LE, new long[]
                                { data.length }, 1, features, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_UINT8, H5S_ALL, H5S_ALL, H5P_DEFAULT, 
                            data);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    @Override
    public void createArray(final String objectPath, final int size)
    {
        createArray(objectPath, size, INT_NO_COMPRESSION);
    }

    @Override
    public void createArray(final String objectPath, final long size, final int blockSize)
    {
        createArray(objectPath, size, blockSize, INT_NO_COMPRESSION);
    }

    @Override
    public void createArray(final String objectPath, final int size,
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert size >= 0;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                            features, new long[] { 0 }, new long[] { size }, 1, registry);

                    } else
                    {
                        baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                            features, new long[] { size }, null, 1, registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    @Override
    public void createArray(final String objectPath, final long size, final int blockSize,
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert size >= 0;
        assert blockSize >= 0 && (blockSize <= size || size == 0);

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                        features, new long[] { size }, new long[]
                        { blockSize }, 1, registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    @Override
    public void writeArrayBlock(final String objectPath, final byte[] data,
            final long blockNumber)
    {
        writeArrayBlockWithOffset(objectPath, data, data.length, data.length * blockNumber);
    }

    @Override
    public void writeArrayBlockWithOffset(final String objectPath, final byte[] data,
            final int dataSize, final long offset)
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    final long[] blockDimensions = new long[]
                        { dataSize };
                    final long[] slabStartOrNull = new long[]
                        { offset };
                    final int dataSetId =
                            baseWriter.h5.openAndExtendDataSet(baseWriter.fileId, objectPath,
                                    baseWriter.fileFormat, new long[]
                                        { offset + dataSize }, -1, registry);
                    final int dataSpaceId = 
                            baseWriter.h5.getDataSpaceForDataSet(dataSetId, registry);
                    baseWriter.h5.setHyperslabBlock(dataSpaceId, slabStartOrNull, blockDimensions);
                    final int memorySpaceId = 
                            baseWriter.h5.createSimpleDataSpace(blockDimensions, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_UINT8, memorySpaceId, dataSpaceId, 
                            H5P_DEFAULT, data);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    /**
     * Writes out a <code>byte</code> matrix (array of rank 2).
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param data The data to write. Must not be <code>null</code>. All columns need to have the
     *            same length.
     */
    @Override
    public void writeMatrix(final String objectPath, final byte[][] data)
    {
        writeMatrix(objectPath, data, INT_NO_COMPRESSION);
    }

    @Override
    public void writeMatrix(final String objectPath, final byte[][] data, 
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert data != null;
        assert HDF5Utils.areMatrixDimensionsConsistent(data);

        writeMDArray(objectPath, new MDByteArray(data), features);
    }

    @Override
    public void createMatrix(final String objectPath, final int sizeX, 
            final int sizeY)
    {
        assert objectPath != null;
        assert sizeX >= 0;
        assert sizeY >= 0;

        createMDArray(objectPath, new int[] { sizeX, sizeY });
    }

    @Override
    public void createMatrix(final String objectPath, final int sizeX, 
            final int sizeY, final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert sizeX >= 0;
        assert sizeY >= 0;

        createMDArray(objectPath, new int[] { sizeX, sizeY }, features);
    }

    @Override
    public void createMatrix(final String objectPath, final long sizeX, final long sizeY,
            final int blockSizeX, final int blockSizeY)
    {
        assert objectPath != null;
        assert sizeX >= 0;
        assert sizeY >= 0;
        assert blockSizeX >= 0 && (blockSizeX <= sizeX || sizeX == 0);
        assert blockSizeY >= 0 && (blockSizeY <= sizeY || sizeY == 0);

        createMDArray(objectPath, new long[] { sizeX, sizeY }, new int[] { blockSizeX, blockSizeY });
    }

    @Override
    public void createMatrix(final String objectPath, final long sizeX, final long sizeY,
            final int blockSizeX, final int blockSizeY, final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert sizeX >= 0;
        assert sizeY >= 0;
        assert blockSizeX >= 0 && (blockSizeX <= sizeX || sizeX == 0);
        assert blockSizeY >= 0 && (blockSizeY <= sizeY || sizeY == 0);

        createMDArray(objectPath, new long[] { sizeX, sizeY }, new int[] { blockSizeX, blockSizeY }, features);
    }

    @Override
    public void writeMatrixBlock(final String objectPath, final byte[][] data,
            final long blockNumberX, final long blockNumberY)
    {
        assert objectPath != null;
        assert data != null;

        writeMDArrayBlock(objectPath, new MDByteArray(data), new long[]
            { blockNumberX, blockNumberY });
    }

    @Override
    public void writeMatrixBlockWithOffset(final String objectPath, final byte[][] data,
            final long offsetX, final long offsetY)
    {
        assert objectPath != null;
        assert data != null;

        writeMDArrayBlockWithOffset(objectPath, new MDByteArray(data, new int[]
            { data.length, data[0].length }), new long[]
            { offsetX, offsetY });
    }

    @Override
    public void writeMatrixBlockWithOffset(final String objectPath, final byte[][] data,
            final int dataSizeX, final int dataSizeY, final long offsetX, final long offsetY)
    {
        assert objectPath != null;
        assert data != null;

        writeMDArrayBlockWithOffset(objectPath, new MDByteArray(data, new int[]
            { dataSizeX, dataSizeY }), new long[]
            { offsetX, offsetY });
    }

    @Override
    public void writeMDArray(final String objectPath, final MDByteArray data)
    {
        writeMDArray(objectPath, data, INT_NO_COMPRESSION);
    }

    @Override
    public void writeMDArray(final String objectPath, final MDByteArray data,
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    final int dataSetId =
                            baseWriter.getOrCreateDataSetId(objectPath, H5T_STD_U8LE, 
                                    data.longDimensions(), 1, features, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_UINT8, H5S_ALL, H5S_ALL, H5P_DEFAULT, 
                            data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    @Override
    public void createMDArray(final String objectPath, final int[] dimensions)
    {
        createMDArray(objectPath, dimensions, INT_NO_COMPRESSION);
    }

    @Override
    public void createMDArray(final String objectPath, final long[] dimensions,
            final int[] blockDimensions)
    {
        createMDArray(objectPath, dimensions, blockDimensions, INT_NO_COMPRESSION);
    }

    @Override
    public void createMDArray(final String objectPath, final int[] dimensions,
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert dimensions != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        final long[] nullDimensions = new long[dimensions.length];
                        baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                                features,
                                nullDimensions, MDArray.toLong(dimensions), 1, registry);
                    } else
                    {
                        baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                                features, MDArray.toLong(dimensions), null, 1, registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    @Override
    public void createMDArray(final String objectPath, final long[] dimensions,
            final int[] blockDimensions, final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert dimensions != null;
        assert blockDimensions != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    baseWriter.createDataSet(objectPath, H5T_STD_U8LE, 
                            features, dimensions, 
                            MDArray.toLong(blockDimensions), 1, registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    @Override
    public void writeMDArrayBlock(final String objectPath, final MDByteArray data,
            final long[] blockNumber)
    {
        assert blockNumber != null;

        final long[] dimensions = data.longDimensions();
        final long[] offset = new long[dimensions.length];
        for (int i = 0; i < offset.length; ++i)
        {
            offset[i] = blockNumber[i] * dimensions[i];
        }
        writeMDArrayBlockWithOffset(objectPath, data, offset);
    }

    @Override
    public void writeMDArrayBlockWithOffset(final String objectPath, final MDByteArray data,
            final long[] offset)
    {
        assert objectPath != null;
        assert data != null;
        assert offset != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    final long[] dimensions = data.longDimensions();
                    assert dimensions.length == offset.length;
                    final long[] dataSetDimensions = new long[dimensions.length];
                    for (int i = 0; i < offset.length; ++i)
                    {
                        dataSetDimensions[i] = offset[i] + dimensions[i];
                    }
                    final int dataSetId =
                            baseWriter.h5.openAndExtendDataSet(baseWriter.fileId, objectPath,
                                    baseWriter.fileFormat, dataSetDimensions, -1, registry);
                    final int dataSpaceId = 
                            baseWriter.h5.getDataSpaceForDataSet(dataSetId, registry);
                    baseWriter.h5.setHyperslabBlock(dataSpaceId, offset, dimensions);
                    final int memorySpaceId = 
                            baseWriter.h5.createSimpleDataSpace(dimensions, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_UINT8, memorySpaceId, dataSpaceId, 
                            H5P_DEFAULT, data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    @Override
    public void writeMDArrayBlockWithOffset(final String objectPath, final MDByteArray data,
            final int[] blockDimensions, final long[] offset, final int[] memoryOffset)
    {
        assert objectPath != null;
        assert data != null;
        assert offset != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                @Override
                public Void call(ICleanUpRegistry registry)
                {
                    final long[] memoryDimensions = data.longDimensions();
                    assert memoryDimensions.length == offset.length;
                    final long[] longBlockDimensions = MDArray.toLong(blockDimensions);
                    assert longBlockDimensions.length == offset.length;
                    final long[] dataSetDimensions = new long[blockDimensions.length];
                    for (int i = 0; i < offset.length; ++i)
                    {
                        dataSetDimensions[i] = offset[i] + blockDimensions[i];
                    }
                    final int dataSetId =
                            baseWriter.h5.openAndExtendDataSet(baseWriter.fileId, objectPath,
                                    baseWriter.fileFormat, dataSetDimensions, -1, registry);
                    final int dataSpaceId = 
                            baseWriter.h5.getDataSpaceForDataSet(dataSetId, registry);
                    baseWriter.h5.setHyperslabBlock(dataSpaceId, offset, longBlockDimensions);
                    final int memorySpaceId = 
                            baseWriter.h5.createSimpleDataSpace(memoryDimensions, registry);
                    baseWriter.h5.setHyperslabBlock(memorySpaceId, MDArray.toLong(memoryOffset),
                            longBlockDimensions);
                    H5Dwrite(dataSetId, H5T_NATIVE_UINT8, memorySpaceId, dataSpaceId,
                            H5P_DEFAULT, data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }
}
