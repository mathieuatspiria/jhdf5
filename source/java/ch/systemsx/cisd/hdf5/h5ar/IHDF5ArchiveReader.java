/*
 * Copyright 2011 ETH Zuerich, CISD
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

package ch.systemsx.cisd.hdf5.h5ar;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import ch.systemsx.cisd.base.io.IInputStream;

/**
 * An interface for an HDF5 archive reader.
 * 
 * @author Bernd Rinn
 */
public interface IHDF5ArchiveReader extends IHDF5ArchiveInfoProvider
{
    public void close();

    public List<ArchiveEntry> list(String fileOrDir);

    public List<ArchiveEntry> list(String fileOrDir, ListParameters params);

    public IHDF5ArchiveReader list(String fileOrDir, IListEntryVisitor visitor);

    public IHDF5ArchiveReader list(String fileOrDir, IListEntryVisitor visitor,
            ListParameters params);

    public List<ArchiveEntry> test();

    public IHDF5ArchiveReader verifyAgainstFilesystem(String fileOrDir, String rootDirectory,
            IListEntryVisitor visitor);

    public IHDF5ArchiveReader verifyAgainstFilesystem(String fileOrDir, String rootDirectory,
            IListEntryVisitor visitor, VerifyParameters params);

    public List<ArchiveEntry> verifyAgainstFilesystem(String rootDirectory);
    
    public List<ArchiveEntry> verifyAgainstFilesystem(String fileOrDir, String rootDirectory);
    
    public List<ArchiveEntry> verifyAgainstFilesystem(String fileOrDir, String rootDirectory,
            VerifyParameters params);
    
    public IHDF5ArchiveReader extractFile(String path, OutputStream out);

    public byte[] extractFileAsByteArray(String path);

    /**
     * @return If the {@link IErrorStrategy} of the archive reader does not re-throw exceptions, the
     *         return value will be <code>null</code> on errors.
     */
    public IInputStream extractFileAsIInputStream(String path);

    /**
     * @return If the {@link IErrorStrategy} of the archive reader does not re-throw exceptions, the
     *         return value will be <code>null</code> on errors.
     */
    public InputStream extractFileAsInputStream(String path);

    public IHDF5ArchiveReader extractToFilesystem(File root, String path);

    public IHDF5ArchiveReader extractToFilesystem(File root, String path,
            IListEntryVisitor visitorOrNull);

    public IHDF5ArchiveReader extractToFilesystem(File root, String path,
            ArchivingStrategy strategy, IListEntryVisitor visitorOrNull);

}