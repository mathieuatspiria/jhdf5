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

import ncsa.hdf.hdf5lib.exceptions.HDF5JavaException;

import ch.systemsx.cisd.base.mdarray.MDIntArray;

/**
 * An interface that provides methods for reading <code>int</code> values from HDF5 files.
 * <p>
 * Note: If the values read are unsigned, use the methods in {@link UnsignedIntUtils} to convert 
 * to a larger Java integer type that can hold all values as unsigned.
 * 
 * @author Bernd Rinn
 */
public interface IHDF5IntReader
{
    // /////////////////////
    // Attributes
    // /////////////////////

    /**
     * Reads a <code>int</code> attribute named <var>attributeName</var> from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param attributeName The name of the attribute to read.
     * @return The attribute value read from the data set.
     */
    public int getAttr(String objectPath, String attributeName);

    /**
     * Reads a <code>int[]</code> attribute named <var>attributeName</var> from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param attributeName The name of the attribute to read.
     * @return The attribute value read from the data set.
     */
    public int[] getArrayAttr(String objectPath, String attributeName);

    /**
     * Reads a multi-dimensional array <code>int</code> attribute named <var>attributeName</var>
     * from the data set <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param attributeName The name of the attribute to read.
     * @return The attribute array value read from the data set.
     */
    public MDIntArray getMDArrayAttr(String objectPath,
            String attributeName);

    /**
     * Reads a <code>int</code> matrix attribute named <var>attributeName</var>
     * from the data set <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param attributeName The name of the attribute to read.
     * @return The attribute matrix value read from the data set.
     */
    public int[][] getMatrixAttr(String objectPath, String attributeName)
            throws HDF5JavaException;

    // /////////////////////
    // Data Sets
    // /////////////////////

    /**
     * Reads a <code>int</code> value from the data set <var>objectPath</var>. This method 
     * doesn't check the data space but simply reads the first value.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The value read from the data set.
     */
    public int read(String objectPath);

    /**
     * Reads a <code>int</code> array (of rank 1) from the data set <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The data read from the data set.
     */
    public int[] readArray(String objectPath);

    /**
     * Reads a multi-dimensional <code>int</code> array data set <var>objectPath</var>
     * into a given <var>array</var> in memory.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param array The array to read the data into.
     * @param memoryOffset The offset in the array to write the data to.
     * @return The effective dimensions of the block in <var>array</var> that was filled.
     */
    public int[] readToMDArrayWithOffset(String objectPath, 
    				MDIntArray array, int[] memoryOffset);

    /**
     * Reads a block of the multi-dimensional <code>int</code> array data set
     * <var>objectPath</var> into a given <var>array</var> in memory.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param array The array to read the data into.
     * @param blockDimensions The size of the block to read along each axis.
     * @param offset The offset of the block in the data set.
     * @param memoryOffset The offset of the block in the array to write the data to.
     * @return The effective dimensions of the block in <var>array</var> that was filled.
     */
    public int[] readToMDArrayBlockWithOffset(String objectPath,
            MDIntArray array, int[] blockDimensions, long[] offset,
            int[] memoryOffset);

    /**
     * Reads a block from a <code>int</code> array (of rank 1) from the data set 
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSize The block size (this will be the length of the <code>int[]</code> returned
     *            if the data set is long enough).
     * @param blockNumber The number of the block to read (starting with 0, offset: multiply with
     *            <var>blockSize</var>).
     * @return The data read from the data set. The length will be min(size - blockSize*blockNumber,
     *         blockSize).
     */
    public int[] readArrayBlock(String objectPath, int blockSize,
            long blockNumber);

    /**
     * Reads a block from <code>int</code> array (of rank 1) from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSize The block size (this will be the length of the <code>int[]</code>
     *            returned).
     * @param offset The offset of the block in the data set to start reading from (starting with 0).
     * @return The data block read from the data set.
     */
    public int[] readArrayBlockWithOffset(String objectPath, int blockSize,
            long offset);

    /**
     * Reads a <code>int</code> matrix (array of arrays) from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The data read from the data set.
     *
     * @throws HDF5JavaException If the data set <var>objectPath</var> is not of rank 2.
     */
    public int[][] readMatrix(String objectPath) throws HDF5JavaException;

    /**
     * Reads a <code>int</code> matrix (array of arrays) from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSizeX The size of the block in the x dimension.
     * @param blockSizeY The size of the block in the y dimension.
     * @param blockNumberX The block number in the x dimension (offset: multiply with
     *            <code>blockSizeX</code>).
     * @param blockNumberY The block number in the y dimension (offset: multiply with
     *            <code>blockSizeY</code>).
     * @return The data block read from the data set.
     *
     * @throws HDF5JavaException If the data set <var>objectPath</var> is not of rank 2.
     */
    public int[][] readMatrixBlock(String objectPath, int blockSizeX,
            int blockSizeY, long blockNumberX, long blockNumberY) 
            throws HDF5JavaException;

    /**
     * Reads a <code>int</code> matrix (array of arrays) from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockSizeX The size of the block in the x dimension.
     * @param blockSizeY The size of the block in the y dimension.
     * @param offsetX The offset in x dimension in the data set to start reading from.
     * @param offsetY The offset in y dimension in the data set to start reading from.
     * @return The data block read from the data set.
     *
     * @throws HDF5JavaException If the data set <var>objectPath</var> is not of rank 2.
     */
    public int[][] readMatrixBlockWithOffset(String objectPath, 
    				int blockSizeX, int blockSizeY, long offsetX, long offsetY) 
    				throws HDF5JavaException;

    /**
     * Reads a multi-dimensional <code>int</code> array from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @return The data read from the data set.
     */
    public MDIntArray readMDArray(String objectPath);

    /**
     * Reads a multi-dimensional <code>int</code> array from the data set 
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockDimensions The extent of the block in each dimension.
     * @param blockNumber The block number in each dimension (offset: multiply with the
     *            <var>blockDimensions</var> in the according dimension).
     * @return The data block read from the data set.
     */
    public MDIntArray readMDArrayBlock(String objectPath,
    				int[] blockDimensions, long[] blockNumber);

    /**
     * Reads a multi-dimensional <code>int</code> array from the data set
     * <var>objectPath</var>.
     * 
     * @param objectPath The name (including path information) of the data set object in the file.
     * @param blockDimensions The extent of the block in each dimension.
     * @param offset The offset in the data set to start reading from in each dimension.
     * @return The data block read from the data set.
     */
    public MDIntArray readMDArrayBlockWithOffset(String objectPath,
            int[] blockDimensions, long[] offset);
    
    /**
     * Provides all natural blocks of this one-dimensional data set to iterate over.
     * 
     * @see HDF5DataBlock
     * @throws HDF5JavaException If the data set is not of rank 1.
     */
    public Iterable<HDF5DataBlock<int[]>> getArrayNaturalBlocks(
    									String dataSetPath)
            throws HDF5JavaException;

    /**
     * Provides all natural blocks of this multi-dimensional data set to iterate over.
     * 
     * @see HDF5MDDataBlock
     */
    public Iterable<HDF5MDDataBlock<MDIntArray>> getMDArrayNaturalBlocks(
    									String dataSetPath);
}
