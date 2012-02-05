/*
 * Copyright 2010 ETH Zuerich, CISD
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

import static ch.systemsx.cisd.hdf5.hdf5lib.H5D.H5Dwrite;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5P_DEFAULT;
import static ch.systemsx.cisd.hdf5.hdf5lib.HDF5Constants.H5S_ALL;
import ncsa.hdf.hdf5lib.exceptions.HDF5JavaException;

import ch.systemsx.cisd.base.mdarray.MDArray;
import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.cleanup.ICallableWithCleanUp;
import ch.systemsx.cisd.hdf5.cleanup.ICleanUpRegistry;

/**
 * The implementation of {@link IHDF5EnumWriter}.
 * 
 * @author Bernd Rinn
 */
class HDF5EnumWriter extends HDF5EnumReader implements IHDF5EnumWriter
{
    private final HDF5BaseWriter baseWriter;

    HDF5EnumWriter(HDF5BaseWriter baseWriter)
    {
        super(baseWriter);
        this.baseWriter = baseWriter;
    }

    // /////////////////////
    // Value creation
    // /////////////////////

    public HDF5EnumerationValue newVal(String typeName, String[] options, String value)
    {
        return new HDF5EnumerationValue(getEnumType(typeName, options), value);
    }

    public HDF5EnumerationValue newVal(String typeName, String[] options, int value)
    {
        return new HDF5EnumerationValue(getEnumType(typeName, options), value);
    }

    public HDF5EnumerationValue newVal(String typeName, String[] options, short value)
    {
        return new HDF5EnumerationValue(getEnumType(typeName, options), value);
    }

    public HDF5EnumerationValue newVal(String typeName, String[] options, byte value)
    {
        return new HDF5EnumerationValue(getEnumType(typeName, options), value);
    }

    public <T extends Enum<T>> HDF5EnumerationValue newVal(String typeName, Enum<T> value)
    {
        return new HDF5EnumerationValue(getEnumType(typeName, getEnumClass(value)), value);
    }

    public HDF5EnumerationValueArray newArray(String typeName, String[] options, String[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueArray newArray(String typeName, String[] options, int[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueArray newArray(String typeName, String[] options, short[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueArray newArray(String typeName, String[] options, byte[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(typeName, options), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueArray newArray(String typeName, Enum<T>[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(typeName, getEnumClass(values)), values);
    }

    public HDF5EnumerationValueMDArray newMDArray(String typeName, String[] options,
            MDArray<String> values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueMDArray newMDArray(String typeName, String[] options, MDIntArray values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueMDArray newMDArray(String typeName, String[] options,
            MDShortArray values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(typeName, options), values);
    }

    public HDF5EnumerationValueMDArray newMDArray(String typeName, String[] options, MDByteArray values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(typeName, options), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueMDArray newMDArray(String typeName,
            MDArray<Enum<T>> values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(typeName, getEnumClass(values)), values);
    }

    public HDF5EnumerationValue newAnonVal(String[] options, String value)
    {
        return new HDF5EnumerationValue(getAnonType(options), value);
    }

    public HDF5EnumerationValue newAnonVal(String[] options, int value)
    {
        return new HDF5EnumerationValue(getAnonType(options), value);
    }

    public HDF5EnumerationValue newAnonVal(String[] options, short value)
    {
        return new HDF5EnumerationValue(getAnonType(options), value);
    }

    public HDF5EnumerationValue newAnonVal(String[] options, byte value)
    {
        return new HDF5EnumerationValue(getAnonType(options), value);
    }

    public <T extends Enum<T>> HDF5EnumerationValue newAnonVal(Enum<T> value)
    {
        return new HDF5EnumerationValue(getAnonType(getEnumClass(value)), value);
    }

    public <T extends Enum<T>> HDF5EnumerationValue newVal(Enum<T> value)
    {
        return new HDF5EnumerationValue(getEnumType(getEnumClass(value)), value);
    }

    public HDF5EnumerationValueArray newAnonArray(String[] options, String[] values)
    {
        return new HDF5EnumerationValueArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueArray newAnonArray(String[] options, int[] values)
    {
        return new HDF5EnumerationValueArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueArray newAnonArray(String[] options, short[] values)
    {
        return new HDF5EnumerationValueArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueArray newAnonArray(String[] options, byte[] values)
    {
        return new HDF5EnumerationValueArray(getAnonType(options), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueArray newAnonArray(Enum<T>[] values)
    {
        return new HDF5EnumerationValueArray(getAnonType(getEnumClass(values)), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueArray newArray(Enum<T>[] values)
    {
        return new HDF5EnumerationValueArray(getEnumType(getEnumClass(values)), values);
    }

    public HDF5EnumerationValueMDArray newAnonMDArray(String[] options, MDArray<String> values)
    {
        return new HDF5EnumerationValueMDArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueMDArray newAnonMDArray(String[] options, MDIntArray values)
    {
        return new HDF5EnumerationValueMDArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueMDArray newAnonMDArray(String[] options, MDShortArray values)
    {
        return new HDF5EnumerationValueMDArray(getAnonType(options), values);
    }

    public HDF5EnumerationValueMDArray newAnonMDArray(String[] options, MDByteArray values)
    {
        return new HDF5EnumerationValueMDArray(getAnonType(options), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueMDArray newAnonMDArray(MDArray<Enum<T>> values)
    {
        return new HDF5EnumerationValueMDArray(getAnonType(getEnumClass(values)), values);
    }

    public <T extends Enum<T>> HDF5EnumerationValueMDArray newMDArray(MDArray<Enum<T>> values)
    {
        return new HDF5EnumerationValueMDArray(getEnumType(getEnumClass(values)), values);
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> Class<Enum<T>> getEnumClass(final Enum<T> value)
    {
        return (Class<Enum<T>>) value.getClass();
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> Class<Enum<T>> getEnumClass(Enum<T>[] data)
    {
        return (Class<Enum<T>>) data.getClass().getComponentType();
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> Class<Enum<T>> getEnumClass(MDArray<Enum<T>> data)
    {
        return (Class<Enum<T>>) data.getAsFlatArray().getClass().getComponentType();
    }

    // /////////////////////
    // Types
    // /////////////////////

    public HDF5EnumerationType getAnonType(String[] values) throws HDF5JavaException
    {
        return getEnumType(null, values, false);
    }

    public HDF5EnumerationType getAnonType(final Class<? extends Enum<?>> enumClass)
            throws HDF5JavaException
    {
        return getEnumType(null, HDF5EnumReader.getEnumOptions(enumClass));
    }

    @Override
    public HDF5EnumerationType getEnumType(final String name, final String[] values)
            throws HDF5JavaException
    {
        return getEnumType(name, values, true);
    }

    @Override
    public HDF5EnumerationType getEnumType(final String nameOrNull, final String[] values,
            final boolean check) throws HDF5JavaException
    {
        baseWriter.checkOpen();
        final int storageDataTypeId =
                getOrCreateEnumDataType(nameOrNull, values, baseWriter.keepDataSetIfExists, check);
        final int nativeDataTypeId =
                baseWriter.h5.getNativeDataType(storageDataTypeId, baseWriter.fileRegistry);
        return new HDF5EnumerationType(baseWriter.fileId, storageDataTypeId, nativeDataTypeId,
                (nameOrNull == null) ? "__anonymous__" : nameOrNull, values);
    }

    @Override
    public HDF5EnumerationType getEnumType(final String name,
            final Class<? extends Enum<?>> enumClass) throws HDF5JavaException
    {
        return getEnumType(name, HDF5EnumReader.getEnumOptions(enumClass), true);
    }

    @Override
    public <T extends Enum<?>> HDF5EnumerationType getEnumType(final String name,
            final Class<T> enumClass, final boolean check) throws HDF5JavaException
    {
        return getEnumType(name, HDF5EnumReader.getEnumOptions(enumClass), check);
    }

    @Override
    public <T extends Enum<?>> HDF5EnumerationType getEnumType(final Class<T> enumClass)
            throws HDF5JavaException
    {
        return getEnumType(enumClass.getSimpleName(), HDF5EnumReader.getEnumOptions(enumClass),
                true);
    }

    @Override
    public HDF5EnumerationType getEnumType(final Class<? extends Enum<?>> enumClass,
            final boolean check) throws HDF5JavaException
    {
        return getEnumType(enumClass.getSimpleName(), HDF5EnumReader.getEnumOptions(enumClass),
                check);
    }

    private int getOrCreateEnumDataType(final String dataTypeNameOrNull, final String[] values,
            boolean committedDataTypeHasPreference, boolean checkIfExists)
    {
        final String dataTypePathOrNull =
                (dataTypeNameOrNull == null) ? null : HDF5Utils.createDataTypePath(
                        HDF5Utils.ENUM_PREFIX, dataTypeNameOrNull);
        final int committedStorageDataTypeId =
                (dataTypePathOrNull == null) ? -1 : baseWriter.getDataTypeId(dataTypePathOrNull);
        final boolean typeExists = (committedStorageDataTypeId >= 0);
        int storageDataTypeId = committedStorageDataTypeId;
        final boolean commitType;
        if ((typeExists == false) || (committedDataTypeHasPreference == false))
        {
            storageDataTypeId = baseWriter.h5.createDataTypeEnum(values, baseWriter.fileRegistry);
            final boolean typesAreEqual =
                    typeExists
                            && baseWriter.h5.dataTypesAreEqual(committedStorageDataTypeId,
                                    storageDataTypeId);
            commitType =
                    (dataTypeNameOrNull != null)
                            && ((typeExists == false) || (typesAreEqual == false));
            if (typeExists && commitType)
            {
                final String replacementDataTypePath =
                        baseWriter.moveLinkOutOfTheWay(dataTypePathOrNull);
                baseWriter.renameNamedDataType(dataTypePathOrNull, replacementDataTypePath);
            }
            if (typesAreEqual)
            {
                storageDataTypeId = committedStorageDataTypeId;
            }
        } else
        {
            commitType = false;
            if (checkIfExists)
            {
                baseWriter.checkEnumValues(storageDataTypeId, values, dataTypeNameOrNull);
            }
        }
        if (commitType)
        {
            baseWriter.commitDataType(dataTypePathOrNull, storageDataTypeId);
        }
        return storageDataTypeId;
    }

    // /////////////////////
    // Attributes
    // /////////////////////

    public void setAttr(final String objectPath, final String name,
            final HDF5EnumerationValue value)
    {
        assert objectPath != null;
        assert name != null;
        assert value != null;

        baseWriter.checkOpen();
        value.getType().check(baseWriter.fileId);
        final int storageDataTypeId = value.getType().getStorageTypeId();
        final int nativeDataTypeId = value.getType().getNativeTypeId();
        baseWriter.setAttribute(objectPath, name, storageDataTypeId, nativeDataTypeId,
                value.toStorageForm());
    }

    public void setAttr(String objectPath, String name, Enum<?> value)
            throws HDF5JavaException
    {
        setAttr(objectPath, name, new HDF5EnumerationValue(
                getEnumType(getEnumClass(value)), value));
    }

    public void setArrayAttr(final String objectPath, final String name,
            final HDF5EnumerationValueArray value)
    {
        baseWriter.setEnumArrayAttribute(objectPath, name, value);
    }

    public void setMDArrayAttr(String objectPath, String name,
            HDF5EnumerationValueMDArray value)
    {
        baseWriter.setEnumMDArrayAttribute(objectPath, name, value);
    }

    // /////////////////////
    // Data Sets
    // /////////////////////

    public void write(final String objectPath, final HDF5EnumerationValue value)
            throws HDF5JavaException
    {
        assert objectPath != null;
        assert value != null;

        baseWriter.checkOpen();
        value.getType().check(baseWriter.fileId);
        final int storageDataTypeId = value.getType().getStorageTypeId();
        final int nativeDataTypeId = value.getType().getNativeTypeId();
        baseWriter.writeScalar(objectPath, storageDataTypeId, nativeDataTypeId,
                value.toStorageForm());
    }

    public void write(final String objectPath, final Enum<?> value) throws HDF5JavaException
    {
        write(objectPath, new HDF5EnumerationValue(getEnumType(getEnumClass(value)), value));
    }

    public void writeArray(final String objectPath, final HDF5EnumerationValueArray data)
            throws HDF5JavaException
    {
        writeArray(objectPath, data, HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public void writeArray(final String objectPath, final HDF5EnumerationValueArray data,
            final HDF5IntStorageFeatures features) throws HDF5JavaException
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        data.getType().check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.isScaling())
                    {
                        features.checkScalingOK(baseWriter.fileFormat);
                        final HDF5IntStorageFeatures actualFeatures =
                                HDF5IntStorageFeatures.createDeflateAndIntegerScaling(features
                                        .getDeflateLevel(), data.getType().getNumberOfBits(),
                                        baseWriter.keepDataIfExists(features));
                        final int dataSetId =
                                baseWriter.getOrCreateDataSetId(objectPath, data.getType()
                                        .getIntStorageTypeId(), new long[]
                                    { data.getLength() }, data.getStorageForm().getStorageSize(),
                                        actualFeatures, registry);
                        H5Dwrite(dataSetId, data.getType().getIntNativeTypeId(), H5S_ALL, H5S_ALL,
                                H5P_DEFAULT, data.toStorageForm());
                        baseWriter.setTypeVariant(dataSetId, HDF5DataTypeVariant.ENUM, registry);
                        baseWriter.setStringAttribute(dataSetId,
                                HDF5Utils.ENUM_TYPE_NAME_ATTRIBUTE, data.getType().getName(), data
                                        .getType().getName().length(), registry);
                    } else
                    {
                        final int dataSetId =
                                baseWriter.getOrCreateDataSetId(objectPath, data.getType()
                                        .getStorageTypeId(), new long[]
                                    { data.getLength() }, data.getStorageForm().getStorageSize(),
                                        features, registry);
                        H5Dwrite(dataSetId, data.getType().getNativeTypeId(), H5S_ALL, H5S_ALL,
                                H5P_DEFAULT, data.toStorageForm());
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public HDF5EnumerationType createArray(final String objectPath,
            final HDF5EnumerationType enumType, final int size)
    {
        return createArray(objectPath, enumType, size,
                HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public HDF5EnumerationType createArray(final String objectPath,
            final HDF5EnumerationType enumType, final long size, final int blockSize)
    {
        return createArray(objectPath, enumType, size, blockSize,
                HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public HDF5EnumerationType createArray(final String objectPath,
            final HDF5EnumerationType enumType, final long size, final int blockSize,
            final HDF5IntStorageFeatures features)
    {
        baseWriter.checkOpen();
        enumType.check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.isScaling())
                    {
                        features.checkScalingOK(baseWriter.fileFormat);
                        final HDF5IntStorageFeatures actualCompression =
                                HDF5IntStorageFeatures.createDeflateAndIntegerScaling(
                                        features.getDeflateLevel(), enumType.getNumberOfBits());
                        final int dataSetId =
                                baseWriter.createDataSet(objectPath,
                                        enumType.getIntStorageTypeId(), actualCompression,
                                        new long[]
                                            { size }, new long[]
                                            { blockSize }, enumType.getStorageForm()
                                                .getStorageSize(), registry);
                        baseWriter.setTypeVariant(dataSetId, HDF5DataTypeVariant.ENUM, registry);
                        baseWriter.setStringAttribute(dataSetId,
                                HDF5Utils.ENUM_TYPE_NAME_ATTRIBUTE, enumType.getName(), enumType
                                        .getName().length(), registry);
                    } else
                    {
                        baseWriter.createDataSet(objectPath, enumType.getStorageTypeId(), features,
                                new long[]
                                    { size }, new long[]
                                    { blockSize }, enumType.getStorageForm().getStorageSize(),
                                registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(createRunnable);
        return enumType;
    }

    public HDF5EnumerationType createArray(final String objectPath,
            final HDF5EnumerationType enumType, final long size,
            final HDF5IntStorageFeatures features)
    {
        baseWriter.checkOpen();
        enumType.check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> createRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        create(new long[]
                            { 0 }, new long[]
                            { size }, registry);
                    } else
                    {
                        create(new long[]
                            { size }, null, registry);
                    }
                    return null; // Nothing to return.
                }

                private void create(final long[] dimensions, final long[] blockDimensionsOrNull,
                        final ICleanUpRegistry registry)
                {
                    if (features.isScaling())
                    {
                        features.checkScalingOK(baseWriter.fileFormat);
                        final HDF5IntStorageFeatures actualCompression =
                                HDF5IntStorageFeatures.createDeflateAndIntegerScaling(
                                        features.getDeflateLevel(), enumType.getNumberOfBits());
                        final int dataSetId =
                                baseWriter.createDataSet(objectPath,
                                        enumType.getIntStorageTypeId(), actualCompression,
                                        dimensions, blockDimensionsOrNull, enumType
                                                .getStorageForm().getStorageSize(), registry);
                        baseWriter.setTypeVariant(dataSetId, HDF5DataTypeVariant.ENUM, registry);
                        baseWriter.setStringAttribute(dataSetId,
                                HDF5Utils.ENUM_TYPE_NAME_ATTRIBUTE, enumType.getName(), enumType
                                        .getName().length(), registry);
                    } else
                    {
                        baseWriter.createDataSet(objectPath, enumType.getStorageTypeId(), features,
                                dimensions, blockDimensionsOrNull, enumType.getStorageForm()
                                        .getStorageSize(), registry);
                    }
                }
            };
        baseWriter.runner.call(createRunnable);
        return enumType;
    }

    public void writeArrayBlock(final String objectPath, final HDF5EnumerationValueArray data,
            final long blockNumber)
    {
        assert objectPath != null;
        assert data != null;

        writeArrayBlockWithOffset(objectPath, data, data.getLength(), data.getLength()
                * blockNumber);
    }

    public void writeArrayBlockWithOffset(final String objectPath,
            final HDF5EnumerationValueArray data, final int dataSize, final long offset)
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        data.getType().check(baseWriter.fileId);
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
                    if (baseWriter.isScaledEnum(dataSetId, registry))
                    {
                        H5Dwrite(dataSetId, data.getType().getIntNativeTypeId(), memorySpaceId,
                                dataSpaceId, H5P_DEFAULT, data.toStorageForm());
                        baseWriter.setTypeVariant(dataSetId, HDF5DataTypeVariant.ENUM, registry);
                        baseWriter.setStringAttribute(dataSetId,
                                HDF5Utils.ENUM_TYPE_NAME_ATTRIBUTE, data.getType().getName(), data
                                        .getType().getName().length(), registry);
                    } else
                    {
                        H5Dwrite(dataSetId, data.getType().getNativeTypeId(), memorySpaceId,
                                dataSpaceId, H5P_DEFAULT, data.toStorageForm());
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public void writeMDArray(final String objectPath, final HDF5EnumerationValueMDArray data)
            throws HDF5JavaException
    {
        writeMDArray(objectPath, data, HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public void writeMDArray(final String objectPath, final HDF5EnumerationValueMDArray data,
            final HDF5IntStorageFeatures features) throws HDF5JavaException
    {
        assert objectPath != null;
        assert data != null;

        baseWriter.checkOpen();
        data.getType().check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(ICleanUpRegistry registry)
                {
                    if (features.isScaling())
                    {
                        features.checkScalingOK(baseWriter.fileFormat);
                        final HDF5IntStorageFeatures actualFeatures =
                                HDF5IntStorageFeatures.createDeflateAndIntegerScaling(features
                                        .getDeflateLevel(), data.getType().getNumberOfBits(),
                                        baseWriter.keepDataIfExists(features));
                        final int dataSetId =
                                baseWriter.getOrCreateDataSetId(objectPath, data.getType()
                                        .getIntStorageTypeId(), data.longDimensions(), data
                                        .getStorageForm().getStorageSize(), actualFeatures,
                                        registry);
                        H5Dwrite(dataSetId, data.getType().getIntNativeTypeId(), H5S_ALL, H5S_ALL,
                                H5P_DEFAULT, data.toStorageForm());
                        baseWriter.setTypeVariant(dataSetId, HDF5DataTypeVariant.ENUM, registry);
                        baseWriter.setStringAttribute(dataSetId,
                                HDF5Utils.ENUM_TYPE_NAME_ATTRIBUTE, data.getType().getName(), data
                                        .getType().getName().length(), registry);
                    } else
                    {
                        final int dataSetId =
                                baseWriter.getOrCreateDataSetId(objectPath, data.getType()
                                        .getStorageTypeId(), data.longDimensions(), data
                                        .getStorageForm().getStorageSize(), features, registry);
                        H5Dwrite(dataSetId, data.getType().getNativeTypeId(), H5S_ALL, H5S_ALL,
                                H5P_DEFAULT, data.toStorageForm());
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    private <T> void writeEnumMDArrayBlockWithOffset(final String objectPath,
            final HDF5EnumerationType enumType, final byte[] data, final long[] dimensions,
            final long[] offset, final long[] dataSetDimensions)
    {
        assert objectPath != null;
        assert enumType != null;
        assert data != null;
        assert offset != null;

        baseWriter.checkOpen();
        enumType.check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(final ICleanUpRegistry registry)
                {
                    final int dataSetId =
                            baseWriter.h5.openAndExtendDataSet(baseWriter.fileId, objectPath,
                                    baseWriter.fileFormat, dataSetDimensions, -1, registry);
                    final int dataSpaceId =
                            baseWriter.h5.getDataSpaceForDataSet(dataSetId, registry);
                    baseWriter.h5.setHyperslabBlock(dataSpaceId, offset, dimensions);
                    final int memorySpaceId =
                            baseWriter.h5.createSimpleDataSpace(dimensions, registry);
                    H5Dwrite(dataSetId, enumType.getNativeTypeId(), memorySpaceId, dataSpaceId,
                            H5P_DEFAULT, data);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
    }

    public void writeMDArrayBlockWithOffset(final String objectPath,
            final HDF5EnumerationValueMDArray data, final long[] offset)
    {
        final long[] dimensions = data.longDimensions();
        final long[] dataSetDimensions = new long[dimensions.length];
        for (int i = 0; i < offset.length; ++i)
        {
            dataSetDimensions[i] = offset[i] + dimensions[i];
        }
        writeEnumMDArrayBlockWithOffset(objectPath, data.getType(), data.toStorageForm(),
                dimensions, offset, dataSetDimensions);
    }

    public void writeMDArrayBlock(String objectPath, HDF5EnumerationValueMDArray data,
            long[] blockNumber)
    {
        final long[] dimensions = data.longDimensions();
        final long[] offset = new long[dimensions.length];
        final long[] dataSetDimensions = new long[dimensions.length];
        for (int i = 0; i < offset.length; ++i)
        {
            offset[i] = blockNumber[i] * dimensions[i];
            dataSetDimensions[i] = offset[i] + dimensions[i];
        }
        writeEnumMDArrayBlockWithOffset(objectPath, data.getType(), data.toStorageForm(),
                dimensions, offset, dataSetDimensions);
    }

    public HDF5EnumerationType createMDArray(String objectPath, HDF5EnumerationType enumType,
            int[] dimensions)
    {
        return createMDArray(objectPath, enumType, dimensions,
                HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public HDF5EnumerationType createMDArray(String objectPath, HDF5EnumerationType enumType,
            long[] dimensions, int[] blockDimensions)
    {
        return createMDArray(objectPath, enumType, dimensions, blockDimensions,
                HDF5IntStorageFeatures.INT_NO_COMPRESSION);
    }

    public HDF5EnumerationType createMDArray(final String objectPath,
            final HDF5EnumerationType enumType, final long[] dimensions,
            final int[] blockDimensions, final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert enumType != null;
        assert dimensions != null;
        assert blockDimensions != null;

        baseWriter.checkOpen();
        enumType.check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(final ICleanUpRegistry registry)
                {
                    baseWriter.createDataSet(objectPath, enumType.getStorageTypeId(), features,
                            dimensions, MDArray.toLong(blockDimensions), enumType.getStorageForm()
                                    .getStorageSize(), registry);
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
        return enumType;
    }

    public HDF5EnumerationType createMDArray(final String objectPath,
            final HDF5EnumerationType enumType, final int[] dimensions,
            final HDF5IntStorageFeatures features)
    {
        assert objectPath != null;
        assert enumType != null;
        assert dimensions != null;

        baseWriter.checkOpen();
        enumType.check(baseWriter.fileId);
        final ICallableWithCleanUp<Void> writeRunnable = new ICallableWithCleanUp<Void>()
            {
                public Void call(final ICleanUpRegistry registry)
                {
                    if (features.requiresChunking())
                    {
                        final long[] nullDimensions = new long[dimensions.length];
                        baseWriter.createDataSet(objectPath, enumType.getStorageTypeId(), features,
                                nullDimensions, MDArray.toLong(dimensions), enumType
                                        .getStorageForm().getStorageSize(), registry);
                    } else
                    {
                        baseWriter.createDataSet(objectPath, enumType.getStorageTypeId(), features,
                                MDArray.toLong(dimensions), null, enumType.getStorageForm()
                                        .getStorageSize(), registry);
                    }
                    return null; // Nothing to return.
                }
            };
        baseWriter.runner.call(writeRunnable);
        return enumType;
    }

}
