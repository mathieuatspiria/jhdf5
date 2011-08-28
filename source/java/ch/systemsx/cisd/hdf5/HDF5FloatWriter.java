/*
 * Copyright 2009 ETH Zuerich, CISD.
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

import static ch.systemsx.cisd.hdf5.HDF5FloatStorageFeatures.FLOAT_NO_COMPRESSION;
import static ch.systemsx.cisd.hdf5.hdf5lib.H5D.H5Dwrite;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5P_DEFAULT;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5S_ALL;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5T_IEEE_F32LE;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5T_NATIVE_FLOAT;

import ch.systemsx.cisd.base.mdarray.MDArray;
import ch.systemsx.cisd.base.mdarray.MDFloatArray;
import ch.systemsx.cisd.hdf5.cleanup.ICallableWithCleanUp;
import ch.systemsx.cisd.hdf5.cleanup.ICleanUpRegistry;

/**
 * The implementation of {@link IHDF5FloatWriter}.
 * 
 * @author Bernd Rinn
 */
class HDF5FloatWriter implements IHDF5FloatWriter
{
    private final HDF5BaseWriter baseWriter;

    HDF5FloatWriter(HDF5BaseWriter baseWriter)
    {
        assert baseWriter != null;

        this.baseWriter = baseWriter;
    }

    // /////////////////////
    // Attributes
    // /////////////////////

    public void setFloatAttribute(final String objectPath, final String name, final float value)
    {
        assert objectPath != null;
        assert name != null;

        baseWriter.checkOpen();
        baseWriter.setAttribute(objectPath, name, H5T_IEEE_F32LE, H5T_NATIVE_FLOAT, new float[]
            { value });
    }

    public void setFloatArrayAttribute(final String objectPath, final String name,
            final float[] value)
    {
        assert objectPath != null;
        assert name != null;
        assert value != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> setAttributeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    final int memoryTypeId =
                            baseWriter.h5.createArrayType(H5T_NATIVE_FLOAT, value.length, registry);
                    final int storageTypeId =
                            baseWriter.h5.createArrayType(H5T_IEEE_F32LE, value.length, registry);
                    baseWriter.setAttribute(objectPath, name, storageTypeId, memoryTypeId, value);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(setAttributeRunnable);
    }

    public void setFloatMDArrayAttribute(final String objectPath, final String name,
            final MDFloatArray value)
    {
        assert objectPath != null;
        assert name != null;
        assert value != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> addAttributeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    final int memoryTypeId =
                            baseWriter.h5.createArrayType(H5T_NATIVE_FLOAT, value.dimensions(),
                                    registry);
                    final int storageTypeId =
                            baseWriter.h5.createArrayType(H5T_IEEE_F32LE, value.dimensions(),
                                    registry);
                    baseWriter.setAttribute(objectPath, name, storageTypeId, memoryTypeId,
                            value.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(addAttributeRunnable);
    }

    public void setFloatMatrixAttribute(final String objectPath, final String name,
            final float[][] value)
    {
        setFloatMDArrayAttribute(objectPath, name, new MDFloatArray(value));
    }

    // /////////////////////
    // Data Sets
    // /////////////////////

    public void writeFloat(final String objectPath, final float value)
    {
        assert objectPath != null;

        baseWriter.checkOpen();
        baseWriter.writeScalar(objectPath, H5T_IEEE_F32LE, H5T_NATIVE_FLOAT, value);
    }

    public void writeFloatArray(final String objectPath, final float[] data)
    {
        writeFloatArray(objectPath, data, FLOAT_NO_COMPRESSION);
    }

    public void writeFloatArray(final String objectPath, final float[] data,
            final HDF5FloatStorageFeatures features)
    {
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    final int dataSetId =
                            baseWriter.getOrCreateDataSetId(objectPath, H5T_IEEE_F32LE, new long[]
                                { data.length }, 4, features, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_FLOAT, H5S_ALL, H5S_ALL, H5P_DEFAULT, data);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public void createFloatArray(final String objectPath, final int size)
    {
        createFloatArray(objectPath, size, FLOAT_NO_COMPRESSION);
    }

    public void createFloatArray(final String objectPath, final long size, final int blockSize)
    {
        createFloatArray(objectPath, size, blockSize, FLOAT_NO_COMPRESSION);
    }

    public void createFloatArray(final String objectPath, final int size,
            final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert size >= 0;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features, new long[]
                            { 0 }, new long[]
                            { size }, 4, registry);

                    } else
                    {
                        baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features, new long[]
                            { size }, null, 4, registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    public void createFloatArray(final String objectPath, final long size, final int blockSize,
            final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert size >= 0;
        assert blockSize >= 0 && (blockSize <= size || size == 0);

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features, new long[]
                        { size }, new long[]
                        { blockSize }, 4, registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    public void writeFloatArrayBlock(final String objectPath, final float[] data,
            final long blockNumber)
    {
        writeFloatArrayBlockWithOffset(objectPath, data, data.length, data.length * blockNumber);
    }

    public void writeFloatArrayBlockWithOffset(final String objectPath, final float[] data,
            final int dataSize, final long offset)
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
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
                    H5Dwrite(dataSetId, H5T_NATIVE_FLOAT, memorySpaceId, dataSpaceId, H5P_DEFAULT,
                            data);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    /**
     * Writes out a <code>float</code> matrix (array of rank 2).
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param data The data to write. Must not be <code>null</code>. All columns need to have the
     *            same length.
     */
    public void writeFloatMatrix(final String objectPath, final float[][] data)
    {
        writeFloatMatrix(objectPath, data, FLOAT_NO_COMPRESSION);
    }

    public void writeFloatMatrix(final String objectPath, final float[][] data,
            final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert data != null;
        assert HDF5Utils.areMatrixDimensionsConsistent(data);

        writeFloatMDArray(objectPath, new MDFloatArray(data), features);
    }

    public void createFloatMatrix(final String objectPath, final int blockSizeX,
            final int blockSizeY)
    {
        createFloatMatrix(objectPath, 0, 0, blockSizeX, blockSizeY, FLOAT_NO_COMPRESSION);
    }

    public void createFloatMatrix(final String objectPath, final long sizeX, final long sizeY,
            final int blockSizeX, final int blockSizeY)
    {
        createFloatMatrix(objectPath, sizeX, sizeY, blockSizeX, blockSizeY, FLOAT_NO_COMPRESSION);
    }

    public void createFloatMatrix(final String objectPath, final long sizeX, final long sizeY,
            final int blockSizeX, final int blockSizeY, final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert sizeX >= 0;
        assert sizeY >= 0;
        assert blockSizeX >= 0 && (blockSizeX <= sizeX || sizeX == 0);
        assert blockSizeY >= 0 && (blockSizeY <= sizeY || sizeY == 0);

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    final long[] dimensions = new long[]
                        { sizeX, sizeY };
                    final long[] blockDimensions = new long[]
                        { blockSizeX, blockSizeY };
                    baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features, dimensions,
                            blockDimensions, 4, registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    public void writeFloatMatrixBlock(final String objectPath, final float[][] data,
            final long blockNumberX, final long blockNumberY)
    {
        assert objectPath != null;
        assert data != null;

        writeFloatMDArrayBlock(objectPath, new MDFloatArray(data), new long[]
            { blockNumberX, blockNumberY });
    }

    public void writeFloatMatrixBlockWithOffset(final String objectPath, final float[][] data,
            final long offsetX, final long offsetY)
    {
        assert objectPath != null;
        assert data != null;

        writeFloatMDArrayBlockWithOffset(objectPath, new MDFloatArray(data, new int[]
            { data.length, data[0].length }), new long[]
            { offsetX, offsetY });
    }

    public void writeFloatMatrixBlockWithOffset(final String objectPath, final float[][] data,
            final int dataSizeX, final int dataSizeY, final long offsetX, final long offsetY)
    {
        assert objectPath != null;
        assert data != null;

        writeFloatMDArrayBlockWithOffset(objectPath, new MDFloatArray(data, new int[]
            { dataSizeX, dataSizeY }), new long[]
            { offsetX, offsetY });
    }

    public void writeFloatMDArray(final String objectPath, final MDFloatArray data)
    {
        writeFloatMDArray(objectPath, data, FLOAT_NO_COMPRESSION);
    }

    public void writeFloatMDArray(final String objectPath, final MDFloatArray data,
            final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    final int dataSetId =
                            baseWriter.getOrCreateDataSetId(objectPath, H5T_IEEE_F32LE,
                                    data.longDimensions(), 4, features, registry);
                    H5Dwrite(dataSetId, H5T_NATIVE_FLOAT, H5S_ALL, H5S_ALL, H5P_DEFAULT,
                            data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public void createFloatMDArray(final String objectPath, final int[] dimensions)
    {
        createFloatMDArray(objectPath, dimensions, FLOAT_NO_COMPRESSION);
    }

    public void createFloatMDArray(final String objectPath, final long[] dimensions,
            final int[] blockDimensions)
    {
        createFloatMDArray(objectPath, dimensions, blockDimensions, FLOAT_NO_COMPRESSION);
    }

    public void createFloatMDArray(final String objectPath, final int[] dimensions,
            final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert dimensions != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        final long[] nullDimensions = new long[dimensions.length];
                        baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features,
                                nullDimensions, MDArray.toLong(dimensions), 4, registry);
                    } else
                    {
                        baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features,
                                MDArray.toLong(dimensions), null, 4, registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    public void createFloatMDArray(final String objectPath, final long[] dimensions,
            final int[] blockDimensions, final HDF5FloatStorageFeatures features)
    {
        assert objectPath != null;
        assert dimensions != null;
        assert blockDimensions != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    baseWriter.createDataSet(objectPath, H5T_IEEE_F32LE, features, dimensions,
                            MDArray.toLong(blockDimensions), 4, registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
    }

    public void writeFloatMDArrayBlock(final String objectPath, final MDFloatArray data,
            final long[] blockNumber)
    {
        assert blockNumber != null;

        final long[] dimensions = data.longDimensions();
        final long[] offset = new long[dimensions.length];
        for (int i = 0; i < offset.length; ++i)
        {
            offset[i] = blockNumber[i] * dimensions[i];
        }
        writeFloatMDArrayBlockWithOffset(objectPath, data, offset);
    }

    public void writeFloatMDArrayBlockWithOffset(final String objectPath, final MDFloatArray data,
            final long[] offset)
    {
        assert objectPath != null;
        assert data != null;
        assert offset != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
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
                    H5Dwrite(dataSetId, H5T_NATIVE_FLOAT, memorySpaceId, dataSpaceId, H5P_DEFAULT,
                            data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public void writeFloatMDArrayBlockWithOffset(final String objectPath, final MDFloatArray data,
            final int[] blockDimensions, final long[] offset, final int[] memoryOffset)
    {
        assert objectPath != null;
        assert data != null;
        assert offset != null;

        baseWriter.checkOpen();
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
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
                    H5Dwrite(dataSetId, H5T_NATIVE_FLOAT, memorySpaceId, dataSpaceId, H5P_DEFAULT,
                            data.getAsFlatArray());
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }
}
