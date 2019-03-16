/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2018-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.repository.vgo.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.goodies.common.Loggers;

import org.slf4j.Logger;

/**
 * Extracts a file from a zip image
 *
 * @since 0.0.2
 */
public class CompressedContentExtractor
    extends ComponentSupport
{
  private static final Logger logger = Loggers.getLogger(CompressedContentExtractor.class);

  /**
   * Extracrts a file from a zip image
   * @param projectAsStream zip file as a stream
   * @param fileName file to extract
   * @return stream of extracted file
   */
  public static InputStream extractFile(final InputStream projectAsStream, final String fileName) {
    try (ZipInputStream zipInputStream = new ZipInputStream(projectAsStream)) {
      ZipEntry nextEntry = zipInputStream.getNextEntry();
      while (nextEntry != null && !nextEntry.getName().endsWith(fileName)) {
        zipInputStream.closeEntry();
        nextEntry = zipInputStream.getNextEntry();
      }

      if (nextEntry != null) {
        ByteArrayOutputStream outputStream = extractEntry(zipInputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
      }
    }
    catch (IOException e) {
      logger.warn("Unable to uncompress zip", e);
    }
    return null;
  }

  private static ByteArrayOutputStream extractEntry(final ZipInputStream zipInputStream) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] bytes = new byte[1024];
    while(true) {
      int read = zipInputStream.read(bytes);
      if (read < 0) {
        break;
      }
      outputStream.write(bytes, 0, read);
    }
    return outputStream;
  }
}
