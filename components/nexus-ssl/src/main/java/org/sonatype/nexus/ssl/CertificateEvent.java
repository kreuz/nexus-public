/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.ssl;

import java.security.cert.Certificate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Certificate} event.
 *
 * @since 3.1
 */
public abstract class CertificateEvent
{
  private final String alias;

  private final Certificate certificate;

  public CertificateEvent(final String alias, final Certificate certificate) {
    this.alias = checkNotNull(alias);
    this.certificate = checkNotNull(certificate);
  }

  public String getAlias() {
    return alias;
  }

  public Certificate getCertificate() {
    return certificate;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "alias='" + alias + '\'' +
        ", certificate=" + certificate +
        '}';
  }
}
